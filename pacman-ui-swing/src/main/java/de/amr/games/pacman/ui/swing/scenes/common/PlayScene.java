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
package de.amr.games.pacman.ui.swing.scenes.common;

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.sound.sampled.Clip;

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.BonusAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	@Override
	public void init() {
		game.setMazeFlashingAnimation(r2D.createMazeFlashingAnimation(r2D.mazeNumber(game.level.number)));
		game.pac.setAnimations(new PacAnimations(r2D));
		game.ghosts().forEach(ghost -> {
			ghost.setAnimations(new GhostAnimations(ghost.id, r2D));
		});
		game.bonus().setAnimations(new BonusAnimations(r2D));
		game.bonus().setInactive();
	}

	@Override
	public void update() {
		updateSound();
	}

	private void updateSound() {
		if (!hasCredit()) {
			return;
		}
		switch (gameController.state()) {
		case HUNTING -> {
			if (SoundManager.get().getClip(GameSound.PACMAN_MUNCH).isRunning() && game.pac.starvingTicks > 10) {
				SoundManager.get().stop(GameSound.PACMAN_MUNCH);
			}
			if (game.huntingTimer.tick() == 0) {
				SoundManager.get().stopSirens();
				SoundManager.get().startSiren(game.huntingTimer.phase() / 2);
			}
		}
		case PACMAN_DYING -> {
			if (gameController.state().timer().atSecond(2)) {
				SoundManager.get().play(GameSound.PACMAN_DEATH);
			}
		}
		default -> {
		}
		}
	}

	@Override
	public void render(Graphics2D g) {
		boolean playing = gameController.isGameRunning();
		boolean showHighScoreOnly = !playing && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;

		r2D.drawScores(g, game, showHighScoreOnly);
		drawMaze(g);
		r2D.drawBonus(g, game.bonus().entity());
		r2D.drawPac(g, game.pac);
		game.ghosts().forEach(ghost -> r2D.drawGhost(g, ghost));

		DebugDraw.drawPlaySceneDebugInfo(g, gameController);

		if (hasCredit() && playing) {
			r2D.drawLivesCounter(g, game);
		}
		if (hasCredit()) {
			r2D.drawLevelCounter(g, game);
		}
		if (!hasCredit() && !playing) {
			r2D.drawCredit(g, gameController.credit());
		}
		SoundManager.get().setMuted(gameController.credit() == 0); // TODO check
	}

	private void drawMaze(Graphics2D g) {
		if (game.mazeFlashingAnimation().isPresent() && game.mazeFlashingAnimation().get().isRunning()) {
			g.drawImage((Image) game.mazeFlashingAnimation().get().frame(), 0, t(3), null);
		} else {
			r2D.drawFullMaze(g, r2D.mazeNumber(game.level.number), 0, t(3));
			r2D.drawDarkTiles(g, game.level.world.tiles(), tile -> game.level.world.containsEatenFood(tile)
					|| game.level.world.isEnergizerTile(tile) && !game.energizerPulse.frame());
		}
		DebugDraw.drawMazeStructure(g, game);
		r2D.drawGameState(g, game, hasCredit() ? gameController.state() : GameState.GAME_OVER);
	}

	@Override
	public void onPlayerLosesPower(GameEvent e) {
		SoundManager.get().stop(GameSound.PACMAN_POWER);
		if (!SoundManager.get().isAnySirenPlaying()) {
			SoundManager.get().startSiren(game.huntingTimer.phase() / 2);
		}
	}

	@Override
	public void onPlayerFindsFood(GameEvent e) {
		SoundManager.get().play(GameSound.PACMAN_MUNCH);
	}

	@Override
	public void onPlayerGetsPower(GameEvent e) {
		SoundManager.get().stopSirens();
		SoundManager.get().loop(GameSound.PACMAN_POWER, Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		SoundManager.get().play(GameSound.BONUS_EATEN);
	}

	@Override
	public void onPlayerGetsExtraLife(GameEvent e) {
		SoundManager.get().play(GameSound.EXTRA_LIFE);
	}

	@Override
	public void onGhostStartsReturningHome(GameEvent e) {
		SoundManager.get().play(GameSound.GHOST_RETURNING);
	}

	@Override
	public void onGhostEntersHouse(GameEvent e) {
		if (game.ghosts(GhostState.DEAD).count() == 0) {
			SoundManager.get().stop(GameSound.GHOST_RETURNING);
		}
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		switch (e.newGameState) {
		case READY -> {
			SoundManager.get().stopAll();
			if (gameController.credit() > 0 && !gameController.isGameRunning()) {
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}
		case PACMAN_DYING -> {
			SoundManager.get().stopAll();
		}
		case GHOST_DYING -> {
			SoundManager.get().play(GameSound.GHOST_EATEN);
		}
		case LEVEL_STARTING -> {
			gameController.state().timer().expire();
		}
		case LEVEL_COMPLETE -> {
			SoundManager.get().stopAll();
		}
		case GAME_OVER -> {
			SoundManager.get().stopAll();
		}
		default -> {
		}
		}
	}
}