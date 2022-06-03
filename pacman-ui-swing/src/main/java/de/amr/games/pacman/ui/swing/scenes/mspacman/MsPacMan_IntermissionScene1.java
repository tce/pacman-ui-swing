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
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.entity.mspacman.Flap2D;
import de.amr.games.pacman.ui.swing.entity.mspacman.Heart2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.MsPacMansHusbandAnimations;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;
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
public class MsPacMan_IntermissionScene1 extends GameScene {

	private final Intermission1Controller sceneController;
	private final Intermission1Controller.Context context;
	private Pac2D msPacMan2D;
	private Pac2D pacMan2D;
	private Ghost2D inky2D;
	private Ghost2D pinky2D;
	private Flap2D flap2D;
	private Heart2D heart2D;

	public MsPacMan_IntermissionScene1(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sceneController = new Intermission1Controller(gameController);
		sceneController.playIntermissionSound = () -> SoundManager.get().play(GameSound.INTERMISSION_1);
		sceneController.playFlapAnimation = () -> flap2D.animation.restart();
		context = sceneController.context();
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sceneController.restartInInitialState(Intermission1Controller.State.FLAP);

		flap2D = new Flap2D(context.flap, game);
		msPacMan2D = new Pac2D(context.msPac, game, new PacAnimations(r2D));
		pacMan2D = new Pac2D(context.pacMan, game, new MsPacMansHusbandAnimations(Rendering2D_MsPacMan.get()));
		inky2D = new Ghost2D(context.inky, game, new GhostAnimations(Ghost.CYAN_GHOST, r2D));
		pinky2D = new Ghost2D(context.pinky, game, new GhostAnimations(Ghost.PINK_GHOST, r2D));
		heart2D = new Heart2D(context.heart);
		heart2D.setImage(Rendering2D_MsPacMan.get().getHeart());
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		flap2D.render(g, r2D);
		msPacMan2D.render(g, r2D);
		pacMan2D.render(g, r2D);
		inky2D.render(g, r2D);
		pinky2D.render(g, r2D);
		heart2D.render(g);
	}
}