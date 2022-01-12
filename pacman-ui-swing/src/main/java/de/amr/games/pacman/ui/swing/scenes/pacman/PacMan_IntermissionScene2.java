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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.pacman.Intermission2Controller;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.ui.PacManGameSound;
import de.amr.games.pacman.ui.swing.rendering.common.Ghost2D;
import de.amr.games.pacman.ui.swing.rendering.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Second intermission scene: Blinky pursues Pac but kicks a nail that tears his dress apart.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene2 extends GameScene {

	private class SceneController extends Intermission2Controller {

		public SceneController(PacManGameController gameController) {
			super(gameController);
		}

		@Override
		public void playIntermissionSound() {
			sounds.play(PacManGameSound.INTERMISSION_2);
		}
	}

	private SceneController sceneController;
	private Player2D pacMan2D;
	private Ghost2D blinky2D;
	private TimedSequence<BufferedImage> blinkyStretchedAnimation;
	private TimedSequence<BufferedImage> blinkyDamagedAnimation;

	public PacMan_IntermissionScene2(Dimension size) {
		super(size, ScenesPacMan.RENDERING, ScenesPacMan.SOUNDS);
	}

	@Override
	public void init(PacManGameController gameController) {
		super.init(gameController);

		sceneController = new SceneController(gameController);
		sceneController.init();
		pacMan2D = new Player2D(sceneController.pac, rendering);
		blinky2D = new Ghost2D(sceneController.blinky, rendering);
		blinkyStretchedAnimation = rendering.createBlinkyStretchedAnimation();
		blinkyDamagedAnimation = rendering.createBlinkyDamagedAnimation();
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		Rendering2D_PacMan r = (Rendering2D_PacMan) rendering;
		r.drawLevelCounter(g, gameController.game(), t(25), t(34));
		r.drawNail(g, sceneController.nail);
		pacMan2D.render(g);
		if (sceneController.nailDistance() < 0) {
			blinky2D.render(g);
		} else {
			drawBlinkyStretched(g, sceneController.nail.position, sceneController.nailDistance() / 4);
		}
	}

	private void drawBlinkyStretched(Graphics2D g, V2d nailPosition, int stretching) {
		BufferedImage stretchedDress = blinkyStretchedAnimation.frame(stretching);
		g.drawImage(stretchedDress, (int) (nailPosition.x - 4), (int) (nailPosition.y - 4), null);
		if (stretching < 3) {
			blinky2D.render(g);
		} else {
			BufferedImage blinkyDamaged = blinkyDamagedAnimation.frame(sceneController.blinky.dir() == Direction.UP ? 0 : 1);
			g.drawImage(blinkyDamaged, (int) (sceneController.blinky.position.x - 4),
					(int) (sceneController.blinky.position.y - 4), null);
		}
	}
}