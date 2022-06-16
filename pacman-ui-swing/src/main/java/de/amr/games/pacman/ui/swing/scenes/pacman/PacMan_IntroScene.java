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
import java.util.stream.Stream;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.pacman.IntroController;
import de.amr.games.pacman.controller.pacman.IntroController.State;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
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

	private IntroController sceneController;
	private IntroController.Context $;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new IntroController(gameController);
		sceneController.addStateChangeListener(this::onSceneStateChange);
		$ = sceneController.$;
	}

	@Override
	public void init() {
		sceneController.restartInInitialState(IntroController.State.START);
		$.pacMan.setAnimations(new PacAnimations(r2D));
		$.pacMan.animations().get().ensureRunning();
		Stream.of($.ghosts).forEach(ghost -> {
			ghost.setAnimations(new GhostAnimations(ghost.id, r2D));
		});
	}

	private void onSceneStateChange(State fromState, State toState) {
		if (fromState == State.CHASING_PAC && toState == State.CHASING_GHOSTS) {
			for (var ghost : $.ghosts) {
				ghost.animations().get().select("blue");
			}
		}
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("1")) {
			gameController.state().requestGame(game);
		} else if (Keyboard.keyPressed("5")) {
			gameController.state().addCredit(game);
		} else {
			sceneController.update();
			updateAnimations();
		}
	}

	private void updateAnimations() {
		// TODO this is not elegant but works
		if (sceneController.state() == State.CHASING_GHOSTS) {
			for (var ghost : $.ghosts) {
				if (ghost.state == GhostState.DEAD && ghost.killIndex != -1) {
					ghost.animations().get().select("value");
					ghost.animations().get().selectedAnimation().ensureRunning();
				} else {
					ghost.animations().get().select("blue");
					ghost.animations().get().selectedAnimation().ensureRunning();
					if (ghost.velocity.length() == 0) {
						ghost.animations().get().byName("blue").stop();
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawScores(g, game, true);
		r2D.drawCredit(g, game.credit);

		switch (sceneController.state()) {
		case START, PRESENTING_GHOSTS -> drawGallery(g);
		case SHOWING_POINTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			if (sceneController.state().timer().tick() > sec_to_ticks(1)) {
				drawEnergizer(g);
				r2D.drawCopyright(g, t(3), t(32));
			}
		}
		case CHASING_PAC -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			r2D.drawCopyright(g, t(3), t(32));
			if ($.blinking.frame()) {
				drawEnergizer(g);
			}
			int offset = sceneController.state().timer().tick() % 5 < 2 ? 0 : -1;
			drawGuys(g, offset);
		}
		case CHASING_GHOSTS -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			r2D.drawCopyright(g, t(3), t(32));
			drawGuys(g, 0);
		}
		case READY_TO_PLAY -> {
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawGuys(g, 0);
		}
		default -> {
		}
		}
	}

	private void drawGuys(Graphics2D g, int offset) {
		Graphics2D gg = (Graphics2D) g.create();
		gg.translate(offset, 0);
		r2D.drawGhost(gg, $.ghosts[1]);
		r2D.drawGhost(gg, $.ghosts[2]);
		gg.dispose();
		r2D.drawGhost(g, $.ghosts[0]);
		r2D.drawGhost(g, $.ghosts[3]);
		r2D.drawPac(g, $.pacMan);
	}

	private void drawGallery(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(r2D.getArcadeFont());
		g.drawString("CHARACTER", t(6), t(6));
		g.drawString("/", t(16), t(6));
		g.drawString("NICKNAME", t(18), t(6));
		for (int id = 0; id < 4; ++id) {
			if ($.pictureVisible[id]) {
				int tileY = 7 + 3 * id;
				r2D.drawSpriteCenteredOverBox(g, r2D.getGhostSprite(id, Direction.RIGHT), t(3), t(tileY));
				if ($.characterVisible[id]) {
					g.setColor(r2D.getGhostColor(id));
					g.drawString("-" + $.characters[id], t(6), t(tileY + 1));
				}
				if ($.nicknameVisible[id]) {
					g.setColor(r2D.getGhostColor(id));
					g.drawString("\"" + $.nicknames[id] + "\"", t(17), t(tileY + 1));
				}
			}
		}
	}

	private void drawPoints(Graphics2D g, int tileX, int tileY) {
		g.setColor(r2D.getFoodColor(1));
		g.fillRect(t(tileX) + 6, t(tileY - 1) + 2, 2, 2);
		if ($.blinking.frame()) {
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