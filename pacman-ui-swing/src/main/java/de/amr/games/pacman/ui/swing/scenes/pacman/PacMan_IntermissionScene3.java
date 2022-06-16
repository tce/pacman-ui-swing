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
import de.amr.games.pacman.controller.pacman.Intermission3Controller;
import de.amr.games.pacman.lib.animation.SpriteAnimation;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Third intermission scene: Blinky in shred dress chases Pac-Man, comes back half-naked drawing dress over the floor.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene3 extends GameScene {

	private Intermission3Controller sceneController;
	private Intermission3Controller.Context $;
	private SpriteAnimation<BufferedImage> blinkyPatchedAnimation;
	private SpriteAnimation<BufferedImage> blinkyNakedAnimation;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission3Controller(gameController);
		$ = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.init();
		$.pac.setAnimations(new PacAnimations(r2D));
		$.pac.animations().get().ensureRunning();
		blinkyPatchedAnimation = Rendering2D_PacMan.get().createBlinkyPatchedAnimation();
		blinkyPatchedAnimation.restart();
		blinkyNakedAnimation = Rendering2D_PacMan.get().createBlinkyNakedAnimation();
		blinkyNakedAnimation.restart();
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawPac(g, $.pac);
		if (sceneController.state() == Intermission3Controller.State.CHASING) {
			r2D.drawEntity(g, $.blinky, blinkyPatchedAnimation.frame());
			blinkyPatchedAnimation.advance();
		} else {
			r2D.drawEntity(g, $.blinky, blinkyNakedAnimation.frame());
			blinkyNakedAnimation.advance();
		}
		if (game.playing) {
			r2D.drawLevelCounter(g, game);
		}
	}
}