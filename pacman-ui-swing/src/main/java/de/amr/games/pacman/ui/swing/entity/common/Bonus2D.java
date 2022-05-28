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

import static de.amr.games.pacman.model.common.world.World.TS;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Bonus symbol. In Pac-Man game, it resides at a fixed position. In Ms. Pac-Man, it appears at some portal and wanders
 * the maze before it exits the maze through some portal on the other side. Changes to bonus value when consumed.
 * 
 * @author Armin Reichert
 */
public class Bonus2D extends GameEntity2D {

	private final Bonus bonus;
	public final TimedSeq<Integer> jumpAnimation;

	public Bonus2D(GameModel game, Bonus bonus, Rendering2D r2D) {
		super(game, r2D);
		this.bonus = bonus;
		jumpAnimation = r2D.createBonusAnimation();
	}

	public void render(Graphics2D g) {
		switch (bonus.state()) {
		case INACTIVE -> {
		}
		case EDIBLE -> {
			var sprite = r2D.getSymbolSpritesMap().get(game.level.bonusSymbol);
			// Ms. Pac.Man bonus is jumping up and down while wandering the maze
			int jump = jumpAnimation != null ? jumpAnimation.animate() : 0;
			g.translate(0, jump);
			renderSprite(g, sprite, bonus.position().x, bonus.position().y);
			g.translate(0, -jump);
		}
		case EATEN -> {
			var sprite = r2D.getBonusNumberSprites().get(bonus.value());
			renderSprite(g, sprite, bonus.position().x, bonus.position().y);
		}
		}
	}

	private void renderSprite(Graphics2D g, BufferedImage sprite, double x, double y) {
		int dx = -(sprite.getWidth() - TS) / 2, dy = -(sprite.getHeight() - TS) / 2;
		g.drawImage(sprite, (int) (x + dx), (int) (y + dy), null);
	}
}