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

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.event.GameEvent;
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.model.common.GameSound;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.rendering.common.GhostAnimations;
import de.amr.games.pacman.ui.swing.rendering.common.PacAnimations;
import de.amr.games.pacman.ui.swing.shell.Keyboard;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	@Override
	public void init() {
		var world = (ArcadeWorld) game.world();
		world.setFlashingAnimation(r2D.createMazeFlashingAnimation(r2D.mazeNumber(game.level.number)));
		game.pac.setAnimations(new PacAnimations(game.pac, r2D));
		game.ghosts().forEach(ghost -> ghost.setAnimations(new GhostAnimations(ghost, r2D)));
	}

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.state().addCredit(game);
		}
	}

	@Override
	public void render(Graphics2D g) {
		boolean showHighScoreOnly = !game.playing && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;

		r2D.drawScores(g, game, showHighScoreOnly);
		drawMaze(g);
		r2D.drawBonus(g, game.bonus().entity());
		r2D.drawPac(g, game.pac);
		game.ghosts().forEach(ghost -> r2D.drawGhost(g, ghost));
		if (PacManGameUI.isDebugDraw()) {
			DebugDraw.drawPlaySceneDebugInfo(g, gameController);
		}
		if (hasCredit() && game.playing) {
			r2D.drawLivesCounter(g, game);
		}
		if (hasCredit()) {
			r2D.drawLevelCounter(g, game);
		}
		if (!hasCredit() && !game.playing) {
			r2D.drawCredit(g, game.credit);
		}
	}

	private void drawMaze(Graphics2D g) {
		var world = (ArcadeWorld) game.world();
		var mazeFlashing = world.flashingAnimation();
		if (mazeFlashing.isPresent() && mazeFlashing.get().isRunning()) {
			g.drawImage((Image) mazeFlashing.get().frame(), 0, t(3), null);
		} else {
			r2D.drawFullMaze(g, r2D.mazeNumber(game.level.number), 0, t(3));
			r2D.drawDarkTiles(g, game.level.world.tiles(), tile -> game.level.world.containsEatenFood(tile)
					|| game.level.world.isEnergizerTile(tile) && !game.energizerPulse.frame());
		}
		if (PacManGameUI.isDebugDraw()) {
			DebugDraw.drawMazeStructure(g, game);
		}
		r2D.drawGameState(g, game, hasCredit() ? gameController.state() : GameState.GAME_OVER);
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		if (e.newGameState == GameState.LEVEL_STARTING) {
			gameController.state().timer().expire(); // TODO check if needed
		}
	}

	@Override
	public void onBonusGetsEaten(GameEvent e) {
		gameController.sounds().ifPresent(snd -> snd.play(GameSound.BONUS_EATEN));
	}

	@Override
	public void onPlayerGetsExtraLife(GameEvent e) {
		gameController.sounds().ifPresent(snd -> snd.play(GameSound.EXTRA_LIFE));

	}
}