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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

import static de.amr.games.pacman.model.common.GameVariant.MS_PACMAN;
import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.mspacman.IntroController;
import de.amr.games.pacman.controller.mspacman.IntroController.IntroState;
import de.amr.games.pacman.lib.TickTimer;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.PacManGameUI_Swing;
import de.amr.games.pacman.ui.swing.rendering.common.Ghost2D;
import de.amr.games.pacman.ui.swing.rendering.common.Player2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intro scene of the Ms. Pac-Man game. The ghosts and Ms. Pac-Man are introduced one after another.
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntroScene extends GameScene {

	private IntroController sceneController;
	private Player2D msPacMan2D;
	private List<Ghost2D> ghosts2D;
	private TickTimer boardAnimationTimer = new TickTimer("boardAnimation-timer");

	public MsPacMan_IntroScene(PacManGameController controller, Dimension size) {
		super(controller, size, PacManGameUI_Swing.RENDERING_MS_PACMAN, PacManGameUI_Swing.SOUND.get(MS_PACMAN));
	}

	@Override
	public void init() {
		sceneController = new IntroController(gameController);
		sceneController.init();
		msPacMan2D = new Player2D(sceneController.msPacMan);
		msPacMan2D.setMunchingAnimations(rendering.createPlayerMunchingAnimations());
		msPacMan2D.getMunchingAnimations().values().forEach(TimedSequence::restart);
		ghosts2D = Stream.of(sceneController.ghosts).map(Ghost2D::new).collect(Collectors.toList());
		ghosts2D.forEach(ghost2D -> {
			ghost2D.setKickingAnimations(rendering.createGhostKickingAnimations(ghost2D.ghost.id));
			ghost2D.getKickingAnimations().values().forEach(TimedSequence::restart);
		});
		boardAnimationTimer.reset();
		boardAnimationTimer.start();
	}

	@Override
	public void update() {
		sceneController.update();
		boardAnimationTimer.tick();
	}

	@Override
	public void end() {
	}

	@Override
	public void render(Graphics2D g_) {
		Graphics2D g = (Graphics2D) g_.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendering.drawScore(g, gameController.game(), true);
		g.setFont(rendering.getScoreFont());
		g.setColor(Color.ORANGE);
		g.drawString("\"MS PAC-MAN\"", t(8), t(sceneController.tileBoardTopLeft.y - 3));
		drawAnimatedBoard(g, 32, 16);
		if (sceneController.currentStateID == IntroState.PRESENTING_GHOST) {
			drawPresentingGhost(g, sceneController.ghosts[sceneController.currentGhostIndex]);
		} else if (sceneController.currentStateID == IntroState.PRESENTING_MSPACMAN) {
			drawStarringMsPacMan(g);
		} else if (sceneController.currentStateID == IntroState.WAITING_FOR_GAME) {
			drawStarringMsPacMan(g);
			drawPointsAnimation(g, 26);
			drawPressKeyToStart(g, 32);
		}
		ghosts2D.forEach(ghost2D -> ghost2D.render(g));
		msPacMan2D.render(g);
		g.dispose();
	}

	private void drawPresentingGhost(Graphics2D g, Ghost ghost) {
		int top = sceneController.tileBoardTopLeft.y;
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		if (ghost == sceneController.ghosts[0]) {
			g.drawString("WITH", t(8), t(top + 3));
		}
		g.setColor(ghost.id == 0 ? Color.RED : ghost.id == 1 ? Color.PINK : ghost.id == 2 ? Color.CYAN : Color.ORANGE);
		g.drawString(ghost.name.toUpperCase(), t(13 - ghost.name.length() / 2), t(top + 6));
	}

	private void drawStarringMsPacMan(Graphics2D g) {
		int top = sceneController.tileBoardTopLeft.y;
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		g.drawString("STARRING", t(8), t(top + 3));
		g.setColor(Color.YELLOW);
		g.drawString("MS PAC-MAN", t(8), t(top + 6));
	}

	private void drawAnimatedBoard(Graphics2D g, int numDotsX, int numDotsY) {
		long time = boardAnimationTimer.ticked();
		int light = (int) (time / 2) % (numDotsX / 2);
		for (int dot = 0; dot < 2 * (numDotsX + numDotsY); ++dot) {
			int x = 0, y = 0;
			if (dot <= numDotsX) {
				x = dot;
			} else if (dot < numDotsX + numDotsY) {
				x = numDotsX;
				y = dot - numDotsX;
			} else if (dot < 2 * numDotsX + numDotsY + 1) {
				x = 2 * numDotsX + numDotsY - dot;
				y = numDotsY;
			} else {
				y = 2 * (numDotsX + numDotsY) - dot;
			}
			g.setColor((dot + light) % (numDotsX / 2) == 0 ? Color.PINK : Color.RED);
			g.fillRect(t(sceneController.tileBoardTopLeft.x) + 4 * x, t(sceneController.tileBoardTopLeft.y) + 4 * y, 2, 2);
		}
	}

	private void drawPressKeyToStart(Graphics2D g, int tileY) {
		if (sceneController.blinking.frame()) {
			String text = "PRESS SPACE TO PLAY";
			g.setColor(Color.ORANGE);
			g.setFont(rendering.getScoreFont());
			g.drawString(text, t(13 - text.length() / 2), t(tileY));
		}
	}

	private void drawPointsAnimation(Graphics2D g, int tileY) {
		int x = t(10), y = t(tileY);
		if (sceneController.blinking.frame()) {
			g.setColor(Color.PINK);
			g.fillOval(x, y + t(1) - 2, 10, 10);
			g.fillRect(x + 6, y - t(1) + 2, 2, 2);
		}
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		g.drawString("10", x + t(2), y);
		g.drawString("50", x + t(2), y + t(2));
		g.setFont(rendering.getScoreFont().deriveFont(6f));
		g.drawString("PTS", x + t(5), y);
		g.drawString("PTS", x + t(5), y + t(2));
	}
}