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

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.pacman.Intermission1Controller;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostAnimation;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.entity.pacman.BigPacMan2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * First intermission scene: Blinky chases Pac-Man and is then chased by a huge Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene1 extends GameScene {

	private Intermission1Controller sceneController;
	private Intermission1Controller.Context context;
	private Pac2D pacMan2D;
	private Ghost2D blinky2D;
	private BigPacMan2D bigPacMan2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission1Controller(gameController);
		sceneController.playIntermissionSound = () -> SoundManager.get().loop(GameSound.INTERMISSION_1, 1);
		context = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.init();
		pacMan2D = new Pac2D(context.pac, game, new PacAnimationSet(r2D));
		blinky2D = new Ghost2D(context.blinky, game, new GhostAnimationSet(Ghost.RED_GHOST, r2D));
		bigPacMan2D = new BigPacMan2D(context.pac, (Rendering2D_PacMan) r2D);
		bigPacMan2D.startMunching();
	}

	@Override
	public void update() {
		sceneController.update();
		blinky2D.animations.selectAnimation(switch (context.blinky.state) {
		case FRIGHTENED -> GhostAnimation.BLUE;
		case HUNTING_PAC -> GhostAnimation.COLOR;
		default -> blinky2D.animations.selectedKey();
		});
	}

	@Override
	public void render(Graphics2D g) {
		blinky2D.render(g, r2D);
		if (sceneController.state() == Intermission1Controller.State.CHASING_PACMAN) {
			pacMan2D.render(g, r2D);
		} else {
			bigPacMan2D.render(g);
		}
		r2D.drawLevelCounter(g, game);
	}
}