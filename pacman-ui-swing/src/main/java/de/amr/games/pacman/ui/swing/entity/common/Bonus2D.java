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
package de.amr.games.pacman.ui.swing.entity.common;

import static de.amr.games.pacman.model.world.World.TS;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.model.pacman.entities.Bonus;
import de.amr.games.pacman.model.pacman.entities.Bonus.BonusState;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Bonus symbol. In Pac-Man game, it resides at a fixed position. In Ms. Pac-Man, it appears at some portal and wanders
 * the maze before it exits the maze through some portal on the other side. Changes to bonus value when consumed.
 * 
 * @author Armin Reichert
 */
public class Bonus2D {

	public Bonus bonus;
	public final Map<Integer, BufferedImage> symbolSprites;
	public final Map<Integer, BufferedImage> numberSprites;
	public final TimedSeq<Integer> jumpAnimation;

	public Bonus2D(Rendering2D rendering) {
		jumpAnimation = rendering.createBonusAnimation();
		symbolSprites = rendering.getSymbolSpritesMap();
		numberSprites = rendering.getBonusNumberSprites();
	}

	public void render(Graphics2D g) {
		BufferedImage sprite = currentSprite();
		if (sprite == null || !bonus.visible) {
			return;
		}
		// Ms. Pac.Man bonus is jumping up and down while wandering the maze
		int jump = jumpAnimation != null ? jumpAnimation.animate() : 0;
		int dx = -(sprite.getWidth() - TS) / 2, dy = -(sprite.getHeight() - TS) / 2;
		g.translate(0, jump);
		g.drawImage(sprite, (int) (bonus.position.x + dx), (int) (bonus.position.y + dy), null);
		g.translate(0, -jump);
	}

	private BufferedImage currentSprite() {
		if (bonus == null) {
			return null;
		}
		if (bonus.state == BonusState.EDIBLE) {
			return symbolSprites.get(bonus.symbol);
		}
		if (bonus.state == BonusState.EATEN) {
			return numberSprites.get(bonus.points);
		}
		return null;
	}
}