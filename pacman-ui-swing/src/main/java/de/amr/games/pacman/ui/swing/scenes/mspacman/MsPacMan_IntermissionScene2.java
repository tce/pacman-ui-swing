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
import de.amr.games.pacman.controller.mspacman.Intermission2Controller.Context;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.entity.mspacman.Flap2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intermission scene 2: "The chase".
 * <p>
 * Pac-Man and Ms. Pac-Man chase each other across the screen over and over. After three turns, they both rapidly run
 * from left to right and right to left. (Played after round 5)
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntermissionScene2 extends GameScene {

	private final Intermission2Controller sceneController;
	private final Context context;
	private Player2D msPacMan2D;
	private Player2D pacMan2D;
	private Flap2D flap2D;

	public MsPacMan_IntermissionScene2(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sceneController = new Intermission2Controller(gameController);
		sceneController.playIntermissionSound = () -> SoundManager.get().play(GameSound.INTERMISSION_2);
		sceneController.playFlapAnimation = () -> flap2D.animation.restart();
		context = sceneController.context();
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sceneController.restartInInitialState(Intermission2Controller.State.FLAP);
		flap2D = new Flap2D(context.flap, game, r2D);
		msPacMan2D = new Player2D(context.msPacMan, game, r2D);
		msPacMan2D.munchings.values().forEach(TimedSeq::restart);
		pacMan2D = new Player2D(context.pacMan, game, r2D);
		pacMan2D.munchings = r2D.createSpouseMunchingAnimations();
		pacMan2D.munchings.values().forEach(TimedSeq::restart);
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		flap2D.render(g);
		msPacMan2D.render(g);
		pacMan2D.render(g);
	}
}