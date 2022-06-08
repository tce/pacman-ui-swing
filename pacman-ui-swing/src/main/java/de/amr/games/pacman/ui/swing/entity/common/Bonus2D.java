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
package de.amr.games.pacman.ui.swing.entity.common;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.animation.GenericAnimation;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Bonus symbol. In Pac-Man game, it resides at a fixed position. In Ms. Pac-Man, it appears at some portal and wanders
 * the maze before it exits the maze through some portal on the other side. Changes to bonus value when consumed.
 * 
 * @author Armin Reichert
 */
public class Bonus2D extends GameEntity2D {

	private final GenericAnimation<BufferedImage> symbolAnimation;
	private final GenericAnimation<BufferedImage> valueAnimation;
	private GenericAnimation<Integer> jumpAnimation;

	public Bonus2D(GameModel game, Rendering2D r2D, boolean jumping) {
		super(game);
		symbolAnimation = r2D.createBonusSymbolAnimation();
		valueAnimation = r2D.createBonusValueAnimation();
		if (jumping) {
			jumpAnimation = new GenericAnimation<>(-2, 2);
			jumpAnimation.frameDuration(10);
			jumpAnimation.repeatForever();
		}
	}

	public void startJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.restart();
		}
	}

	public void stopJumping() {
		if (jumpAnimation != null) {
			jumpAnimation.stop();
		}
	}

	@Override
	public void render(Graphics2D g, Rendering2D r2D) {
		var bonus = game.bonus();

		// TODO use animation key...
		switch (bonus.state()) {
		case EDIBLE -> {
			var sprite = symbolAnimation.frame(bonus.symbol());
			if (jumpAnimation != null) {
				int jump = jumpAnimation.animate();
				g.translate(0, jump);
				r2D.drawSpriteCenteredOverBox(g, sprite, bonus.position().x, bonus.position().y);
				g.translate(0, -jump);
			} else {
				r2D.drawSpriteCenteredOverBox(g, sprite, bonus.position().x, bonus.position().y);
			}
		}
		case EATEN -> {
			var sprite = valueAnimation.frame(bonus.symbol());
			r2D.drawSpriteCenteredOverBox(g, sprite, bonus.position().x, bonus.position().y);
		}
		case INACTIVE -> {
		}
		}
	}
}