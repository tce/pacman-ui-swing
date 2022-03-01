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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.lib.TickTimer.sec_to_ticks;
import static de.amr.games.pacman.model.world.World.TS;
import static de.amr.games.pacman.model.world.World.t;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.pacman.IntroController;
import de.amr.games.pacman.controller.pacman.IntroController.GhostPortrait;
import de.amr.games.pacman.controller.pacman.IntroController.IntroState;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intro scene of the PacMan game.
 * <p>
 * The ghost are presented one after another, then Pac-Man is chased by the ghosts, turns the card and hunts the ghost
 * himself.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntroScene extends GameScene {

	private final IntroController sc = new IntroController();

	private Player2D pacMan2D;
	private Ghost2D[] ghosts2D;
	private Ghost2D[] gallery2D;

	public PacMan_IntroScene(GameController gameController, V2i size, Rendering2D r2D, SoundManager sounds) {
		super(gameController, size, r2D, sounds);
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sc.init(gameController);

		pacMan2D = new Player2D(sc.pacMan, game, r2D);
		pacMan2D.munchings.values().forEach(TimedSeq::restart);

		ghosts2D = Stream.of(sc.ghosts).map(ghost -> {
			Ghost2D ghost2D = new Ghost2D(ghost, game, r2D);
			ghost2D.animKicking.values().forEach(TimedSeq::restart);
			ghost2D.animFrightened.restart();
			return ghost2D;
		}).toArray(Ghost2D[]::new);

		gallery2D = new Ghost2D[] { //
				new Ghost2D(sc.portraits[0].ghost, game, r2D), //
				new Ghost2D(sc.portraits[1].ghost, game, r2D), //
				new Ghost2D(sc.portraits[2].ghost, game, r2D), //
				new Ghost2D(sc.portraits[3].ghost, game, r2D) };
	}

	@Override
	public void update() {
		sc.updateState();
		// TODO find a better solution:
		if (sc.state == IntroState.CHASING_GHOSTS) {
			for (Ghost ghost : sc.ghosts) {
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
		switch (sc.state) {

		case BEGIN, PRESENTING_GHOSTS -> drawGallery(g);

		case SHOWING_POINTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			if (sc.stateTimer().ticked() > sec_to_ticks(1)) {
				drawEnergizer(g);
				drawCopyright(g, 32);
			}
		}

		case CHASING_PAC -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawCopyright(g, 32);
			if (sc.blinking.frame()) {
				drawEnergizer(g);
			}
			int offset = sc.stateTimer().ticked() % 5 < 2 ? 0 : -1;
			drawGuys(g, offset);
		}

		case CHASING_GHOSTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawCopyright(g, 32);
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
		g.setFont(r2D.getScoreFont());
		g.drawString("CHARACTER", t(6), sc.topY);
		g.drawString("/", t(16), sc.topY);
		g.drawString("NICKNAME", t(18), sc.topY);
		for (int ghostID = 0; ghostID < 4; ++ghostID) {
			GhostPortrait portrait = sc.portraits[ghostID];
			if (portrait.ghost.visible) {
				int y = sc.topY + t(1 + 3 * ghostID);
				gallery2D[ghostID].render(g);
				if (portrait.characterVisible) {
					g.setColor(r2D.getGhostColor(ghostID));
					g.drawString("-" + portrait.character, t(6), y + 8);
				}
				if (portrait.nicknameVisible) {
					g.setColor(r2D.getGhostColor(ghostID));
					g.drawString("\"" + portrait.ghost.name + "\"", t(18), y + 8);
				}
			}
		}
	}

	private void drawPressKeyToStart(Graphics2D g, int yTile) {
		if (sc.slowBlinking.frame()) {
			String text = "PRESS SPACE TO PLAY";
			g.setColor(Color.WHITE);
			g.setFont(r2D.getScoreFont());
			g.drawString(text, t(14 - text.length() / 2), t(yTile));
		}
	}

	private void drawPoints(Graphics2D g, int tileX, int tileY) {
		g.setColor(r2D.getFoodColor(1));
		g.fillRect(t(tileX) + 6, t(tileY - 1) + 2, 2, 2);
		if (sc.blinking.frame()) {
			g.fillOval(t(tileX), t(tileY + 1) - 2, 10, 10);
		}
		g.setColor(Color.WHITE);
		g.setFont(r2D.getScoreFont());
		g.drawString("10", t(tileX + 2), t(tileY));
		g.drawString("50", t(tileX + 2), t(tileY + 2));
		g.setFont(r2D.getScoreFont().deriveFont(6f));
		g.drawString("PTS", t(tileX + 5), t(tileY));
		g.drawString("PTS", t(tileX + 5), t(tileY + 2));
	}

	private void drawEnergizer(Graphics2D g) {
		g.setColor(r2D.getFoodColor(1));
		g.fillOval(t(2), t(20), TS, TS);
	}

	private void drawCopyright(Graphics2D g, int yTile) {
		String text = "\u00A9" + "  1980 MIDWAY MFG. CO.";
		g.setFont(r2D.getScoreFont());
		g.setColor(r2D.getGhostColor(GameModel.PINK_GHOST));
		g.drawString(text, t(3), t(yTile));
	}
}