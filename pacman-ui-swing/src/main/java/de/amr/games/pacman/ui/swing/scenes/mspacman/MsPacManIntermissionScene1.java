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
import de.amr.games.pacman.controller.mspacman.Intermission1Controller;
import de.amr.games.pacman.lib.animation.EntityAnimationSet;
import de.amr.games.pacman.model.common.actors.AnimKeys;
import de.amr.games.pacman.ui.swing.entity.mspacman.Heart2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 1: "They meet".
 * <p>
 * Pac-Man leads Inky and Ms. Pac-Man leads Pinky. Soon, the two Pac-Men are about to collide, they quickly move
 * upwards, causing Inky and Pinky to collide and vanish. Finally, Pac-Man and Ms. Pac-Man face each other at the top of
 * the screen and a big pink heart appears above them. (Played after round 2)
 * 
 * @author Armin Reichert
 */
public class MsPacManIntermissionScene1 extends GameScene {

	private Intermission1Controller sceneController;
	private Intermission1Controller.Context ctx;
	private Heart2D heart2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new Intermission1Controller(gameController);
		ctx = sceneController.context();
	}

	@Override
	public void init() {
		sceneController.restartInState(Intermission1Controller.State.FLAP);
		ctx.flap.setAnimationSet(new FlapAnimations());
		ctx.msPac.setAnimationSet(new PacAnimations(ctx.msPac, r2D));
		ctx.msPac.animationSet().ifPresent(EntityAnimationSet::ensureRunning);
		ctx.pacMan.setAnimationSet(new PacAnimations(ctx.pacMan, r2D));
		var husbandMunching = SpritesheetMsPacMan.get().createHusbandMunchingAnimations(ctx.pacMan);
		ctx.pacMan.animationSet().ifPresent(anims -> anims.put(AnimKeys.PAC_MUNCHING, husbandMunching));
		ctx.pacMan.animationSet().ifPresent(anims -> anims.selectedAnimation().ensureRunning());
		ctx.inky.setAnimationSet(new GhostAnimations(ctx.inky, r2D));
		ctx.pinky.setAnimationSet(new GhostAnimations(ctx.pinky, r2D));
		heart2D = new Heart2D(ctx.heart);
		heart2D.setImage(SpritesheetMsPacMan.get().getHeart());
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		((SpritesheetMsPacMan) r2D).drawFlap(g, ctx.flap);
		r2D.drawPac(g, ctx.msPac);
		r2D.drawPac(g, ctx.pacMan);
		r2D.drawGhost(g, ctx.inky);
		r2D.drawGhost(g, ctx.pinky);
		heart2D.render(g);
	}
}