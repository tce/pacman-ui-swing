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

import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.mspacman.IntroController;
import de.amr.games.pacman.controller.mspacman.IntroController.IntroState;
import de.amr.games.pacman.lib.TickTimer;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

/**
 * Intro scene of the Ms. Pac-Man game. The ghosts and Ms. Pac-Man are introduced one after another.
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntroScene extends GameScene {

	private static final String MIDWAY_LOGO = "/mspacman/graphics/midway.png";

	private final V2i titlePosition = new V2i(t(9), t(8));
	private final TickTimer boardAnimationTimer = new TickTimer("boardAnimation-timer");
	private final IntroController sceneController = new IntroController();

	private BufferedImage midwayLogo;
	private Player2D msPacMan2D;
	private List<Ghost2D> ghosts2D;

	public MsPacMan_IntroScene(PacManGameUI_Swing ui, Dimension size) {
		super(ui, size, ScenesMsPacMan.RENDERING, ScenesMsPacMan.SOUNDS);
		try {
			midwayLogo = ImageIO.read(getClass().getResourceAsStream(MIDWAY_LOGO));
		} catch (IOException e) {
			throw new RuntimeException(String.format("Resource '%s' could not be read", MIDWAY_LOGO));
		}
	}

	@Override
	public void init(PacManGameController gameController) {
		super.init(gameController);
		sceneController.init(gameController);

		msPacMan2D = new Player2D(sceneController.msPacMan, rendering);
		msPacMan2D.munchingAnimations.values().forEach(TimedSequence::restart);

		ghosts2D = Stream.of(sceneController.ghosts).map(ghost -> {
			Ghost2D ghost2D = new Ghost2D(ghost, rendering);
			ghost2D.kickingAnimations.values().forEach(TimedSequence::restart);
			return ghost2D;
		}).collect(Collectors.toList());

		boardAnimationTimer.setIndefinite().start();
	}

	@Override
	public void update() {
		sceneController.updateState();
		boardAnimationTimer.tick();
	}

	@Override
	public void render(Graphics2D g_) {
		IntroState state = sceneController.currentStateID;
		Graphics2D g = (Graphics2D) g_.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendering.drawScore(g, gameController.game, true);
		g.setFont(rendering.getScoreFont());
		g.setColor(Color.ORANGE);
		g.drawString("\"MS PAC-MAN\"", titlePosition.x, titlePosition.y);
		drawAnimatedBoard(g, 32, 16);
		if (state == IntroState.PRESENTING_GHOSTS) {
			drawPresentingGhost(g, sceneController.ghosts[sceneController.currentGhostIndex]);
		} else if (state == IntroState.PRESENTING_MSPACMAN) {
			drawStarringMsPacMan(g);
		} else if (state == IntroState.WAITING_FOR_GAME) {
			// TODO: this hack ensures that Ms. Pac-Man is displayed with mouth half open
			msPacMan2D.reset();
			drawStarringMsPacMan(g);
			drawPressKeyToStart(g, 26);
		}
		ghosts2D.forEach(ghost2D -> ghost2D.render(g));
		msPacMan2D.render(g);
		drawCopyright(g);
		g.dispose();
	}

	private void drawPresentingGhost(Graphics2D g, Ghost ghost) {
		int top = sceneController.tileBoardTopLeft.y;
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		if (ghost == sceneController.ghosts[0]) {
			g.drawString("WITH", titlePosition.x, t(top + 3));
		}
		g.setColor(ghost.id == 0 ? Color.RED : ghost.id == 1 ? Color.PINK : ghost.id == 2 ? Color.CYAN : Color.ORANGE);
		g.drawString(ghost.name.toUpperCase(), t(14 - ghost.name.length() / 2), t(top + 6));
	}

	private void drawStarringMsPacMan(Graphics2D g) {
		int top = sceneController.tileBoardTopLeft.y;
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		g.drawString("STARRING", titlePosition.x, t(top + 3));
		g.setColor(Color.YELLOW);
		g.drawString("MS PAC-MAN", titlePosition.x, t(top + 6));
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
			g.setColor(Color.WHITE);
			g.setFont(rendering.getScoreFont());
			g.drawString(text, t(13 - text.length() / 2), t(tileY));
		}
	}

	private void drawCopyright(Graphics2D g) {
		double scale = 36.0 / midwayLogo.getHeight();
		g.drawImage(midwayLogo, t(4), t(28) + 3, (int) (scale * midwayLogo.getWidth()),
				(int) (scale * midwayLogo.getHeight()), null);
		g.setColor(Color.RED);
		g.setFont(new Font("Dialog", Font.PLAIN, 11));
		g.drawString("\u00a9", t(9), t(30) + 2); // (c) symbol
		g.setFont(rendering.getScoreFont());
		g.drawString("MIDWAY MFG CO", t(11), t(30));
		g.drawString("1980/1981", t(12), t(32));
	}
}