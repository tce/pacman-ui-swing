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
package de.amr.games.pacman.ui.swing.scenes.common;

import static de.amr.games.pacman.model.world.World.t;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import javax.sound.sampled.Clip;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.GameState;
import de.amr.games.pacman.controller.event.GameEvent;
import de.amr.games.pacman.controller.event.GameStateChangeEvent;
import de.amr.games.pacman.controller.event.ScatterPhaseStartedEvent;
import de.amr.games.pacman.lib.TickTimerEvent;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.model.common.GhostState;
import de.amr.games.pacman.ui.GameSounds;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Bonus2D;
import de.amr.games.pacman.ui.swing.entity.common.Energizer2D;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	private Player2D player2D;
	private List<Ghost2D> ghosts2D;
	private List<Energizer2D> energizers2D;
	private Bonus2D bonus2D;

	private TimedSeq<?> mazeFlashing;

	public PlayScene(PacManGameUI_Swing ui, Dimension size, Rendering2D r2D, SoundManager sounds) {
		super(ui, size, r2D, sounds);
	}

	@Override
	public void init(GameController gameController) {
		super.init(gameController);

		player2D = new Player2D(game.player, game, r2D);
		ghosts2D = game.ghosts().map(ghost -> new Ghost2D(ghost, game, r2D)).collect(Collectors.toList());
		energizers2D = game.world.energizerTiles().stream().map(Energizer2D::new).collect(Collectors.toList());
		bonus2D = new Bonus2D(game, r2D);
		mazeFlashing = r2D.mazeFlashing(game.mazeNumber).repetitions(game.numFlashes);
		mazeFlashing.reset();
		game.player.powerTimer.addEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void update() {
		if (gameController.state == GameState.LEVEL_COMPLETE) {
			playLevelCompleteAnimation(gameController.state);
		} else if (gameController.state == GameState.LEVEL_STARTING) {
			gameController.stateTimer().expire();
		}
	}

	@Override
	public void end() {
		game.player.powerTimer.removeEventListener(this::handleGhostsFlashing);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		sounds.setMuted(gameController.attractMode);

		// enter READY
		if (e.newGameState == GameState.READY) {
			sounds.stopAll();
			energizers2D.forEach(energizer2D -> energizer2D.getAnimation().reset());
			r2D.mazeFlashing(game.mazeNumber).reset();
			player2D.reset();
			ghosts2D.forEach(Ghost2D::reset);
			if (!gameController.attractMode && !gameController.gameRunning) {
				sounds.setMuted(false);
				sounds.play(GameSounds.GAME_READY);
			}
		}

		// enter HUNTING
		if (e.newGameState == GameState.HUNTING) {
			energizers2D.forEach(energizer2D -> energizer2D.getAnimation().restart());
			player2D.munchings.values().forEach(TimedSeq::restart);
			ghosts2D.forEach(ghost2D -> {
				ghost2D.animKicking.values().forEach(TimedSeq::restart);
			});
		}

		// enter PACMAN_DYING
		if (e.newGameState == GameState.PACMAN_DYING) {
			gameController.stateTimer().setSeconds(3).start();
			sounds.stopAll();
			ghosts2D.forEach(ghost2D -> {
				ghost2D.animKicking.values().forEach(TimedSeq::reset);
			});
			player2D.dying.delay(60).onStart(() -> {
				game.hideGhosts();
				if (gameController.gameRunning) {
					sounds.play(GameSounds.PACMAN_DEATH);
				}
			}).restart();
		}

		// enter GHOST_DYING
		if (e.newGameState == GameState.GHOST_DYING) {
			sounds.play(GameSounds.GHOST_EATEN);
			energizers2D.forEach(energizer2D -> energizer2D.getAnimation().restart());
		}

		// exit GHOST_DYING
		if (e.oldGameState == GameState.GHOST_DYING) {
			// the dead ghost(s) will return home now
			if (game.ghosts(GhostState.DEAD).count() > 0) {
				sounds.loop(GameSounds.GHOST_RETURNING, Clip.LOOP_CONTINUOUSLY);
			}
		}

		// enter LEVEL_COMPLETE
		if (e.newGameState == GameState.LEVEL_COMPLETE) {
			player2D.reset();
			mazeFlashing = r2D.mazeFlashing(game.mazeNumber);
			sounds.stopAll();
		}

		// enter GAME_OVER
		if (e.newGameState == GameState.GAME_OVER) {
			ghosts2D.forEach(ghost2D -> {
				ghost2D.animKicking.values().forEach(TimedSeq::reset);
			});
		}
	}

	@Override
	public void onGameEvent(GameEvent gameEvent) {
		sounds.setMuted(gameController.attractMode);
		super.onGameEvent(gameEvent);
	}

	@Override
	public void onScatterPhaseStarted(ScatterPhaseStartedEvent e) {
		if (e.scatterPhase > 0) {
			sounds.stop(GameSounds.SIRENS.get(e.scatterPhase - 1));
		}
		sounds.loop(GameSounds.SIRENS.get(e.scatterPhase), Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void onPlayerLostPower(GameEvent e) {
		sounds.stop(GameSounds.PACMAN_POWER);
	}

	@Override
	public void onPlayerFoundFood(GameEvent e) {
		sounds.play(GameSounds.PACMAN_MUNCH);
	}

	@Override
	public void onPlayerGainsPower(GameEvent e) {
		game.ghosts(GhostState.FRIGHTENED).map(ghost -> ghosts2D.get(ghost.id)).forEach(ghost2D -> {
			ghost2D.animFlashing.reset();
			ghost2D.animFrightened.restart();
		});
		sounds.loop(GameSounds.PACMAN_POWER, Clip.LOOP_CONTINUOUSLY);
	}

	@Override
	public void onBonusActivated(GameEvent e) {
		bonus2D.bonus = game.bonus;
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.restart();
		}
	}

	@Override
	public void onBonusEaten(GameEvent e) {
		sounds.play(GameSounds.BONUS_EATEN);
		if (bonus2D.jumpAnimation != null) {
			bonus2D.jumpAnimation.reset();
		}
	}

	@Override
	public void onExtraLife(GameEvent e) {
		sounds.play(GameSounds.EXTRA_LIFE);
		ui.showFlashMessage(1, "Extra life!");
	}

	@Override
	public void onGhostReturnsHome(GameEvent e) {
		sounds.play(GameSounds.GHOST_RETURNING);
	}

	@Override
	public void onGhostEntersHouse(GameEvent e) {
		if (game.ghosts(GhostState.DEAD).count() == 0) {
			sounds.stop(GameSounds.GHOST_RETURNING);
		}
	};

	@Override
	public void render(Graphics2D g) {
		r2D.drawMaze(g, game.mazeNumber, 0, t(3), mazeFlashing.isRunning());
		if (!mazeFlashing.isRunning()) {
			r2D.hideEatenFood(g, game.world.tiles(), game.world::isFoodEaten);
			energizers2D.forEach(energizer2D -> energizer2D.render(g));
		}
		if (gameController.attractMode) {
			r2D.drawGameState(g, game, GameState.GAME_OVER);
		} else {
			r2D.drawGameState(g, game, gameController.state);
			r2D.drawLevelCounter(g, game, t(25), t(34));
		}
		bonus2D.render(g);
		player2D.render(g);
		ghosts2D.forEach(ghost2D -> {
			ghost2D.render(g);
		});
		if (gameController.gameRunning) {
			r2D.drawScore(g, game, false);
			r2D.drawLivesCounter(g, game, t(2), t(34));
		} else {
			r2D.drawScore(g, game, true);
		}
	}

	private void handleGhostsFlashing(TickTimerEvent e) {
		if (e.type == TickTimerEvent.Type.HALF_EXPIRED) {
			game.ghosts(GhostState.FRIGHTENED).forEach(ghost -> {
				TimedSeq<?> flashing = ghosts2D.get(ghost.id).animFlashing;
				long frameTime = e.ticks / (game.numFlashes * flashing.numFrames());
				flashing.frameDuration(frameTime).repetitions(game.numFlashes).restart();
			});
		}
	}

	private void playLevelCompleteAnimation(GameState state) {
		if (gameController.stateTimer().isRunningSeconds(2)) {
			game.hideGhosts();
		}
		if (gameController.stateTimer().isRunningSeconds(3)) {
			mazeFlashing.restart();
		}
		mazeFlashing.animate();
		if (mazeFlashing.isComplete()) {
			gameController.stateTimer().expire();
		}
	}
}