/*
MIT License

Copyright (c) 2022 Armin Reichert

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

package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.lib.V2i.v2i;

import java.awt.Graphics2D;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.EntityAnimation;
import de.amr.games.pacman.model.common.GameSound;
import de.amr.games.pacman.model.common.actors.AnimKeys;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * @author Armin Reichert
 */
public class PacManCutscene3 extends GameScene {

	private int initialDelay;
	private int frame;
	private Pac pac;
	private Ghost blinky;

	@Override
	public void init() {
		frame = -1;
		initialDelay = 120;
		pac = new Pac("Pac-Man");
		pac.setAnimationSet(new PacAnimations(pac, r2D));
		blinky = new Ghost(Ghost.ID_RED_GHOST, "Blinky");
		blinky.setAnimationSet(new GhostAnimations(blinky, r2D));
		blinky.animationSet()
				.ifPresent(anims -> anims.put(AnimKeys.BLINKY_PATCHED, SpritesheetPacMan.get().createBlinkyPatchedAnimation()));
		blinky.animationSet()
				.ifPresent(anims -> anims.put(AnimKeys.BLINKY_NAKED, SpritesheetPacMan.get().createBlinkyNakedAnimation()));
	}

	@Override
	public void update() {
		if (initialDelay > 0) {
			--initialDelay;
			return;
		}
		++frame;
		if (frame == 0) {
			gameController.sounds().loop(GameSound.INTERMISSION_1, 1);
			pac.placeAtTile(v2i(29, 20), 0, 0);
			pac.setMoveDir(Direction.LEFT);
			pac.setAbsSpeed(1.25);
			pac.show();
			pac.animationSet().ifPresent(anims -> anims.select(AnimKeys.PAC_MUNCHING));
			pac.animation(AnimKeys.PAC_MUNCHING).ifPresent(EntityAnimation::restart);
			blinky.placeAtTile(v2i(35, 20), 0, 0);
			blinky.setMoveAndWishDir(Direction.LEFT);
			blinky.setAbsSpeed(1.25);
			blinky.show();
			blinky.animationSet().ifPresent(anims -> anims.select(AnimKeys.BLINKY_PATCHED));
			blinky.animation(AnimKeys.BLINKY_PATCHED).ifPresent(EntityAnimation::restart);
		} else if (frame == 296) {
			blinky.placeAtTile(v2i(-1, 20), 0, 0);
			blinky.setMoveAndWishDir(Direction.RIGHT);
			blinky.animationSet().ifPresent(anims -> anims.select(AnimKeys.BLINKY_NAKED));
			blinky.animationSet().ifPresent(anims -> anims.selectedAnimation().get().restart());
		} else if (frame == 516) {
			gameController.terminateCurrentState();
			return;
		}
		pac.move();
		pac.updateAnimation();
		blinky.move();
		blinky.updateAnimation();
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawPac(g, pac);
		r2D.drawGhost(g, blinky);
	}
}