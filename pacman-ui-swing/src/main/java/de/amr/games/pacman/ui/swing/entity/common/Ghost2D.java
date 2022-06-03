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

import static de.amr.games.pacman.model.common.actors.GhostState.DEAD;
import static de.amr.games.pacman.model.common.actors.GhostState.ENTERING_HOUSE;
import static de.amr.games.pacman.model.common.actors.GhostState.FRIGHTENED;
import static de.amr.games.pacman.model.common.actors.GhostState.LOCKED;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.SpriteAnimation;
import de.amr.games.pacman.lib.SpriteAnimationMap;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * 2D representation of a ghost.
 * 
 * @author Armin Reichert
 */
public class Ghost2D extends GameEntity2D {

	public final Ghost ghost;
	public SpriteAnimationMap<Direction, BufferedImage> animColor;
	public SpriteAnimationMap<Direction, BufferedImage> animEyes;
	public SpriteAnimation<BufferedImage> animFlashing;
	public SpriteAnimation<BufferedImage> animBlue;

	public Ghost2D(Ghost ghost, GameModel game, Rendering2D r2D) {
		super(game, r2D);
		this.ghost = ghost;
		animColor = r2D.createGhostColorAnimation(ghost.id);
		animEyes = r2D.createGhostEyesAnimation();
		animBlue = r2D.createGhostBlueAnimation();
		animFlashing = r2D.createGhostFlashingAnimation();
	}

	public void reset() {
		for (Direction dir : Direction.values()) {
			animColor.get(dir).reset();
			animEyes.get(dir).reset();
		}
		animFlashing.reset();
		animBlue.reset();
	}

	public void render(Graphics2D g) {
		BufferedImage sprite = null;
		final Direction dir = ghost.wishDir();
		if (ghost.bounty > 0) {
			sprite = r2D.getNumberSprite(ghost.bounty);
		} else if (ghost.is(DEAD) || ghost.is(ENTERING_HOUSE)) {
			sprite = animEyes.get(dir).animate();
		} else if (ghost.is(FRIGHTENED)) {
			if (animFlashing.isRunning()) {
				sprite = animFlashing.animate();
			} else {
				sprite = animBlue.animate();
			}
		} else if (ghost.is(LOCKED) && game.pac.hasPower()) {
			if (!animBlue.isRunning()) {
				animBlue.restart();
			}
			sprite = animBlue.animate();
		} else if (ghost.velocity.equals(V2d.NULL)) {
			sprite = animColor.get(ghost.wishDir()).frame();
		} else {
			sprite = animColor.get(dir).animate();
		}
		r2D.drawEntity(g, ghost, sprite);
	}
}