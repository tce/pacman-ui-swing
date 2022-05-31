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
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSeq;
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
	public Map<Direction, TimedSeq<BufferedImage>> animKicking;
	public Map<Direction, TimedSeq<BufferedImage>> animReturningHome;
	public TimedSeq<BufferedImage> animFlashing;
	public TimedSeq<BufferedImage> animFrightened;

	public Ghost2D(Ghost ghost, GameModel game, Rendering2D r2D) {
		super(game, r2D);
		this.ghost = ghost;
		animKicking = r2D.createGhostKickingAnimations(ghost.id);
		animReturningHome = r2D.createGhostReturningHomeAnimations();
		animFrightened = r2D.createGhostFrightenedAnimation();
		animFlashing = r2D.createGhostFlashingAnimation();
	}

	public void reset() {
		for (Direction dir : Direction.values()) {
			animKicking.get(dir).reset();
			animReturningHome.get(dir).reset();
		}
		animFlashing.reset();
		animFrightened.reset();
	}

	public void render(Graphics2D g) {
		BufferedImage sprite = null;
		final Direction dir = ghost.wishDir();
		if (ghost.bounty > 0) {
			sprite = r2D.getBountyNumberSprites().get(ghost.bounty);
		} else if (ghost.is(DEAD) || ghost.is(ENTERING_HOUSE)) {
			sprite = animReturningHome.get(dir).animate();
		} else if (ghost.is(FRIGHTENED)) {
			if (animFlashing.isRunning()) {
				sprite = animFlashing.animate();
			} else {
				sprite = animFrightened.animate();
			}
		} else if (ghost.is(LOCKED) && game.player.hasPower()) {
			if (!animFrightened.isRunning()) {
				animFrightened.restart();
			}
			sprite = animFrightened.animate();
		} else if (ghost.velocity.equals(V2d.NULL)) {
			sprite = animKicking.get(ghost.wishDir()).frame();
		} else {
			sprite = animKicking.get(dir).animate();
		}
		r2D.renderEntity(g, ghost, sprite);
	}
}