/*
MIT License

Copyright (c) 2021 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package de.amr.games.pacman.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;

import de.amr.games.pacman.lib.TickTimer;

/**
 * Implements display of flash messages which disappear after a defined timespan.
 * 
 * @author Armin Reichert
 */
public class FlashMessageDisplay {

	static class FlashMessage {

		private final TickTimer timer = new TickTimer(this.toString());
		public final String text;

		public FlashMessage(String text, long ticks) {
			this.text = text;
			timer.reset(ticks);
		}
	}

	private final Dimension unscaledSize;
	private final Deque<FlashMessage> flashMessageQ = new ArrayDeque<>();

	public FlashMessageDisplay(Dimension unscaledSize) {
		this.unscaledSize = unscaledSize;
	}

	public void update() {
		FlashMessage message = flashMessageQ.peek();
		if (message != null) {
			if (!message.timer.isRunning()) {
				message.timer.start();
			}
			message.timer.tick();
			if (message.timer.hasExpired()) {
				flashMessageQ.remove();
			}
		}
	}

	public void render(Graphics2D g) {
		FlashMessage message = flashMessageQ.peek();
		if (message != null) {
			double alpha = Math.cos(Math.PI * message.timer.ticked() / (2 * message.timer.duration()));
			g.setColor(Color.BLACK);
			g.fillRect(0, unscaledSize.height - 16, unscaledSize.width, 16);
			g.setColor(new Color(1, 1, 0, (float) alpha));
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(message.text, (unscaledSize.width - g.getFontMetrics().stringWidth(message.text)) / 2,
					unscaledSize.height / 2);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	public void addMessage(double seconds, String message, Object... args) {
		flashMessageQ.add(new FlashMessage(String.format(message, args), (long) (60 * seconds)));

	}
}