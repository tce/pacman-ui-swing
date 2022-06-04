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

import static de.amr.games.pacman.lib.TickTimer.sec_to_ticks;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import javax.sound.sampled.Clip;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostAnimation;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.model.common.actors.PacAnimation;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Bonus2D;
import de.amr.games.pacman.ui.swing.entity.common.Energizer2D;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Pac2D;
import de.amr.games.pacman.ui.swing.lib.U;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.rendering.common.MyGhostAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.common.MyPacAnimationSet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	private Pac2D pac2D;
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

		pac2D = new Pac2D(game.pac, game, new MyPacAnimationSet(r2D));
		ghosts2D = game.ghosts().map(ghost -> new Ghost2D(ghost, game, new MyGhostAnimationSet(ghost.id, r2D)))
				.toArray(Ghost2D[]::new);
		energizers2D = game.level.world.energizerTiles().map(Energizer2D::new).toArray(Energizer2D[]::new);
		var jumpAnimation = game.variant == GameVariant.MS_PACMAN ? Rendering2D_MsPacMan.get().createBonusAnimation()
				: null;
		bonus2D = new Bonus2D(game, game.bonus(), jumpAnimation);
		mazeFlashing = r2D.mazeFlashing(r2D.mazeNumber(game.level.number)).repetitions(game.level.numFlashes).reset();
	}

	@Override
	public void update() {
		updateMaze();
		updateAnimations();
		updateSound();
	}

	// TODO check this
	private void updateMaze() {
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
	}

	private void updateAnimations() {
		long recoveringTicks = sec_to_ticks(2); // TODO not sure about recovering duration
		boolean recoveringStarts = game.pac.powerTimer.remaining() == recoveringTicks;
		boolean recovering = game.pac.powerTimer.remaining() <= recoveringTicks;
		for (var ghost2D : ghosts2D) {
			if (recoveringStarts) {
				// TODO avoid cast
				MyGhostAnimationSet animations = (MyGhostAnimationSet) ghost2D.animations;
				animations.startFlashing(game.level.numFlashes, recoveringTicks);
			}
			ghost2D.updateAnimation(game.pac.hasPower(), recovering);
		}
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
			if (game.huntingTimer.tick() == 0) {
				SoundManager.get().stopSirens();
				SoundManager.get().startSiren(game.huntingTimer.phase() / 2);
			}
		}
		default -> {
		}
		}
	}

	@Override
	public void render(Graphics2D g) {
		boolean playing = gameController.isGameRunning();
		boolean hasCredit = gameController.credit() > 0;

		boolean showCredit = !hasCredit && !playing;
		boolean showLivesCounter = hasCredit && playing;
		boolean showLevelCounter = hasCredit;
		boolean showHighScoreOnly = !playing && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;

		r2D.drawScore(g, game, showHighScoreOnly);
		if (showLivesCounter) {
			r2D.drawLivesCounter(g, game);
		}
		if (showCredit) {
			r2D.drawCredit(g, gameController.credit());
		}
		if (showLevelCounter) {
			r2D.drawLevelCounter(g, game);
		}

		r2D.drawMaze(g, r2D.mazeNumber(game.level.number), 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			r2D.drawEatenFood(g, game.level.world.tiles(), game.level.world::containsEatenFood);
			Stream.of(energizers2D).forEach(energizer2D -> energizer2D.render(g));
		}
		DebugDraw.drawMazeStructure(g, game);

		if (!hasCredit) {
			r2D.drawGameState(g, game, GameState.GAME_OVER);
		} else {
			r2D.drawGameState(g, game, gameController.state());
		}

		bonus2D.render(g, r2D);
		pac2D.render(g, r2D);
		Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.render(g, r2D));

		DebugDraw.drawPlaySceneDebugInfo(g, gameController);
	}

	@Override
	public void onGameEvent(GameEvent gameEvent) {
		SoundManager.get().setMuted(gameController.credit() == 0); // TODO check
		super.onGameEvent(gameEvent);
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
	public void onBonusGetsActive(GameEvent e) {
		bonus2D.startJumping();
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		bonus2D.stopJumping();
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
		boolean playing = gameController.isGameRunning();
		switch (e.newGameState) {
		case READY -> {
			SoundManager.get().stopAll();
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::reset);
			r2D.mazeFlashing(r2D.mazeNumber(game.level.number)).reset();
			if (!playing) {
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}
		case HUNTING -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::restart);
			pac2D.animations.restart();
			Stream.of(ghosts2D).forEach(ghost2D -> ghost2D.animations.restart(GhostAnimation.COLOR));
		}
		case PACMAN_DYING -> {
			gameController.state().timer().setDurationSeconds(4.5);
			gameController.state().timer().start();
			SoundManager.get().stopAll();
			pac2D.animations.selectAnimation(PacAnimation.DYING);
			pac2D.animations.selectedAnimation().stop();
			U.afterSeconds(1, () -> {
				game.ghosts().forEach(Ghost::hide);
			});
			U.afterSeconds(2, () -> {
				SoundManager.get().play(GameSound.PACMAN_DEATH);
				pac2D.animations.selectedAnimation().run();
			});
			U.afterSeconds(4, () -> {
				game.pac.hide();
				pac2D.animations.selectAnimation(PacAnimation.MUNCHING);
			});
		}
		case GHOST_DYING -> {
			game.pac.hide();
			SoundManager.get().play(GameSound.GHOST_EATEN);
		}
		case LEVEL_COMPLETE -> {
			SoundManager.get().stopAll();
			mazeFlashing = r2D.mazeFlashing(r2D.mazeNumber(game.level.number));
		}
		case GAME_OVER -> {
			Stream.of(energizers2D).map(Energizer2D::getAnimation).forEach(TimedSeq::stop);
			SoundManager.get().stopAll();
		}
		default -> {
		}
		}
	}
}