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
package de.amr.games.pacman.ui.swing.entity.pacman;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.SpriteAnimation;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;

/**
 * The big Pac-Man from the first intermission scene in Pac-Man.
 * 
 * @author Armin Reichert
 */
public class BigPacMan2D {

	private final Rendering2D_PacMan rendering;
	private final Pac pacMan;
	public final SpriteAnimation<BufferedImage> munchingAnimation;

	public BigPacMan2D(Pac pacMan, Rendering2D_PacMan rendering) {
		this.pacMan = pacMan;
		this.rendering = rendering;
		munchingAnimation = rendering.createBigPacManMunchingAnimation();
	}

	public void render(Graphics2D g_) {
		Graphics2D g = (Graphics2D) g_.create();
		BufferedImage sprite = munchingAnimation.animate();
		// lift it up such that it sits on the ground instead of being vertically
		// centered to the ground
		g.translate(0, -sprite.getHeight() / 2 + 8);
		rendering.drawEntity(g, pacMan, sprite);
		g.dispose();
	}
}