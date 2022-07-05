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

import static de.amr.games.pacman.lib.V2i.v;

import java.awt.Graphics2D;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.SpriteAnimation;
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

	private static final String ANIMKEY_PATCHED = "patched";
	private static final String ANIMKEY_NAKED = "naked";

	private int initialDelay;
	private int frame;
	private Pac pac;
	private Ghost blinky;

	@Override
	public void init() {
		frame = -1;
		initialDelay = 120;
		pac = new Pac("Pac-Man");
		pac.setAnimations(new PacAnimations(pac, r2D));
		blinky = new Ghost(Ghost.RED_GHOST, "Blinky");
		blinky.setAnimations(new GhostAnimations(blinky, r2D));
		blinky.animations()
				.ifPresent(anims -> anims.put(ANIMKEY_PATCHED, SpritesheetPacMan.get().createBlinkyPatchedAnimation()));
		blinky.animations()
				.ifPresent(anims -> anims.put(ANIMKEY_NAKED, SpritesheetPacMan.get().createBlinkyNakedAnimation()));
	}

	@Override
	public void update() {
		if (initialDelay > 0) {
			--initialDelay;
			return;
		}
		++frame;
		if (frame == 0) {
			gameController.sounds().ifPresent(snd -> snd.loop(GameSound.INTERMISSION_1, 1));
			pac.placeAt(v(29, 20), 0, 0);
			pac.setMoveDir(Direction.LEFT);
			pac.setAbsSpeed(1.25);
			pac.show();
			pac.animations().ifPresent(anims -> anims.select(AnimKeys.PAC_MUNCHING));
			pac.animation(AnimKeys.PAC_MUNCHING).ifPresent(SpriteAnimation::restart);
			blinky.placeAt(v(35, 20), 0, 0);
			blinky.setBothDirs(Direction.LEFT);
			blinky.setAbsSpeed(1.25);
			blinky.show();
			blinky.animations().ifPresent(anims -> anims.select(ANIMKEY_PATCHED));
			blinky.animation(ANIMKEY_PATCHED).ifPresent(SpriteAnimation::restart);
		} else if (frame == 296) {
			blinky.placeAt(v(-1, 20), 0, 0);
			blinky.setBothDirs(Direction.RIGHT);
			blinky.animations().ifPresent(anims -> anims.select(ANIMKEY_NAKED));
			blinky.animations().ifPresent(anims -> anims.selectedAnimation().restart());
		} else if (frame == 516) {
			gameController.state().timer().expire();
			return;
		}
		pac.move();
		pac.animate();
		blinky.move();
		blinky.animate();
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawPac(g, pac);
		r2D.drawGhost(g, blinky);
	}
}