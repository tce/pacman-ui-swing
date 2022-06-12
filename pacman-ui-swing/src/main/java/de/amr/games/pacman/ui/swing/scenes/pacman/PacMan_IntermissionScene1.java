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
import de.amr.games.pacman.lib.animation.ThingAnimationCollection;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostAnimationKey;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.pacman.BigPacMan2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * First intermission scene: Blinky chases Pac-Man and is then chased by a huge Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene1 extends GameScene {

	private Intermission1Controller sceneController;
	private Intermission1Controller.Context $;
	private BigPacMan2D bigPacMan2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission1Controller(gameController);
		sceneController.playIntermissionSound = () -> SoundManager.get().loop(GameSound.INTERMISSION_1, 1);
		$ = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.init();
		$.pac.setAnimations(new PacAnimations(r2D));
		$.pac.animations().ifPresent(ThingAnimationCollection::ensureRunning);
		$.blinky.setAnimations(new GhostAnimations(Ghost.RED_GHOST, r2D));
		bigPacMan2D = new BigPacMan2D($.pac, (Rendering2D_PacMan) r2D);
		bigPacMan2D.startMunching();
	}

	@Override
	public void update() {
		sceneController.update();
		$.blinky.animations().get().select(switch ($.blinky.state) {
		case FRIGHTENED -> GhostAnimationKey.ANIM_BLUE;
		case HUNTING_PAC -> GhostAnimationKey.ANIM_COLOR;
		default -> $.blinky.animations().get().selectedKey();
		});
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawGhost(g, $.blinky);
		if (sceneController.state() == Intermission1Controller.State.CHASING_PACMAN) {
			r2D.drawPac(g, $.pac);
		} else {
			bigPacMan2D.render(g);
		}
		r2D.drawLevelCounter(g, game);
	}
}