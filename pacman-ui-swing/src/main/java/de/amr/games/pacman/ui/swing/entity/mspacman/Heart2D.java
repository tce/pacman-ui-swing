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
package de.amr.games.pacman.ui.swing.entity.mspacman;

import static de.amr.games.pacman.model.common.world.World.TS;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.model.common.actors.Entity;

public class Heart2D {

	private final Entity heart;
	private BufferedImage image;

	public Heart2D(Entity heart) {
		this.heart = heart;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void render(Graphics2D g) {
		if (heart.isVisible()) {
			int dx = -(image.getWidth() - TS) / 2;
			int dy = -(image.getHeight() - TS) / 2;
			g.drawImage(image, (int) heart.position().x() + dx, (int) heart.position().y() + dy, null);
		}
	}
}