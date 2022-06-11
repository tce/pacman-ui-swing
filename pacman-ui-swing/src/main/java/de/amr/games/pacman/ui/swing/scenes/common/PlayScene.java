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

import javax.sound.sampled.Clip;

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.animation.ThingAnimation;
import de.amr.games.pacman.lib.animation.ThingList;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.actors.BonusAnimationKey;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostState;
import de.amr.games.pacman.model.common.actors.PacAnimationKey;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Bonus2D;
import de.amr.games.pacman.ui.swing.lib.U;
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

	private Bonus2D bonus2D;
	private ThingList<BufferedImage> mazeFlashing;

	@Override
	public void init() {
		game.pac.setAnimations(new PacAnimations(r2D));
		game.pac.animations().ifPresent(ThingAnimation::ensureRunning);
		game.ghosts().forEach(ghost -> {
			ghost.setAnimations(new GhostAnimations(ghost.id, r2D));
			ghost.animations().get().ensureRunning();
		});
		mazeFlashing = r2D.mazeFlashing(r2D.mazeNumber(game.level.number));
		mazeFlashing.repeat(game.level.numFlashes);
		mazeFlashing.reset();
		bonus2D = new Bonus2D(game, new BonusAnimations(r2D), game.variant == GameVariant.MS_PACMAN);
		bonus2D.animations.select(BonusAnimationKey.ANIM_NONE);
	}

	@Override
	public void update() {
		updateMaze();
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

		r2D.drawMaze(g, r2D.mazeNumber(game.level.number), 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			r2D.drawDarkTiles(g, game.level.world.tiles(), tile -> game.level.world.containsEatenFood(tile)
					|| game.level.world.isEnergizerTile(tile) && !game.energizerPulse.frame());
		}
		DebugDraw.drawMazeStructure(g, game);

		if (!hasCredit) {
			r2D.drawGameState(g, game, GameState.GAME_OVER);
		} else {
			r2D.drawGameState(g, game, gameController.state());
		}

		bonus2D.render(g, r2D);
		r2D.drawPac(g, game.pac);
		game.ghosts().forEach(ghost -> r2D.drawGhost(g, ghost));

		DebugDraw.drawPlaySceneDebugInfo(g, gameController);

		if (showLivesCounter) {
			r2D.drawLivesCounter(g, game);
		}
		if (showLevelCounter) {
			r2D.drawLevelCounter(g, game);
		}
		if (showCredit) {
			r2D.drawCredit(g, gameController.credit());
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
		bonus2D.animations.select(BonusAnimationKey.ANIM_SYMBOL);
		bonus2D.startJumping();
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		bonus2D.animations.select(BonusAnimationKey.ANIM_VALUE);
		bonus2D.stopJumping();
		SoundManager.get().play(GameSound.BONUS_EATEN);
	}

	@Override
	public void onBonusExpires(GameEvent e) {
		bonus2D.animations.select(BonusAnimationKey.ANIM_NONE);
		bonus2D.stopJumping();
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
			r2D.mazeFlashing(r2D.mazeNumber(game.level.number)).reset();
			if (!playing) {
				SoundManager.get().play(GameSound.GAME_READY);
			}
		}
		case HUNTING -> {
			game.pac.animations().get().restart();
		}
		case PACMAN_DYING -> {
			gameController.state().timer().setSeconds(4.5);
			gameController.state().timer().start();
			SoundManager.get().stopAll();
			game.pac.animations().get().select(PacAnimationKey.ANIM_DYING);
			game.pac.animations().get().selectedAnimation().stop();
			U.afterSeconds(1, () -> {
				game.ghosts().forEach(Ghost::hide);
			});
			U.afterSeconds(2, () -> {
				SoundManager.get().play(GameSound.PACMAN_DEATH);
				game.pac.animations().get().selectedAnimation().run();
			});
			U.afterSeconds(4, () -> {
				game.pac.hide();
				game.pac.animations().get().select(PacAnimationKey.ANIM_MUNCHING);
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
			SoundManager.get().stopAll();
		}
		default -> {
		}
		}
	}
}