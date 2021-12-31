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

import static de.amr.games.pacman.model.common.GameVariant.PACMAN;
import static de.amr.games.pacman.model.world.PacManGameWorld.TS;
import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.controller.pacman.IntroController;
import de.amr.games.pacman.controller.pacman.IntroController.GhostPortrait;
import de.amr.games.pacman.lib.TickTimer;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.PacManGameUI_Swing;
import de.amr.games.pacman.ui.swing.rendering.common.Ghost2D;
import de.amr.games.pacman.ui.swing.rendering.common.Player2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Intro scene of the PacMan game.
 * <p>
 * The ghost are presented one after another, then Pac-Man is chased by the ghosts, turns the card
 * and hunts the ghost himself.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntroScene extends GameScene {

	// use exactly same RGB values as sprites
	static final Color PINK = new Color(252, 181, 255);
	static final Color ORANGE = new Color(253, 192, 90);
	static final Color PELLET_COLOR = new Color(254, 189, 180);

	private IntroController sceneController;
	private Player2D pacMan2D;
	private List<Ghost2D> ghosts2D;
	private List<Ghost2D> ghostsInGallery2D;

	public PacMan_IntroScene(PacManGameController controller, Dimension size) {
		super(controller, size, PacManGameUI_Swing.RENDERING_PACMAN, PacManGameUI_Swing.SOUND.get(PACMAN));
	}

	@Override
	public void init() {
		sceneController = new IntroController(gameController);
		sceneController.init();
		pacMan2D = new Player2D(sceneController.pacMan);
		pacMan2D.setMunchingAnimations(rendering.createPlayerMunchingAnimations());
		ghosts2D = Stream.of(sceneController.ghosts).map(Ghost2D::new).collect(Collectors.toList());
		ghosts2D.forEach(ghost2D -> {
			ghost2D.setKickingAnimations(rendering.createGhostKickingAnimations(ghost2D.ghost.id));
			ghost2D.getKickingAnimations().values().forEach(TimedSequence::restart);
			ghost2D.setFrightenedAnimation(rendering.createGhostFrightenedAnimation());
			ghost2D.getFrightenedAnimation().restart();
			ghost2D.setFlashingAnimation(rendering.createGhostFlashingAnimation());
			ghost2D.setBountyNumberSprites(rendering.getBountyNumberSpritesMap());
		});
		ghostsInGallery2D = new ArrayList<>();
		for (int i = 0; i < 4; ++i) {
			Ghost ghost = sceneController.portraits[i].ghost;
			Ghost2D ghost2D = new Ghost2D(ghost);
			ghost2D.setKickingAnimations(rendering.createGhostKickingAnimations(ghost.id));
			ghostsInGallery2D.add(ghost2D);
		}
	}

	@Override
	public void update() {
		sceneController.update();
	}

	@Override
	public void end() {
	}

	@Override
	public void render(Graphics2D g_) {
		Graphics2D g = (Graphics2D) g_.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendering.drawScore(g, gameController.game(), true);
		switch (sceneController.currentStateID) {
		case BEGIN:
		case PRESENTING_GHOSTS:
			drawGallery(g);
			break;
		case SHOWING_POINTS:
			drawGallery(g);
			drawPoints(g, 11, 25);
			if (sceneController.stateTimer().ticked() > TickTimer.sec_to_ticks(1)) {
				drawEnergizer(g);
				drawCopyright(g, 32);
			}
			break;
		case CHASING_PAC:
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawCopyright(g, 32);
			if (sceneController.blinking.frame()) {
				drawEnergizer(g);
			}
			drawGuys(g);
			break;
		case CHASING_GHOSTS:
			drawGallery(g);
			drawPoints(g, 11, 25);
			drawCopyright(g, 32);
			drawGuys(g);
			break;
		case READY_TO_PLAY:
			drawGallery(g);
			drawPressKeyToStart(g, 24);
			break;
		default:
			break;
		}
		g.dispose();
	}

	private void drawGuys(Graphics2D g) {
		ghosts2D.forEach(ghost2D -> ghost2D.render(g));
		pacMan2D.render(g);
	}

	private void drawGallery(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		g.drawString("CHARACTER", t(6), sceneController.topY);
		g.drawString("/", t(16), sceneController.topY);
		g.drawString("NICKNAME", t(18), sceneController.topY);
		for (int i = 0; i < 4; ++i) {
			GhostPortrait portrait = sceneController.portraits[i];
			if (portrait.ghost.visible) {
				int y = sceneController.topY + t(1 + 3 * i);
				ghostsInGallery2D.get(i).render(g);
				g.setColor(getGhostColor(i));
				g.setFont(rendering.getScoreFont());
				if (portrait.characterVisible) {
					g.drawString("-" + portrait.character, t(6), y + 8);
				}
				if (portrait.nicknameVisible) {
					g.drawString("\"" + portrait.ghost.name + "\"", t(18), y + 8);
				}
			}
		}
	}

	private Color getGhostColor(int i) {
		return i == 0 ? Color.RED : i == 1 ? PINK : i == 2 ? Color.CYAN : ORANGE;
	}

	private void drawPressKeyToStart(Graphics2D g, int yTile) {
		if (sceneController.blinking.frame()) {
			String text = "PRESS SPACE TO PLAY";
			g.setColor(ORANGE);
			g.setFont(rendering.getScoreFont());
			g.drawString(text, t(14 - text.length() / 2), t(yTile));
		}
	}

	private void drawPoints(Graphics2D g, int tileX, int tileY) {
		g.setColor(PELLET_COLOR);
		g.fillRect(t(tileX) + 6, t(tileY - 1) + 2, 2, 2);
		if (sceneController.blinking.frame()) {
			g.fillOval(t(tileX), t(tileY + 1) - 2, 10, 10);
		}
		g.setColor(Color.WHITE);
		g.setFont(rendering.getScoreFont());
		g.drawString("10", t(tileX + 2), t(tileY));
		g.drawString("50", t(tileX + 2), t(tileY + 2));
		g.setFont(rendering.getScoreFont().deriveFont(6f));
		g.drawString("PTS", t(tileX + 5), t(tileY));
		g.drawString("PTS", t(tileX + 5), t(tileY + 2));
	}

	private void drawEnergizer(Graphics2D g) {
		g.setColor(PELLET_COLOR);
		g.fillOval(t(2), t(20), TS, TS);
	}

	private void drawCopyright(Graphics2D g, int yTile) {
		String text = "\u00A9" + "  1980 MIDWAY MFG. CO.";
		g.setFont(rendering.getScoreFont());
		g.setColor(PINK);
		g.drawString(text, t(3), t(yTile));
	}
}