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
package de.amr.games.pacman.ui.swing.rendering.common;

import static de.amr.games.pacman.model.world.PacManGameWorld.HTS;
import static de.amr.games.pacman.model.world.PacManGameWorld.TS;
import static de.amr.games.pacman.model.world.PacManGameWorld.t;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.PacManGameState;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameEntity;
import de.amr.games.pacman.model.common.GameModel;

/**
 * Spritesheet-based rendering for Pac-Man and Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public abstract class Rendering2D {

	public Color getGhostColor(int ghostID) {
		switch (ghostID) {
		case GameModel.RED_GHOST:
			return Color.RED;
		case GameModel.PINK_GHOST:
			return new Color(252, 181, 255);
		case GameModel.CYAN_GHOST:
			return Color.CYAN;
		case GameModel.ORANGE_GHOST:
			return new Color(253, 192, 90);
		default:
			return Color.WHITE;
		}
	}

	public abstract Map<Direction, TimedSequence<BufferedImage>> createPlayerMunchingAnimations();

	public abstract TimedSequence<BufferedImage> createPlayerDyingAnimation();

	public abstract Map<Direction, TimedSequence<BufferedImage>> createGhostKickingAnimations(int ghostID);

	public abstract TimedSequence<BufferedImage> createGhostFrightenedAnimation();

	public abstract TimedSequence<BufferedImage> createGhostFlashingAnimation();

	public abstract Map<Direction, TimedSequence<BufferedImage>> createGhostReturningHomeAnimations();

	public abstract Map<Integer, BufferedImage> getSymbolSpritesMap();

	public abstract Map<Integer, BufferedImage> getBountyNumberSprites();

	public abstract Map<Integer, BufferedImage> getBonusNumberSprites();

	public abstract BufferedImage symbolSprite(int symbol);

	public abstract BufferedImage lifeSprite();

	public abstract TimedSequence<BufferedImage> mazeFlashing(int mazeNumber);

	public abstract Color getMazeWallColor(int mazeIndex);

	public abstract Color getMazeWallBorderColor(int mazeIndex);

	/**
	 * @param mazeNumber the 1-based maze number
	 * @return color of pellets in this maze
	 */
	public abstract Color getFoodColor(int mazeNumber);

	public abstract Font getScoreFont();

	// only use in Pac-Man:

	public TimedSequence<BufferedImage> createBlinkyStretchedAnimation() {
		return null;
	}

	public TimedSequence<BufferedImage> createBlinkyDamagedAnimation() {
		return null;
	}

	// only used in Ms. Pac-Man:

	public TimedSequence<Integer> createBonusAnimation() {
		return null;
	}

	public Map<Direction, TimedSequence<BufferedImage>> createSpouseMunchingAnimations() {
		return null;
	}

	public TimedSequence<BufferedImage> createFlapAnimation() {
		return null;
	}

	public TimedSequence<BufferedImage> createStorkFlyingAnimation() {
		return null;
	}

	public BufferedImage getBlueBag() {
		return null;
	}

	public BufferedImage getJunior() {
		return null;
	}

	public BufferedImage getHeart() {
		return null;
	}

	// drawing

	public void renderEntity(Graphics2D g, GameEntity entity, BufferedImage sprite) {
		if (entity.visible && sprite != null) {
			int dx = HTS - sprite.getWidth() / 2, dy = HTS - sprite.getHeight() / 2;
			g.drawImage(sprite, (int) (entity.position.x + dx), (int) (entity.position.y + dy), null);
		}
	}

	public void hideEatenFood(Graphics2D g, Stream<V2i> tiles, Predicate<V2i> eaten) {
		g.setColor(Color.BLACK);
		tiles.filter(eaten).forEach(tile -> {
			g.fillRect(tile.x * TS, tile.y * TS, TS, TS);
		});
	}

	public abstract void drawMaze(Graphics2D g, int mazeNumber, int i, int t, boolean running);

	public void drawScore(Graphics2D g, GameModel game, boolean showHiscoreOnly) {
		g.setFont(getScoreFont());
		g.translate(0, 2);
		g.setColor(Color.WHITE);
		g.drawString("SCORE", t(1), t(1));
		g.drawString("HIGH SCORE", t(15), t(1));
		g.translate(0, 1);
		Color pointsColor = getMazeWallColor(game.mazeNumber - 1);
		if (pointsColor == Color.BLACK) {
			pointsColor = Color.YELLOW;
		}
		if (!showHiscoreOnly) {
			g.setColor(pointsColor);
			g.drawString(String.format("%08d", game.score), t(1), t(2));
			g.setColor(Color.LIGHT_GRAY);
			g.drawString(String.format("L%02d", game.levelNumber), t(9), t(2));
		}
		g.setColor(pointsColor);
		g.drawString(String.format("%08d", game.hiscorePoints), t(15), t(2));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(String.format("L%02d", game.hiscoreLevel), t(23), t(2));
		g.translate(0, -3);
	}

	public void drawLivesCounter(Graphics2D g, GameModel game, int x, int y) {
		int maxLivesDisplayed = 5;
		for (int i = 0; i < Math.min(game.player.lives, maxLivesDisplayed); ++i) {
			g.drawImage(lifeSprite(), x + t(2 * i), y, null);
		}
		if (game.player.lives > maxLivesDisplayed) {
			g.setColor(Color.YELLOW);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 6));
			g.drawString("+" + (game.player.lives - maxLivesDisplayed), x + t(10), y + t(1) - 2);
		}
	}

	public void drawLevelCounter(Graphics2D g, GameModel game, int rightX, int y) {
		int x = rightX;
		int firstLevel = Math.max(1, game.levelNumber - 6);
		for (int level = firstLevel; level <= game.levelNumber; ++level) {
			int symbol = game.levelSymbol(level);
			g.drawImage(symbolSprite(symbol), x, y, null);
			x -= t(2);
		}
	}

	public void drawGameState(Graphics2D g, GameModel game, PacManGameState gameState) {
		if (gameState == PacManGameState.READY) {
			g.setFont(getScoreFont());
			g.setColor(Color.YELLOW);
			g.drawString("READY!", t(11), t(21));
		} else if (gameState == PacManGameState.GAME_OVER) {
			g.setFont(getScoreFont());
			g.setColor(Color.RED);
			g.drawString("GAME", t(9), t(21));
			g.drawString("OVER", t(15), t(21));
		}
	}
}