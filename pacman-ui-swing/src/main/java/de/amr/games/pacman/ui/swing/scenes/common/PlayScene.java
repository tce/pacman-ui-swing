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
import javax.swing.Timer;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Bonus2D;
import de.amr.games.pacman.ui.swing.entity.common.Energizer2D;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D.PacAnimation;
import de.amr.games.pacman.ui.swing.rendering.common.Debug;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	private Pac2D player2D;
	private Ghost2D[] ghosts2D;
	private Energizer2D[] energizers2D;
	private Bonus2D bonus2D;
	private TimedSeq<BufferedImage> mazeFlashing;

	public PlayScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
	}

	private int mazeNumber(GameModel game) {
		if (game.variant == GameVariant.PACMAN) {
			return 1;
		}
		return switch (game.level.number) {
		case 1, 2 -> 1;
		case 3, 4, 5 -> 2;
		case 6, 7, 8, 9 -> 3;
		case 10, 11, 12, 13 -> 4;
		default -> (game.level.number - 14) % 8 < 4 ? 5 : 6;
		};
	}

	private void afterSeconds(double seconds, Runnable action) {
		Timer timer = new Timer(0, e -> action.run());
		timer.setInitialDelay((int) (seconds * 1000));
		timer.setRepeats(false);
		timer.start();
	}

	@Override
	public void init(GameModel game) {
		super.init(game);

		player2D = new Pac2D(game.pac, game, new PacAnimations(r2D));
		ghosts2D = game.ghosts().map(ghost -> new Ghost2D(ghost, game, new GhostAnimations(ghost.id, r2D)))
				.toArray(Ghost2D[]::new);
		energizers2D = game.level.world.energizerTiles().map(Energizer2D::new).toArray(Energizer2D[]::new);
		bonus2D = new Bonus2D(game, game.bonus());
		mazeFlashing = r2D.mazeFlashing(mazeNumber(game)).repetitions(game.level.numFlashes).reset();
//		game.pac.powerTimer.addEventListener(this::handleGhostsFlashing);
	}

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
			} else {
				mazeFlashing.advance();
			}
		}
		case LEVEL_STARTING -> {
			gameController.state().timer().expire();
		}
		default -> {
		}
		}
		updateSound();
	}

	private void updateSound() {
		if (gameController.credit() == 0) {
			return;
		}
		switch (gameController.state()) {
		case HUNTING -> {
			if (SoundManager.get().getClip(GameSound.PACMAN_MUNCH).isRunning() && game.pac.starvingTicks > 10) {
				SoundManager.get().stop(GameSound.PACMAN_MUNCH);
			}
			boolean scatterPhaseStarts = game.huntingTimer.scatteringPhase() >= 0 && game.huntingTimer.tick() == 0;
			if (scatterPhaseStarts) {
				SoundManager.get().stopSirens();
				SoundManager.get().startSiren(game.huntingTimer.scatteringPhase());
			}
			if (game.huntingTimer.chasingPhase() >= 0 && !SoundManager.get().isAnySirenPlaying()) {
				SoundManager.get().startSiren(game.huntingTimer.chasingPhase());
			}
		}
		default -> {
		}
		}
	}

	@Override
	public void end() {
//		game.pac.powerTimer.removeEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		SoundManager.get().setMuted(gameController.credit() == 0); // TODO

		switch (e.newGameState) {

		case READY -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::reset);
			r2D.mazeFlashing(mazeNumber(game)).reset();
			SoundManager.get().stopAll();
			if (gameController.credit() > 0 && !gameController.isGameRunning()) {
				SoundManager.get().setMuted(false);
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}

		case HUNTING -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::restart);
		}

		case PACMAN_DYING -> {
			gameController.state().timer().setDurationSeconds(4);
			gameController.state().timer().start();
			SoundManager.get().stopAll();
			player2D.animations.selectAnimation(PacAnimation.DYING);
			afterSeconds(1, () -> {
				game.ghosts().forEach(Ghost::hide);
			});
			afterSeconds(2, () -> {
				player2D.animations.selectedAnimation().run();
				if (gameController.isGameRunning()) {
					SoundManager.get().play(GameSound.PACMAN_DEATH);
				}
			});
		}

		case GHOST_DYING -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::restart);
			SoundManager.get().play(GameSound.GHOST_EATEN);
		}

		case LEVEL_COMPLETE -> {
			mazeFlashing = r2D.mazeFlashing(mazeNumber(game));
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
	public void onPlayerLosesPower(GameEvent e) {
		SoundManager.get().stop(GameSound.PACMAN_POWER);
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
	public void onBonusGetsActive(GameEvent e) {
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.restart();
		}
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.reset();
		}
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
	public void render(Graphics2D g) {
		r2D.drawMaze(g, mazeNumber(game), 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			r2D.drawEatenFood(g, game.level.world.tiles(), game.level.world::containsEatenFood);
			Stream.of(energizers2D).forEach(energizer2D -> energizer2D.render(g));
		}
		if (Debug.on) {
			Debug.drawMazeStructure(g, game);
		}
		if (gameController.credit() == 0) {
			r2D.drawGameState(g, game, GameState.GAME_OVER);
		} else {
			r2D.drawGameState(g, game, gameController.state());
			r2D.drawLevelCounter(g, game, t(24), t(34));
		}
		bonus2D.render(g, r2D);
		Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.render(g, r2D));

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
		if (Debug.on) {
			Debug.drawPlaySceneDebugInfo(g, gameController);
		}
	}
//
//	private void handleGhostsFlashing(TickTimerEvent e) {
//		if (e.type == TickTimerEvent.Type.HALF_EXPIRED) {
//			game.ghosts(GhostState.FRIGHTENED).forEach(ghost -> {
//				TimedSeq<?> flashing = ghosts2D[ghost.id].animFlashing;
//				long frameTime = e.ticks / (game.level.numFlashes * flashing.numFrames());
//				flashing.frameDuration(frameTime).repetitions(game.level.numFlashes).restart();
//			});
//		}
//	}
}