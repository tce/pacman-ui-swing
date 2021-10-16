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
package de.amr.games.pacman.ui.swing.rendering.mspacman;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.mspacman.entities.Flap;

public class Flap2D {

	private final Flap flap;
	private TimedSequence<BufferedImage> animation;
	private Font font;

	public Flap2D(Flap flap) {
		this.flap = flap;
	}

	public void setAnimation(TimedSequence<BufferedImage> animation) {
		this.animation = animation;
	}

	public TimedSequence<BufferedImage> getAnimation() {
		return animation;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Flap getFlap() {
		return flap;
	}

	public void render(Graphics2D g) {
		if (flap.isVisible()) {
			g.drawImage(animation.animate(), (int) flap.position().x, (int) flap.position().y, null);
			g.setFont(font);
			g.setColor(new Color(222, 222, 225, 192));
			g.drawString(flap.sceneNumber + "", (int) flap.position().x + 20, (int) flap.position().y + 30);
			g.drawString(flap.sceneTitle, (int) flap.position().x + 40, (int) flap.position().y + 20);
		}
	}
}