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
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
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
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.Debug;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.ScenesMsPacMan;
import de.amr.games.pacman.ui.swing.scenes.pacman.ScenesPacMan;

/**
 * A Swing UI for the Pac-Man / Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class PacManGameUI_Swing extends DefaultGameEventHandler {

	private final GameLoop gameLoop;
	private final GameController gameController;
	private final V2i unscaledSize;
	private final V2i scaledSize;
	private final double scaling;
	private final JFrame window;
	private final Timer titleUpdateTimer;
	private final Canvas canvas;
	private final FlashMessageDisplay flashMessageDisplay;
	private final ScenesMsPacMan scenesMsPacMan;
	private final ScenesPacMan scenesPacMan;

	public final Keyboard keyboard;

	private GameScene currentGameScene;

	public PacManGameUI_Swing(GameLoop gameLoop, GameController controller, double height) {
		this.gameLoop = gameLoop;
		this.gameController = controller;
		this.unscaledSize = new V2i(t(28), t(36));
		this.scaling = height / unscaledSize.y;
		this.scaledSize = new V2d(unscaledSize.x, unscaledSize.y).scaled(scaling).toV2i();

		scenesMsPacMan = new ScenesMsPacMan(gameController, unscaledSize);
		scenesPacMan = new ScenesPacMan(gameController, unscaledSize);

		flashMessageDisplay = new FlashMessageDisplay(unscaledSize);

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
		SoundManager.get().selectGameVariant(gameController.gameVariant());
		onGameStateChange(new GameStateChangeEvent(gameController.game(), null, controller.state));
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
			newScene.init(gameController.game());
			log("Current scene changed from %s to %s", currentGameScene, newScene);
		}
		SoundManager.get().selectGameVariant(gameController.gameVariant());
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
		var game = gameController.game();
		var scenes = gameController.gameVariant() == MS_PACMAN ? scenesMsPacMan.gameScenes : scenesPacMan.gameScenes;
		return switch (state) {
		case INTRO -> scenes.get(0); // intro scene
		case INTERMISSION -> scenes.get(game.intermissionNumber(game.levelNumber));
		case INTERMISSION_TEST -> scenes.get(game.intermissionTestNumber);
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
		SoundManager.get().stopAll();
	}

	public void showFlashMessage(double seconds, String message, Object... args) {
		flashMessageDisplay.addMessage(seconds, message, args);
	}

	private void handleNonPlayerKeys() {
		final var game = gameController.game();

		if (keyboard.pressed("A")) {
			gameController.playerAutomove = !gameController.playerAutomove;
			showFlashMessage(1, "Autopilot %s", gameController.playerAutomove ? "on" : "off");
		}

		else if (keyboard.pressed(Keyboard.CONTROL, "D")) {
			Debug.on = !Debug.on;
			log("UI debug mode is %s", Debug.on ? "on" : "off");
		}

		else if (keyboard.pressed("E")) {
			game.cheatEatAllPellets();
		}

		else if (keyboard.pressed("I")) {
			gameController.playerImmune = !gameController.playerImmune;
			showFlashMessage(1, "Player is %s", gameController.playerImmune ? "immune" : "vulnerable");
		}

		else if (keyboard.pressed("L")) {
			if (game.running) {
				game.player.lives += 3;
			}
		}

		else if (keyboard.pressed("N")) {
			if (game.running) {
				gameController.changeState(GameState.LEVEL_COMPLETE);
			}
		}

		else if (keyboard.pressed("Q")) {
			if (gameController.state != GameState.INTRO) {
				reset();
				gameController.changeState(GameState.INTRO);
			}
		}

		else if (keyboard.pressed(Keyboard.CONTROL, "S")) {
			int fps = gameLoop.clock.getTargetFPS() + 10;
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(1, "Target FPS set to %s Hz", fps);
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (keyboard.pressed(Keyboard.CONTROL | Keyboard.SHIFT, "S")) {
			int fps = gameLoop.clock.getTargetFPS() - 10;
			fps = Math.max(10, fps);
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(1, "Target FPS set to %s Hz", fps);
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (keyboard.pressed("V")) {
			if (gameController.state == GameState.INTRO) {
				gameController.selectGameVariant(gameController.gameVariant().succ());
			}
		}

		else if (keyboard.pressed("X")) {
			gameController.cheatKillGhosts();
		}

		else if (keyboard.pressed("Z")) {
			gameController.startIntermissionTest();
		}

		else if (keyboard.pressed("Space")) {
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