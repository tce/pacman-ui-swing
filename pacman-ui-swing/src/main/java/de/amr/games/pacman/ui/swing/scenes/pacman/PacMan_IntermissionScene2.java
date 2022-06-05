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
import de.amr.games.pacman.lib.GenericAnimation;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Second intermission scene: Blinky pursues Pac but kicks a nail that tears his dress apart.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene2 extends GameScene {

	private final Intermission2Controller sceneController;
	private final Intermission2Controller.Context context;

	private Pac2D pacMan2D;
	private Ghost2D blinky2D;
	private GenericAnimation<BufferedImage> blinkyStretchedAnimation;
	private GenericAnimation<BufferedImage> blinkyDamagedAnimation;

	public PacMan_IntermissionScene2(GameController gameController) {
		super(gameController);
		sceneController = new Intermission2Controller(gameController);
		sceneController.playIntermissionSound = () -> SoundManager.get().play(GameSound.INTERMISSION_2);
		context = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.init();
		pacMan2D = new Pac2D(context.pac, game, new PacAnimationSet(r2D));
		blinky2D = new Ghost2D(context.blinky, game, new GhostAnimationSet(Ghost.RED_GHOST, r2D));
		blinkyStretchedAnimation = Rendering2D_PacMan.get().createBlinkyStretchedAnimation();
		blinkyDamagedAnimation = Rendering2D_PacMan.get().createBlinkyDamagedAnimation();
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		Rendering2D_PacMan r = (Rendering2D_PacMan) r2D;
		r.drawLevelCounter(g, gameController.game());
		r.drawNail(g, context.nail);
		pacMan2D.render(g, r2D);
		if (sceneController.nailDistance() < 0) {
			blinky2D.render(g, r2D);
		} else {
			drawBlinkyStretched(g, context.nail.position, sceneController.nailDistance() / 4);
		}
	}

	private void drawBlinkyStretched(Graphics2D g, V2d nailPosition, int stretching) {
		BufferedImage stretchedDress = blinkyStretchedAnimation.frame(stretching);
		g.drawImage(stretchedDress, (int) (nailPosition.x - 4), (int) (nailPosition.y - 4), null);
		if (stretching < 3) {
			blinky2D.render(g, r2D);
		} else {
			BufferedImage blinkyDamaged = blinkyDamagedAnimation.frame(context.blinky.moveDir() == Direction.UP ? 0 : 1);
			g.drawImage(blinkyDamaged, (int) (context.blinky.position.x - 4), (int) (context.blinky.position.y - 4), null);
		}
	}
}