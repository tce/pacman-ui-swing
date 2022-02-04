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

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.controller.event.DefaultGameEventHandler;
import de.amr.games.pacman.controller.event.GameEvent;
import de.amr.games.pacman.controller.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
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
public class PacManGameUI_Swing extends DefaultGameEventHandler {

	private final GameLoop gameLoop;
	private final GameController gameController;
	private final Dimension unscaledSize;
	private final V2i scaledSize;
	private final double scaling;
	private final JFrame window;
	private final Timer titleUpdateTimer;
	private final Canvas canvas;
	private final FlashMessageDisplay flashMessageDisplay = new FlashMessageDisplay(ScenesPacMan.UNSCALED_SIZE);

	public final Keyboard keyboard;

	private GameScene currentGameScene;

	public PacManGameUI_Swing(GameLoop gameLoop, GameController controller, double height) {
		this.gameLoop = gameLoop;
		this.gameController = controller;

		ScenesPacMan.init(this);
		ScenesMsPacMan.init(this);

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
				gameController.gameVariant == MS_PACMAN ? "Ms. Pac-Man" : "Pac-Man", gameLoop.clock.getLastFPS())));

		// start initial game scene
		onGameStateChange(new GameStateChangeEvent(gameController.game, null, controller.state));
		show();
	}

	@Override
	public void onGameEvent(GameEvent event) {
		super.onGameEvent(event);
		currentGameScene.onGameEvent(event);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
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

	private GameScene getSceneForGameState(GameState state) {
		var game = gameController.game;
		var scenes = gameController.gameVariant == MS_PACMAN ? ScenesMsPacMan.SCENES : ScenesPacMan.SCENES;
		return switch (state) {
		case INTRO -> scenes.get(0); // intro scene
		case INTERMISSION -> scenes.get(game.intermissionNumber(game.levelNumber));
		case INTERMISSION_TEST -> scenes.get(gameController.intermissionTestNumber);
		default -> scenes.get(4); // play scene
		};
	}

	public void update() {
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

	public void showFlashMessage(double seconds, String message, Object... args) {
		flashMessageDisplay.addMessage(seconds, message, args);
	}

	private void handleNonPlayerKeys() {
		final var game = gameController.game;

		if (keyboard.keyPressed("A")) {
			gameController.autoControlled = !gameController.autoControlled;
			showFlashMessage(1, "Autopilot %s", gameController.autoControlled ? "on" : "off");
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
			game.player.immune = !game.player.immune;
			showFlashMessage(1, "Player is %s", game.player.immune ? "immune" : "vulnerable");
		}

		else if (keyboard.keyPressed("L")) {
			game.player.lives += 3;
		}

		else if (keyboard.keyPressed("N")) {
			if (gameController.gameRunning) {
				gameController.changeState(GameState.LEVEL_COMPLETE);
			}
		}

		else if (keyboard.keyPressed("Q")) {
			if (gameController.state != GameState.INTRO) {
				reset();
				gameController.changeState(GameState.INTRO);
			}
		}

		else if (keyboard.keyPressed("S")) {
			gameLoop.clock.setTargetFPS(gameLoop.clock.getTargetFPS() != 30 ? 30 : 60);
			showFlashMessage(1, "Speed: %s", gameLoop.clock.getTargetFPS() == 60 ? "Normal" : "Slow");
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (keyboard.keyPressed("V")) {
			if (gameController.state == GameState.INTRO) {
				gameController.selectGameVariant(gameController.gameVariant.succ());
			}
		}

		else if (keyboard.keyPressed("X")) {
			gameController.cheatKillGhosts();
		}

		else if (keyboard.keyPressed("Z")) {
			gameController.startIntermissionTest();
		}

		else if (keyboard.keyPressed("Space")) {
			gameController.requestGame();
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