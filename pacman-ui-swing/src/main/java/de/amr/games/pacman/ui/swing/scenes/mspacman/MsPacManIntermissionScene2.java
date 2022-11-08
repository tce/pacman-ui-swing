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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.mspacman.Intermission2Controller;
import de.amr.games.pacman.lib.animation.EntityAnimationSet;
import de.amr.games.pacman.model.common.actors.AnimKeys;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 2: "The chase".
 * <p>
 * Pac-Man and Ms. Pac-Man chase each other across the screen over and over. After three turns, they both rapidly run
 * from left to right and right to left. (Played after round 5)
 * 
 * @author Armin Reichert
 */
public class MsPacManIntermissionScene2 extends GameScene {

	private Intermission2Controller sceneController;
	private Intermission2Controller.Context ctx;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission2Controller(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restartInState(Intermission2Controller.State.FLAP);
		ctx.clapperboard.setAnimationSet(new ClapperboardAnimations());
		ctx.msPacMan.setAnimationSet(new PacAnimations(ctx.msPacMan, r2D));
		ctx.msPacMan.animationSet().ifPresent(EntityAnimationSet::ensureRunning);
		ctx.pacMan.setAnimationSet(new PacAnimations(ctx.pacMan, r2D));
		var husbandMunching = SpritesheetMsPacMan.get().createHusbandMunchingAnimations(ctx.pacMan);
		ctx.pacMan.animationSet().ifPresent(anims -> anims.put(AnimKeys.PAC_MUNCHING, husbandMunching));
		ctx.pacMan.animationSet().ifPresent(EntityAnimationSet::ensureRunning);
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		((SpritesheetMsPacMan) r2D).drawFlap(g, ctx.clapperboard);
		r2D.drawPac(g, ctx.msPacMan);
		r2D.drawPac(g, ctx.pacMan);
	}
}