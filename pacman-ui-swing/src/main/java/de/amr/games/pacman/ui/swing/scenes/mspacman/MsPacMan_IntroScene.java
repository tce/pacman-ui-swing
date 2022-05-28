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

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.mspacman.IntroController;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * Intro scene of the Ms. Pac-Man game. The ghosts and Ms. Pac-Man are introduced one after another.
 * 
 * @author Armin Reichert
 */
public class MsPacMan_IntroScene extends GameScene {

	private final IntroController sceneController;
	private final IntroController.Context context;
	private Player2D msPacMan2D;
	private Ghost2D[] ghosts2D;

	public MsPacMan_IntroScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sceneController = new IntroController(gameController);
		context = sceneController.context();
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sceneController.restartInInitialState(IntroController.State.BEGIN);
		msPacMan2D = new Player2D(context.msPacMan, game, r2D);
		msPacMan2D.munchings.values().forEach(TimedSeq::restart);
		ghosts2D = Stream.of(context.ghosts).map(ghost -> new Ghost2D(ghost, game, r2D)).toArray(Ghost2D[]::new);
		Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.animKicking.values().forEach(TimedSeq::restart));
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			SoundManager.get().play(GameSound.CREDIT);
			gameController.addCredit();
			return;
		} else if (Keyboard.keyPressed("Space")) {
			gameController.requestGame();
			return;
		}
		sceneController.update();
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawScore(g, gameController.game(), true);
		r2D.drawCredit(g, gameController.credit());

		g.setFont(r2D.getArcadeFont());
		g.setColor(Color.ORANGE);
		g.drawString("\"MS PAC-MAN\"", context.titlePosition.x, context.titlePosition.y);

		drawAnimatedBoard(g, 32, 16);
		switch (sceneController.state()) {
		case GHOSTS -> drawGhostText(g);
		case MSPACMAN -> drawMsPacManText(g);
		case READY_TO_PLAY -> {
			msPacMan2D.reset();// ensure Ms. Pac-Man is displayed with mouth half open
			drawMsPacManText(g);
			if (gameController.credit() > 0) {
				drawPressKeyToStart(g, 26);
			}
		}
		default -> {
		}
		}

		Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.render(g));
		msPacMan2D.render(g);
		r2D.drawCopyright(g, t(4), t(28));
	}

	private void drawGhostText(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		if (context.ghostIndex == 0) {
			g.drawString("WITH", context.titlePosition.x, context.lightsTopLeft.y + t(3));
		}
		Ghost ghost = context.ghosts[context.ghostIndex];
		g.setColor(r2D.getGhostColor(ghost.id));
		g.drawString(ghost.name.toUpperCase(), t(14 - ghost.name.length() / 2), context.lightsTopLeft.y + t(6));
	}

	private void drawMsPacManText(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		g.drawString("STARRING", context.titlePosition.x, context.lightsTopLeft.y + t(3));
		g.setColor(Color.YELLOW);
		g.drawString("MS PAC-MAN", context.titlePosition.x, context.lightsTopLeft.y + t(6));
	}

	private void drawAnimatedBoard(Graphics2D g, int numDotsX, int numDotsY) {
		long time = context.lightsTimer.tick();
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
			g.fillRect(context.lightsTopLeft.x + 4 * x, context.lightsTopLeft.y + 4 * y, 2, 2);
		}
	}

	private void drawPressKeyToStart(Graphics2D g, int tileY) {
		if (context.blinking.frame()) {
			String text = "PRESS SPACE TO PLAY";
			g.setColor(Color.WHITE);
			g.setFont(r2D.getArcadeFont());
			g.drawString(text, t(13 - text.length() / 2), t(tileY));
		}
	}

}