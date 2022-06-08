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
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations.Key;
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
	private Pac2D pacMan2D;
	private Ghost2D[] ghosts2D;

	@Override
	public void setContext(GameController gameController) {
		super.setContext(gameController);
		sceneController = new IntroController(gameController);
		sceneController.addStateChangeListener(this::onSceneStateChange);
		$ = sceneController.context;
	}

	@Override
	public void init() {
		sceneController.restartInInitialState(IntroController.State.START);
		pacMan2D = new Pac2D($.pacMan, game, new PacAnimations(r2D));
		ghosts2D = Stream.of($.ghosts).map(ghost -> {
			Ghost2D ghost2D = new Ghost2D(ghost, game, new GhostAnimations(ghost.id, r2D));
			return ghost2D;
		}).toArray(Ghost2D[]::new);
	}

	private void onSceneStateChange(State fromState, State toState) {
		if (fromState == State.CHASING_PAC && toState == State.CHASING_GHOSTS) {
			for (var ghost2D : ghosts2D) {
				ghost2D.animations.select(Key.ANIM_BLUE);
			}
		}
	}

	@Override
	public void update() {
		if (gameController.credit() > 0 && Keyboard.keyPressed("1")) {
			gameController.requestGame();
			return;
		} else if (Keyboard.keyPressed("5")) {
			SoundManager.get().play(GameSound.CREDIT);
			gameController.addCredit();
			return;
		}
		sceneController.update();
		updateAnimations();
	}

	private void updateAnimations() {
		// TODO this is not elegant but works
		if (sceneController.state() == State.CHASING_GHOSTS) {
			for (var ghost2D : ghosts2D) {
				if (ghost2D.ghost.bounty > 0) {
					ghost2D.animations.select(Key.ANIM_VALUE);
				} else {
					ghost2D.animations.select(Key.ANIM_BLUE);
					if (ghost2D.ghost.velocity.length() == 0) {
						ghost2D.animations.stop(Key.ANIM_BLUE);
					} else {
						ghost2D.animations.run(Key.ANIM_BLUE);
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawScore(g, game, true);
		r2D.drawCredit(g, gameController.credit());

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
		ghosts2D[1].render(gg, r2D);
		ghosts2D[2].render(gg, r2D);
		gg.dispose();
		ghosts2D[0].render(g, r2D);
		ghosts2D[3].render(g, r2D);
		pacMan2D.render(g, r2D);
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