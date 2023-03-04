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

import static de.amr.games.pacman.lib.math.Vector2i.v2i;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.lib.anim.EntityAnimation;
import de.amr.games.pacman.lib.anim.SingleEntityAnimation;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.common.GameModel;
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
		pac.setAnimations(new PacAnimations(pac, r2D));
		pac.animations().ifPresent(anims -> anims.select(GameModel.AK_PAC_MUNCHING));
		pac.animation(GameModel.AK_PAC_MUNCHING).ifPresent(EntityAnimation::restart);
		pac.placeAtTile(v2i(29, 20), 0, 0);
		pac.setMoveDir(Direction.LEFT);
		pac.setPixelSpeed(1.15f);
		pac.show();

		stretched = SpritesheetPacMan.get().createBlinkyStretchedAnimation();
		blinky = new Ghost(Ghost.ID_RED_GHOST, "Blinky");
		blinky.setAnimations(new GhostAnimations(blinky, r2D));
		var damagedBlinkyAnimation = SpritesheetPacMan.get().createBlinkyDamagedAnimation();
		blinky.animations().ifPresent(anims -> anims.put(GameModel.AK_BLINKY_DAMAGED, damagedBlinkyAnimation));
		blinky.animations().ifPresent(anims -> anims.select(GameModel.AK_GHOST_COLOR));
		blinky.animation(GameModel.AK_GHOST_COLOR).ifPresent(EntityAnimation::restart);
		blinky.placeAtTile(v2i(28, 20), 0, 0);
		blinky.setMoveAndWishDir(Direction.LEFT);
		blinky.setPixelSpeed(0);
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
			GameEvents.publishSoundEvent("start_intermission_2");
		} else if (frame == 110) {
			blinky.setPixelSpeed(1.25f);
			blinky.show();
		} else if (frame == 196) {
			blinky.setPixelSpeed(0.17f);
			stretched.setFrameIndex(1);
		} else if (frame == 226) {
			stretched.setFrameIndex(2);
		} else if (frame == 248) {
			blinky.setPixelSpeed(0);
			blinky.animations().ifPresent(anims -> anims.selectedAnimation().get().stop());
			stretched.setFrameIndex(3);
		} else if (frame == 328) {
			stretched.setFrameIndex(4);
		} else if (frame == 329) {
			blinky.animations().ifPresent(anims -> anims.select(GameModel.AK_BLINKY_DAMAGED));
			blinky.animation(GameModel.AK_BLINKY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(0));
		} else if (frame == 389) {
			blinky.animation(GameModel.AK_BLINKY_DAMAGED).ifPresent(damaged -> damaged.setFrameIndex(1));
		} else if (frame == 508) {
			stretched = null;
		} else if (frame == 509) {
			gameController.terminateCurrentState();
			return;
		}
		pac.move();
		pac.animate();
		blinky.move();
		blinky.animate();
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