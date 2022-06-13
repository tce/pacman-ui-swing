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
package de.amr.games.pacman.ui.swing.shell;

import static de.amr.games.pacman.lib.Logging.log;
import static de.amr.games.pacman.model.common.GameVariant.MS_PACMAN;
import static de.amr.games.pacman.ui.swing.shell.Keyboard.MOD_CTRL;
import static de.amr.games.pacman.ui.swing.shell.Keyboard.MOD_SHIFT;

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
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameEventAdapter;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.swing.app.GameLoop;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.lib.U;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_CreditScene;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene1;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene2;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntermissionScene3;
import de.amr.games.pacman.ui.swing.scenes.mspacman.MsPacMan_IntroScene;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacMan_CreditScene;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacMan_IntermissionScene1;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacMan_IntermissionScene2;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacMan_IntermissionScene3;
import de.amr.games.pacman.ui.swing.scenes.pacman.PacMan_IntroScene;

/**
 * A Swing UI for the Pac-Man / Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class PacManGameUI_Swing implements GameEventAdapter {

	private final GameLoop gameLoop;
	private final GameController gameController;
	private final V2i unscaledSize;
	private final V2i scaledSize;
	private final double scaling;
	private final JFrame window;
	private final Timer titleUpdateTimer;
	private final Canvas canvas;
	private final FlashMessageDisplay flashMessageDisplay;

	private final List<GameScene> gameScenesPacMan = List.of( //
			new PacMan_IntroScene(), //
			new PacMan_CreditScene(), //
			new PacMan_IntermissionScene1(), //
			new PacMan_IntermissionScene2(), //
			new PacMan_IntermissionScene3(), //
			new PlayScene() //
	);

	private final List<GameScene> gameScenesMsPacMan = List.of( //
			new MsPacMan_IntroScene(), //
			new MsPacMan_CreditScene(), //
			new MsPacMan_IntermissionScene1(), //
			new MsPacMan_IntermissionScene2(), //
			new MsPacMan_IntermissionScene3(), //
			new PlayScene()//
	);

	private GameScene currentGameScene;

	public PacManGameUI_Swing(GameLoop gameLoop, GameController controller, double height) {
		this.gameLoop = gameLoop;
		this.gameController = controller;
		this.unscaledSize = ArcadeWorld.SIZE;
		this.scaling = height / unscaledSize.y;
		this.scaledSize = new V2d(unscaledSize.x, unscaledSize.y).scaled(scaling).toV2i();

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
		window.setIconImage(U.image("/pacman/graphics/pacman.png"));
		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				titleUpdateTimer.stop();
				gameLoop.end();
			}
		});
		window.getContentPane().add(canvas);
		window.addKeyListener(Keyboard.theKeyboard.handler);

		titleUpdateTimer = new Timer(1000, e -> window.setTitle(String.format("%s (%d fps, JFC Swing)",
				gameController.game().variant == MS_PACMAN ? "Ms. Pac-Man" : "Pac-Man", gameLoop.clock.getLastFPS())));

		// start initial game scene
		SoundManager.get().selectGameVariant(gameController.game().variant);
		onGameStateChange(new GameStateChangeEvent(gameController.game(), null, controller.state()));
	}

	public void show() {
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		moveMousePointerOutOfSight();
		titleUpdateTimer.start();
	}

	@Override
	public void onGameEvent(GameEvent event) {
		GameEventAdapter.super.onGameEvent(event);
		currentGameScene.onGameEvent(event);
	}

	@Override
	public void onUIForceUpdate(GameEvent e) {
		updateGameScene(gameController.state(), true);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		updateGameScene(e.newGameState, false);
	}

	private void updateGameScene(GameState gameState, boolean forced) {
		var newGameScene = getSceneForGameState(gameState);
		if (newGameScene == null) {
			throw new IllegalStateException("No scene found for game state " + gameState);
		}
		if (currentGameScene != newGameScene || forced) {
			if (currentGameScene != null) {
				currentGameScene.end();
			}
			newGameScene.setContext(gameController);
			newGameScene.init();
			log("Current scene changed from %s to %s", currentGameScene, newGameScene);
		}
		SoundManager.get().selectGameVariant(gameController.game().variant);
		currentGameScene = newGameScene;
	}

	private GameScene getSceneForGameState(GameState state) {
		var game = gameController.game();
		var scenes = game.variant == MS_PACMAN ? gameScenesMsPacMan : gameScenesPacMan;
		return switch (state) {
		case INTRO -> scenes.get(0);
		case CREDIT -> scenes.get(1);
		case INTERMISSION -> scenes.get(1 + game.intermissionNumber(game.level.number));
		case INTERMISSION_TEST -> scenes.get(1 + game.intermissionTestNumber);
		default -> scenes.get(5);
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
			canvas.createBufferStrategy(2);
			return;
		}
		do {
			do {
				Graphics2D g = (Graphics2D) buffers.getDrawGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				g.scale(scaling, scaling);
				Graphics2D smooth = createSmoothGraphics(g);
				currentGameScene.render(smooth);
				smooth.dispose();
				flashMessageDisplay.render(g);
				g.dispose();
			} while (buffers.contentsRestored());
			buffers.show();
		} while (buffers.contentsLost());
	}

	private Graphics2D createSmoothGraphics(Graphics2D g) {
		Graphics2D smooth = (Graphics2D) g.create();
		smooth.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		smooth.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		smooth.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		smooth.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		return smooth;
	}

	public void showFlashMessage(double seconds, String message, Object... args) {
		flashMessageDisplay.addMessage(seconds, message, args);
	}

	private void handleNonPlayerKeys() {
		var game = gameController.game();

		if (Keyboard.keyPressed("A")) {
			game.autoControlled = !game.autoControlled;
			showFlashMessage(1, "Autopilot %s", game.autoControlled ? "on" : "off");
		}

		else if (Keyboard.keyPressed(MOD_CTRL, "D")) {
			DebugDraw.on = !DebugDraw.on;
			log("UI debug mode is %s", DebugDraw.on ? "on" : "off");
		}

		else if (Keyboard.keyPressed("E")) {
			gameController.state().cheatEatAllPellets(game);
		}

		else if (Keyboard.keyPressed("I")) {
			gameController.toggleIsPacImmune();
			showFlashMessage(1, "Player is %s", game.isPacImmune ? "immune" : "vulnerable");
		}

		else if (Keyboard.keyPressed("L")) {
			if (game.playing) {
				game.lives += 3;
			}
		}

		else if (Keyboard.keyPressed("N")) {
			if (game.playing) {
				gameController.changeState(GameState.LEVEL_COMPLETE);
			}
		}

		else if (Keyboard.keyPressed("Q")) {
			restartIntroScene();
		}

		else if (Keyboard.keyPressed(MOD_CTRL, "S")) {
			int fps = gameLoop.clock.getTargetFPS() + 10;
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(2, "Target FPS set to %s Hz", fps);
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (Keyboard.keyPressed(MOD_CTRL | MOD_SHIFT, "S")) {
			int fps = gameLoop.clock.getTargetFPS() - 10;
			fps = Math.max(10, fps);
			gameLoop.clock.setTargetFPS(fps);
			showFlashMessage(2, "Target FPS set to %s Hz", fps);
			log("Clock frequency changed to %d Hz", gameLoop.clock.getTargetFPS());
		}

		else if (Keyboard.keyPressed("V")) {
			gameController.state().selectGameVariant(gameController.game().variant.next());
		}

		else if (Keyboard.keyPressed("X")) {
			gameController.state().cheatKillAllEatableGhosts(gameController.game());
		}

		else if (Keyboard.keyPressed("Z")) {
			gameController.state().startIntermissionTest(game);
		}
	}

	private void restartIntroScene() {
		currentGameScene.end();
		SoundManager.get().stopAll();
		gameController.restartIntro();
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