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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.pacman.Intermission2Controller;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.animation.SimpleThingAnimation;
import de.amr.games.pacman.lib.animation.ThingAnimationCollection;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Second intermission scene: Blinky pursues Pac but kicks a nail that tears his dress apart.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene2 extends GameScene {

	private Intermission2Controller sceneController;
	private Intermission2Controller.Context $;
	private SimpleThingAnimation<BufferedImage> blinkyStretchedAnimation;
	private SimpleThingAnimation<BufferedImage> blinkyDamagedAnimation;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission2Controller(gameController);
		$ = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.init();
		$.pac.setAnimations(new PacAnimations(r2D));
		$.pac.animations().get().ensureRunning();
		$.blinky.setAnimations(new GhostAnimations(Ghost.RED_GHOST, r2D));
		$.blinky.animations().ifPresent(ThingAnimationCollection::restart);
		blinkyStretchedAnimation = Rendering2D_PacMan.get().createBlinkyStretchedAnimation();
		blinkyStretchedAnimation.restart();
		blinkyDamagedAnimation = Rendering2D_PacMan.get().createBlinkyDamagedAnimation();
		blinkyDamagedAnimation.restart();
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		Rendering2D_PacMan sspm = (Rendering2D_PacMan) r2D;
		sspm.drawNail(g, $.nail);
		r2D.drawPac(g, $.pac);
		if (sceneController.nailDistance() < 0) {
			r2D.drawGhost(g, $.blinky);
		} else {
			drawBlinkyStretched(g, $.nail.position, sceneController.nailDistance() / 4);
		}
		if (game.playing) {
			r2D.drawLevelCounter(g, game);
		}
	}

	private void drawBlinkyStretched(Graphics2D g, V2d nailPosition, int stretching) {
		BufferedImage stretchedDress = blinkyStretchedAnimation.frame(stretching);
		g.drawImage(stretchedDress, (int) (nailPosition.x - 4), (int) (nailPosition.y - 4), null);
		if (stretching < 3) {
			r2D.drawGhost(g, $.blinky);
		} else {
			BufferedImage blinkyDamaged = blinkyDamagedAnimation.frame($.blinky.moveDir() == Direction.UP ? 0 : 1);
			g.drawImage(blinkyDamaged, (int) ($.blinky.position.x - 4), (int) ($.blinky.position.y - 4), null);
		}
	}
}