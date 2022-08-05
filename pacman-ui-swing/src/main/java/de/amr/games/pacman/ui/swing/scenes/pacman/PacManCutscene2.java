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
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.EntityAnimation;
import de.amr.games.pacman.lib.animation.SingleEntityAnimation;
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
public class PacManCutscene2 extends GameScene {

	private static final String ANIM_KEY_DAMAGED = "damaged";

	private int initialDelay;
	private int frame;
	private Pac pac;
	private Ghost blinky;
	private SingleEntityAnimation<BufferedImage> stretched;

	@Override
	public void init() {
		frame = -1;
		initialDelay = 120;

		pac = new Pac("Pac-Man");
		pac.setAnimationSet(new PacAnimations(pac, r2D));
		pac.animationSet().ifPresent(anims -> anims.select(AnimKeys.PAC_MUNCHING));
		pac.animation(AnimKeys.PAC_MUNCHING).ifPresent(EntityAnimation::restart);
		pac.placeAtTile(v(29, 20), 0, 0);
		pac.setMoveDir(Direction.LEFT);
		pac.setAbsSpeed(1.15);
		pac.show();

		stretched = SpritesheetPacMan.get().createBlinkyStretchedAnimation();
		blinky = new Ghost(Ghost.RED_GHOST, "Blinky");
		blinky.setAnimationSet(new GhostAnimations(blinky, r2D));
		var damaged = SpritesheetPacMan.get().createBlinkyDamagedAnimation();
		blinky.animationSet().ifPresent(anims -> anims.put(ANIM_KEY_DAMAGED, damaged));
		blinky.animationSet().ifPresent(anims -> anims.select(AnimKeys.GHOST_COLOR));
		blinky.animation(AnimKeys.GHOST_COLOR).ifPresent(EntityAnimation::restart);
		blinky.placeAtTile(v(28, 20), 0, 0);
		blinky.setMoveAndWishDir(Direction.LEFT);
		blinky.setAbsSpeed(0);
		blinky.hide();
	}

	@Override
	public void update() {
		if (initialDelay > 0) {
			--initialDelay;
			return;
		}
		++frame;
		if (frame == 0) {
			gameController.sounds().play(GameSound.INTERMISSION_1);
		} else if (frame == 110) {
			blinky.setAbsSpeed(1.25);
			blinky.show();
		} else if (frame == 196) {
			blinky.setAbsSpeed(0.17);
			stretched.setFrameIndex(1);
		} else if (frame == 226) {
			stretched.setFrameIndex(2);
		} else if (frame == 248) {
			blinky.setAbsSpeed(0);
			blinky.animationSet().ifPresent(anims -> anims.selectedAnimation().stop());
			stretched.setFrameIndex(3);
		} else if (frame == 328) {
			stretched.setFrameIndex(4);
		} else if (frame == 329) {
			blinky.animationSet().ifPresent(anims -> anims.select(ANIM_KEY_DAMAGED));
			blinky.animation(ANIM_KEY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(0));
		} else if (frame == 389) {
			blinky.animation(ANIM_KEY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(1));
		} else if (frame == 508) {
			stretched = null;
		} else if (frame == 509) {
			gameController.terminateCurrentState();
			return;
		}
		pac.move();
		pac.advanceAnimation();
		blinky.move();
		blinky.advanceAnimation();
	}

	@Override
	public void render(Graphics2D g) {
		if (stretched != null) {
			r2D.drawSprite(g, stretched.frame(), t(14), t(19) + 3);
		}
		r2D.drawGhost(g, blinky);
		r2D.drawPac(g, pac);
	}
}