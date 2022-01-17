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
package de.amr.games.pacman.ui.swing.shell;

import static de.amr.games.pacman.lib.Logging.log;
import static de.amr.games.pacman.model.common.GameVariant.MS_PACMAN;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.PacManGameState;
import de.amr.games.pacman.controller.event.DefaultPacManGameEventHandler;
import de.amr.games.pacman.controller.event.PacManGameEvent;
import de.amr.games.pacman.controller.event.PacManGameStateChangeEvent;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.ui.PacManGameUI;
import de.amr.games.pacman.ui.swing.app.GameLoop;
import de.amr.games.pacman.ui.swing.assets.AssetLoader;
import de.amr.games.pacman.ui.swing.rendering.common.Debug;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.ScenesMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.pacman.ScenesPacMan;

/**
 * A Swing implementation of the Pac-Man game UI interface.
 * 
 * @author Armin Reichert
 */
public class PacManGameUI_Swing implements PacManGameUI, DefaultPacManGameEventHandler {

	private final GameLoop gameLoop;
	private final PacManGameController gameController;
	private final Dimension unscaledSize;
	private final V2i scaledSize;
	private final double scaling;
	private final JFrame window;
	private final Timer titleUpdateTimer;
	private final Canvas canvas;
	private final FlashMessageDisplay flashMessageDisplay = new FlashMessageDisplay(ScenesPacMan.UNSCALED_SIZE);

	public final Keyboard keyboard;

	private GameScene currentGameScene;

