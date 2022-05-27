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
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import javax.sound.sampled.Clip;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.event.ScatterPhaseStartedEvent;
import de.amr.games.pacman.lib.TickTimerEvent;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.model.common.GhostState;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Bonus2D;
import de.amr.games.pacman.ui.swing.entity.common.Energizer2D;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	private Player2D player2D;
	private Ghost2D[] ghosts2D;
	private Energizer2D[] energizers2D;
	private Bonus2D bonus2D;
	private TimedSeq<BufferedImage> mazeFlashing;

	public PlayScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
	}

	@Override
	public void init(GameModel game) {
		super.init(game);

		player2D = new Player2D(game.player, game, r2D);
		ghosts2D = game.ghosts().map(ghost -> new Ghost2D(ghost, game, r2D)).toArray(Ghost2D[]::new);
		energizers2D = game.level.world.energizerTiles().map(Energizer2D::new).toArray(Energizer2D[]::new);
		bonus2D = new Bonus2D(game, game.bonus().get(), r2D);
		mazeFlashing = r2D.mazeFlashing(game.level.mazeNumber).repetitions(game.level.numFlashes).reset();
		game.player.powerTimer.addEventListener(this::handleGhostsFlashing);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void update() {
		switch (gameController.state()) {
		case LEVEL_COMPLETE -> {
			if (mazeFlashing.isComplete()) {
				gameController.state().timer().expire();
			} else if (gameController.state().timer().atSecond(2)) {
				game.ghosts().forEach(Ghost::hide);
			} else if (gameController.state().timer().atSecond(3)) {
				mazeFlashing.restart();
			} else if (mazeFlashing.isRunning()) {
				mazeFlashing.animate();
			}
		}
		case LEVEL_STARTING -> {
			gameController.state().timer().expire();
		}
		case HUNTING -> {
			if (!SoundManager.get().isAnySirenPlaying() && !game.player.powerTimer.isRunning()) {
				int sirenIndex = gameController.huntingTimer().phase() / 2;
				SoundManager.get().startSiren(sirenIndex);
			}
		}
		}
	}

	@Override
	public void end() {
		game.player.powerTimer.removeEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		SoundManager.get().setMuted(gameController.credit() == 0); // TODO

		switch (e.newGameState) {

		case READY -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::reset);
			r2D.mazeFlashing(game.level.mazeNumber).reset();
			player2D.reset();
			Stream.of(ghosts2D).forEach(Ghost2D::reset);
			SoundManager.get().stopAll();
			if (gameController.credit() > 0 && !gameController.isGameRunning()) {
				SoundManager.get().setMuted(false);
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}

		case HUNTING -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::restart);
			player2D.munchings.values().forEach(TimedSeq::restart);
			Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.animKicking.values().forEach(TimedSeq::restart));
		}

		case PACMAN_DYING -> {
			gameController.state().timer().setDurationSeconds(3).start();
			SoundManager.get().stopAll();
			player2D.dying.delay(60).onStart(() -> {
				game.ghosts().forEach(Ghost::hide);
				if (gameController.isGameRunning()) {
					SoundManager.get().play(GameSound.PACMAN_DEATH);
				}
			}).restart();
		}

		case GHOST_DYING -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::restart);
			SoundManager.get().play(GameSound.GHOST_EATEN);
		}

		case LEVEL_COMPLETE -> {
			player2D.reset();
			mazeFlashing = r2D.mazeFlashing(game.level.mazeNumber);
			SoundManager.get().stopAll();
		}

		case GAME_OVER -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::stop);
			SoundManager.get().stopAll();
		}

		default -> {
		}

		}

		// exit GHOST_DYING
		if (e.oldGameState == GameState.GHOST_DYING) {
			if (game.ghosts(GhostState.DEAD).count() > 0) {
				SoundManager.get().loop(GameSound.GHOST_RETURNING, Clip.LOOP_CONTINUOUSLY);
			}
		}
	}

	@Override
	public void onGameEvent(GameEvent gameEvent) {
		SoundManager.get().setMuted(gameController.credit() == 0); // TODO check
		super.onGameEvent(gameEvent);
	}

	@Override
	public void onScatterPhaseStarted(ScatterPhaseStartedEvent e) {
		SoundManager.get().stopSirens();
		SoundManager.get().startSiren(e.scatterPhase);
	}

	@Override
	public void onPlayerLostPower(GameEvent e) {
		SoundManager.get().stop(GameSound.PACMAN_POWER);
	}

	@Override
	public void onPlayerFoundFood(GameEvent e) {
		SoundManager.get().play(GameSound.PACMAN_MUNCH);
	}

	@Override
	public void onPlayerGotPower(GameEvent e) {
		game.ghosts(GhostState.FRIGHTENED).map(ghost -> ghosts2D[ghost.id]).forEach(ghost2D -> {
			ghost2D.animFlashing.reset();
			ghost2D.animFrightened.restart();
		});
		SoundManager.get().stopSirens();
		SoundManager.get().loop(GameSound.PACMAN_POWER, Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void onBonusActivated(GameEvent e) {
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.restart();
		}
	}

	@Override
	public void onBonusEaten(GameEvent e) {
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.reset();
		}
		SoundManager.get().play(GameSound.BONUS_EATEN);
	}

	@Override
	public void onPlayerGotExtraLife(GameEvent e) {
		SoundManager.get().play(GameSound.EXTRA_LIFE);
	}

	@Override
	public void onGhostStartedReturningHome(GameEvent e) {
		SoundManager.get().play(GameSound.GHOST_RETURNING);
	}

	@Override
	public void onGhostEnteredHouse(GameEvent e) {
		if (game.ghosts(GhostState.DEAD).count() == 0) {
			SoundManager.get().stop(GameSound.GHOST_RETURNING);
		}
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawMaze(g, game.level.mazeNumber, 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			r2D.hideEatenFood(g, game.level.world.tiles(), game.level.world::containsEatenFood);
			Stream.of(energizers2D).forEach(energizer2D -> energizer2D.render(g));
		}
		if (gameController.credit() == 0) {
			r2D.drawGameState(g, game, GameState.GAME_OVER);
		} else {
			r2D.drawGameState(g, game, gameController.state());
			r2D.drawLevelCounter(g, game, t(24), t(34));
		}
		bonus2D.render(g);
		player2D.render(g);
		Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.render(g));

		boolean showCredit = !gameController.isGameRunning() && gameController.credit() == 0;
		boolean showHighScoreOnly = !gameController.isGameRunning() && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;
		boolean showLivesCounter = gameController.credit() > 0 && !showHighScoreOnly;
		r2D.drawScore(g, game, showHighScoreOnly);
		if (showLivesCounter) {
			r2D.drawLivesCounter(g, game, t(2), t(34));
		}
		if (showCredit) {
			r2D.drawCredit(g, gameController.credit());
		}
	}

	private void handleGhostsFlashing(TickTimerEvent e) {
		if (e.type == TickTimerEvent.Type.HALF_EXPIRED) {
			game.ghosts(GhostState.FRIGHTENED).forEach(ghost -> {
				TimedSeq<?> flashing = ghosts2D[ghost.id].animFlashing;
				long frameTime = e.ticks / (game.level.numFlashes * flashing.numFrames());
				flashing.frameDuration(frameTime).repetitions(game.level.numFlashes).restart();
			});
		}
	}
}