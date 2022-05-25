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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.lib.TickTimer.sec_to_ticks;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.pacman.IntroController;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * Intro scene of the PacMan game.
 * <p>
 * The ghost are presented one after another, then Pac-Man is chased by the ghosts, turns the card and hunts the ghost
 * himself.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntroScene extends GameScene {

	private final IntroController sceneController;

	private Player2D pacMan2D;
	private Ghost2D[] ghosts2D;

	public PacMan_IntroScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sceneController = new IntroController(gameController);
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sceneController.reset(IntroController.State.BEGIN);

		pacMan2D = new Player2D(sceneController.context.pacMan, game, r2D);
		pacMan2D.munchings.values().forEach(TimedSeq::restart);

		ghosts2D = Stream.of(sceneController.context.ghosts).map(ghost -> {
			Ghost2D ghost2D = new Ghost2D(ghost, game, r2D);
			ghost2D.animKicking.values().forEach(TimedSeq::restart);
			ghost2D.animFrightened.restart();
			return ghost2D;
		}).toArray(Ghost2D[]::new);
	}

	@Override
	public void update() {
		if (Keyboard.get().pressed("5")) {
			SoundManager.get().play(GameSound.CREDIT);
			gameController.addCredit();
			return;
		} else if (Keyboard.get().pressed("Space")) {
			gameController.requestGame();
			return;
		}

		sceneController.update();
		// TODO find a better solution:
		if (sceneController.state() == IntroController.State.CHASING_GHOSTS) {
			for (Ghost ghost : sceneController.context.ghosts) {
				if (ghost.velocity.equals(V2d.NULL)) {
					ghosts2D[ghost.id].animFrightened.stop();
				} else if (!ghosts2D[ghost.id].animFrightened.isRunning()) {
					ghosts2D[ghost.id].animFrightened.restart();
				}
			}
		}
	}

	@Override
	public void render(Graphics2D g_) {
		Graphics2D g = (Graphics2D) g_.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		r2D.drawScore(g, game, true);
		r2D.drawCredit(g, gameController.credit());
		switch (sceneController.state()) {
		case BEGIN, PRESENTING_GHOSTS -> drawGallery(g);
		case SHOWING_POINTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			if (sceneController.state().timer().tick() > sec_to_ticks(1)) {
				drawEnergizer(g);
				r2D.drawCopyright(g, t(4), t(29));
			}
		}
		case CHASING_PAC -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			r2D.drawCopyright(g, t(4), t(29));
			if (sceneController.context.fastBlinking.frame()) {
				drawEnergizer(g);
			}
			int offset = sceneController.state().timer().tick() % 5 < 2 ? 0 : -1;
			drawGuys(g, offset);
		}
		case CHASING_GHOSTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			r2D.drawCopyright(g, t(4), t(29));
			drawGuys(g, 0);
		}
		case READY_TO_PLAY -> {
			drawGallery(g);
			drawPressKeyToStart(g, 24);
		}
		default -> {
		}
		}
		g.dispose();
	}

	private void drawGuys(Graphics2D g, int offset) {
		Graphics2D gg = (Graphics2D) g.create();
		gg.translate(offset, 0);
		ghosts2D[1].render(gg);
		ghosts2D[2].render(gg);
		gg.dispose();
		ghosts2D[0].render(g);
		ghosts2D[3].render(g);
		pacMan2D.render(g);
	}

	private void drawGallery(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		g.drawString("CHARACTER", t(6), t(6));
		g.drawString("/", t(16), t(6));
		g.drawString("NICKNAME", t(18), t(6));
		for (int ghostID = 0; ghostID < 4; ++ghostID) {
			if (sceneController.context.pictureVisible[ghostID]) {
				int tileX = 3, tileY = 7 + 3 * ghostID;
				drawGhost(g, ghostID, t(tileX), t(tileY));
				if (sceneController.context.characterVisible[ghostID]) {
					g.setColor(r2D.getGhostColor(ghostID));
					g.drawString("-" + sceneController.context.characters[ghostID], t(6), t(tileY + 1));
				}
				if (sceneController.context.nicknameVisible[ghostID]) {
					g.setColor(r2D.getGhostColor(ghostID));
					g.drawString("\"" + sceneController.context.nicknames[ghostID] + "\"", t(18), t(tileY + 1));
				}
			}
		}
	}

	private void drawGhost(Graphics2D g, int ghostID, int x, int y) {
		BufferedImage sprite = r2D.s(0, 4 + ghostID);
		r2D.renderSprite(g, sprite, x, y);
	}

	private void drawPressKeyToStart(Graphics2D g, int yTile) {
		if (sceneController.context.slowBlinking.frame()) {
			String text = "PRESS SPACE TO PLAY";
			g.setColor(Color.WHITE);
			g.setFont(r2D.getArcadeFont());
			g.drawString(text, t(14 - text.length() / 2), t(yTile));
		}
	}

	private void drawPoints(Graphics2D g, int tileX, int tileY) {
		g.setColor(r2D.getFoodColor(1));
		g.fillRect(t(tileX) + 6, t(tileY - 1) + 2, 2, 2);
		if (sceneController.context.fastBlinking.frame()) {
			g.fillOval(t(tileX), t(tileY + 1) - 2, 10, 10);
		}
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		g.drawString("10", t(tileX + 2), t(tileY));
		g.drawString("50", t(tileX + 2), t(tileY + 2));
		g.setFont(r2D.getArcadeFont().deriveFont(6f));
		g.drawString("PTS", t(tileX + 5), t(tileY));
		g.drawString("PTS", t(tileX + 5), t(tileY + 2));
	}

	private void drawEnergizer(Graphics2D g) {
		g.setColor(r2D.getFoodColor(1));
		g.fillOval(t(3), t(20), TS, TS);
	}
}