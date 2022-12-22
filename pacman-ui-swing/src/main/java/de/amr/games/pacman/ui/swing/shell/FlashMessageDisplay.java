/*
MIT License

Copyright (c) 2021-22 Armin Reichert

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
package de.amr.games.pacman.ui.swing.shell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;

import de.amr.games.pacman.lib.math.Vector2i;

/**
 * Implements display of flash messages which disappear after a defined timespan.
 * 
 * @author Armin Reichert
 */
public class FlashMessageDisplay {

	static class FlashMessage {

		public final String text;
		private final long displayTimeMillis;
		private final long createdAt;

		public FlashMessage(String text, double seconds) {
			this.text = text;
			displayTimeMillis = (long) (1000 * seconds);
			createdAt = System.currentTimeMillis();
		}

		public boolean hasExpired() {
			return System.currentTimeMillis() - createdAt > displayTimeMillis;
		}
	}

	private final Vector2i unscaledSize;
	private final Deque<FlashMessage> flashMessageQ = new ArrayDeque<>();

	public FlashMessageDisplay(Vector2i unscaledSize) {
		this.unscaledSize = unscaledSize;
	}

	public void update() {
		var message = flashMessageQ.peek();
		if (message != null && message.hasExpired()) {
			flashMessageQ.remove();
		}
	}

	public void render(Graphics2D g) {
		var message = flashMessageQ.peek();
		if (message != null) {
			double t = ((double) System.currentTimeMillis() - message.createdAt) / message.displayTimeMillis;
			double alpha = Math.abs(Math.cos(0.5 * Math.PI * t));
			g.setColor(Color.BLACK);
			g.fillRect(0, unscaledSize.y() - 16, unscaledSize.x(), 12);
			g.setColor(new Color(0.8f, 0.8f, 0.8f, (float) alpha));
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(message.text, (unscaledSize.x() - g.getFontMetrics().stringWidth(message.text)) / 2,
					unscaledSize.y() / 2);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	public void addMessage(double seconds, String message, Object... args) {
		flashMessageQ.add(new FlashMessage(String.format(message, args), seconds));
	}
}