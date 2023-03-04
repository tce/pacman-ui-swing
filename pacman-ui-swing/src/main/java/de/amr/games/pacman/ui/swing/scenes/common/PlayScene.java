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
import de.amr.games.pacman.event.GameStateChangeEvent;
import de.amr.games.pacman.lib.anim.AnimationKey;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.swing.rendering.common.DebugDraw;
import de.amr.games.pacman.ui.swing.shell.Keyboard;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI;

/**
 * The play scene for Pac-Man and Ms. Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PlayScene extends GameScene {

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.state().addCredit(game);
		}
	}

	@Override
	public void render(Graphics2D g) {
		game.level().ifPresent(level -> {
			drawMaze(g, level.world(), r2D.mazeNumber(level.number()));
			r2D.drawBonus(g, level.bonus());
			r2D.drawGameState(g, game, game.hasCredit() ? gameController.state() : GameState.GAME_OVER);
			r2D.drawPac(g, level.pac());
			r2D.drawGhost(g, level.ghost(Ghost.ID_ORANGE_GHOST));
			r2D.drawGhost(g, level.ghost(Ghost.ID_CYAN_GHOST));
			r2D.drawGhost(g, level.ghost(Ghost.ID_PINK_GHOST));
			r2D.drawGhost(g, level.ghost(Ghost.ID_RED_GHOST));
			if (PacManGameUI.isDebugDraw()) {
				DebugDraw.drawPlaySceneDebugInfo(g, gameController);
			}
		});
		boolean highScoreOnly = !game.isPlaying() && gameController.state() != GameState.READY
				&& gameController.state() != GameState.GAME_OVER;
		r2D.drawScores(g, game, highScoreOnly);
		if (game.hasCredit()) {
			r2D.drawLivesCounter(g, game);
		} else {
			r2D.drawCredit(g, game.credit());
		}
		r2D.drawLevelCounter(g, game.levelCounter());
	}

	private void drawMaze(Graphics2D g, World world, int mazeNumber) {
		var flashing = world.animation(AnimationKey.MAZE_FLASHING);
		if (flashing.isPresent() && flashing.get().isRunning()) {
			g.drawImage((Image) flashing.get().frame(), 0, t(3), null);
		} else {
			r2D.drawFullMaze(g, mazeNumber, 0, t(3));
			var energizerPulse = world.animation(AnimationKey.MAZE_ENERGIZER_BLINKING);
			if (energizerPulse.isPresent()) {
				boolean dark = !(boolean) energizerPulse.get().frame();
				r2D.drawDarkTiles(g, world.tiles(),
						tile -> world.containsEatenFood(tile) || world.isEnergizerTile(tile) && dark);
			} else {
				r2D.drawDarkTiles(g, world.tiles(), world::containsEatenFood);
			}
		}
		if (PacManGameUI.isDebugDraw()) {
			DebugDraw.drawMazeStructure(g, world);
		}
	}

	@Override
	public void onGameStateChange(GameStateChangeEvent e) {
		if (e.newGameState == GameState.CHANGING_TO_NEXT_LEVEL) {
			gameController.terminateCurrentState(); // TODO check if needed
		}
	}
}