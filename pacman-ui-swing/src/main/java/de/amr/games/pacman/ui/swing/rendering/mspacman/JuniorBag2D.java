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

import static de.amr.games.pacman.model.world.PacManGameWorld.TS;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.model.common.GameEntity;
import de.amr.games.pacman.model.mspacman.entities.JuniorBag;

public class JuniorBag2D {

	private final JuniorBag bag;
	private BufferedImage blueBag;
	private BufferedImage junior;

	public JuniorBag2D(JuniorBag bag) {
		this.bag = bag;
	}

	public void setBlueBag(BufferedImage blueBag) {
		this.blueBag = blueBag;
	}

	public void setJunior(BufferedImage junior) {
		this.junior = junior;
	}

	private void drawEntity(Graphics2D g, GameEntity entity, BufferedImage sprite) {
		int dx = -(sprite.getWidth() - TS) / 2, dy = -(sprite.getHeight() - TS) / 2;
		g.drawImage(sprite, (int) (entity.position.x + dx), (int) (entity.position.y + dy), null);
	}

	public void render(Graphics2D g) {
		if (bag.visible) {
			drawEntity(g, bag, bag.open ? junior : blueBag);
		}
	}
}