	public PacManGameUI_Swing(GameLoop gameLoop, PacManGameController controller, double height) {
		this.gameLoop = gameLoop;
		this.gameController = controller;

		unscaledSize = ScenesPacMan.UNSCALED_SIZE;
		scaling = Math.round(height / unscaledSize.height);
		scaledSize = new V2d(unscaledSize.width, unscaledSize.height).scaled(this.scaling).toV2i();

		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		canvas.setSize(scaledSize.x, scaledSize.y);
		canvas.setFocusable(false);

		window = new JFrame();
		window.setTitle("Swing: Pac-Man");
		window.setBackground(Color.BLACK);
		window.setResizable(false);
		window.setFocusable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setIconImage(AssetLoader.image("/pacman/graphics/pacman.png"));
		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				gameLoop.end();
			}
		});

		window.getContentPane().add(canvas);

		keyboard = new Keyboard(window);

		titleUpdateTimer = new Timer(1000, e -> window.setTitle(String.format("%s (%d fps, JFC Swing)",
				gameController.gameVariant() == MS_PACMAN ? "Ms. Pac-Man" : "Pac-Man", gameLoop.clock.getLastFPS())));

		// start initial game scene
		onPacManGameStateChange(new PacManGameStateChangeEvent(gameController.game(), null, controller.currentStateID));
		show();
	}

	@Override
	public void onGameEvent(PacManGameEvent event) {
		DefaultPacManGameEventHandler.super.onGameEvent(event);
		currentGameScene.onGameEvent(event);
	}

	@Override
	public void onPacManGameStateChange(PacManGameStateChangeEvent e) {
		GameScene newScene = getSceneForGameState(e.newGameState);
		if (newScene == null) {
			throw new IllegalStateException("No scene found for game state " + e.newGameState);
		}
		if (currentGameScene != newScene) {
			if (currentGameScene != null) {
				currentGameScene.end();
			}
			newScene.init(gameController);
			log("Current scene changed from %s to %s", currentGameScene, newScene);
		}
		currentGameScene = newScene;
	}

	private void show() {
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.requestFocus();
		canvas.createBufferStrategy(2);
		moveMousePointerOutOfSight();
		titleUpdateTimer.start();
	}

	private GameScene getSceneForGameState(PacManGameState state) {
		var game = gameController.game();
		var scenes = gameController.gameVariant() == MS_PACMAN ? ScenesMsPacMan.SCENES : ScenesPacMan.SCENES;
		switch (state) {
		case INTRO:
			return scenes.get(0); // intro scene
		case INTERMISSION:
			return scenes.get(game.intermissionNumber(game.levelNumber));
		case INTERMISSION_TEST:
			return scenes.get(gameController.intermissionTestNumber);
		default:
			return scenes.get(4); // play scene
		}
	}

	@Override
	public void onTick() {
		handleNonPlayerKeys();
		if (currentGameScene != null) {
			currentGameScene.update();
		}
		flashMessageDisplay.update();
		EventQueue.invokeLater(this::renderScreen);
	}

	private void renderScreen() {
		BufferStrategy buffers = canvas.getBufferStrategy();
		if (buffers == null) {
			return;
		}
		do {
			do {
				Graphics2D g = (Graphics2D) buffers.getDrawGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				g.scale(scaling, scaling);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				currentGameScene.render(g);
				flashMessageDisplay.render(g);
				g.dispose();
			} while (buffers.contentsRestored());
			buffers.show();
		} while (buffers.contentsLost());
	}

	public void reset() {
		currentGameScene.end();
		ScenesMsPacMan.SOUNDS.stopAll();
		ScenesPacMan.SOUNDS.stopAll();
	}

	@Override
	public void showFlashMessage(double seconds, String message, Object... args) {
		flashMessageDisplay.addMessage(seconds, message, args);
	}

	private void handleNonPlayerKeys() {

		if (keyboard.keyPressed("A")) {
			gameController.setAutoControlled(!gameController.isAutoControlled());
			showFlashMessage(1, "Autopilot %s", gameController.isAutoControlled() ? "on" : "off");
		}

		else if (keyboard.keyPressed("D")) {
			Debug.on = !Debug.on;
			log("UI debug mode is %s", Debug.on ? "on" : "off");
		}

		else if (keyboard.keyPressed("E")) {
			gameController.cheatEatAllPellets();
		}

		else if (keyboard.keyPressed("F")) {
			gameLoop.clock.setTargetFPS(gameLoop.clock.getTargetFPS() != 120 ? 120 : 60);
			showFlashMessage(1, "Speed: %s", gameLoop.clock.getTargetFPS() == 60 ? "Normal" : "Fast");
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (keyboard.keyPressed("I")) {
			gameController.game().player.immune = !gameController.game().player.immune;
			showFlashMessage(1, "Player is %s", gameController.game().player.immune ? "immune" : "vulnerable");
		}

		else if (keyboard.keyPressed("L")) {
			gameController.game().player.lives += 3;
		}

		else if (keyboard.keyPressed("N")) {
			if (gameController.isGameRunning()) {
				gameController.changeState(PacManGameState.LEVEL_COMPLETE);
			}
		}

		else if (keyboard.keyPressed("Q")) {
			if (gameController.currentStateID != PacManGameState.INTRO) {
				reset();
				gameController.changeState(PacManGameState.INTRO);
			}
		}

		else if (keyboard.keyPressed("S")) {
			gameLoop.clock.setTargetFPS(gameLoop.clock.getTargetFPS() != 30 ? 30 : 60);
			showFlashMessage(1, "Speed: %s", gameLoop.clock.getTargetFPS() == 60 ? "Normal" : "Slow");
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (keyboard.keyPressed("V")) {
			if (gameController.currentStateID == PacManGameState.INTRO) {
				gameController.selectGameVariant(gameController.gameVariant().succ());
			}
		}

		else if (keyboard.keyPressed("X")) {
			gameController.cheatKillGhosts();
		}

		else if (keyboard.keyPressed("Z")) {
			gameController.startIntermissionTest();
		}

		else if (keyboard.keyPressed("Space")) {
			gameController.startGame();
		}
	}

	private void moveMousePointerOutOfSight() {
		try {
			Robot robot = new Robot();
			robot.mouseMove(window.getX() + 10, window.getY());
		} catch (AWTException x) {
			x.printStackTrace();
		}
	}
}