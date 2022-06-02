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
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * 2D representation of the player (Pac-Man or Ms. Pac-Man).
 * 
 * @author Armin Reichert
 */
public class Player2D extends GameEntity2D {

	public final Pac pac;
	public Map<Direction, TimedSeq<BufferedImage>> munchings;
	public final TimedSeq<BufferedImage> dying;

	public Player2D(Pac pac, GameModel game, Rendering2D r2D) {
		super(game, r2D);
		this.pac = pac;
		munchings = r2D.createPlayerMunchingAnimations();
		dying = r2D.createPlayerDyingAnimation();
	}

	public void reset() {
		munchings.values().forEach(TimedSeq::reset);
		dying.reset();
	}

	public void render(Graphics2D g) {
		BufferedImage sprite = null;
		if (pac.killed) {
			if (dying.hasStarted()) {
				dying.animate();
			}
			sprite = dying.frame();
		} else {
			if (!pac.velocity.equals(V2d.NULL) && !pac.stuck) {
				munchings.get(pac.moveDir()).animate();
			}
			sprite = munchings.get(pac.moveDir()).frame();
		}
		r2D.renderEntity(g, pac, sprite);
	}
}