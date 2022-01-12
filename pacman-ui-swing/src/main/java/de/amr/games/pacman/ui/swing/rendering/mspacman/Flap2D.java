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
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

public class Flap2D {

	public final Flap flap;
	public final Rendering2D rendering;
	public TimedSequence<BufferedImage> animation;
	public Font font;

	public Flap2D(Flap flap, Rendering2D rendering) {
		this.flap = flap;
		this.rendering = rendering;
		font = rendering.getScoreFont();
		animation = rendering.createFlapAnimation();
	}

	public void render(Graphics2D g) {
		if (flap.visible) {
			g.drawImage(animation.animate(), (int) flap.position.x, (int) flap.position.y, null);
			g.setFont(font);
			g.setColor(new Color(222, 222, 255));
			g.drawString(flap.sceneNumber + "", (int) flap.position.x + 20, (int) flap.position.y + 30);
			g.drawString(flap.sceneTitle, (int) flap.position.x + 40, (int) flap.position.y + 20);
		}
	}
}