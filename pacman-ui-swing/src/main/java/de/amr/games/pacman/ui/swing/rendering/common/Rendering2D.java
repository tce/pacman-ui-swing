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
package de.amr.games.pacman.ui.swing.rendering.common;

import static de.amr.games.pacman.model.common.Ghost.CYAN_GHOST;
import static de.amr.games.pacman.model.common.Ghost.ORANGE_GHOST;
import static de.amr.games.pacman.model.common.Ghost.PINK_GHOST;
import static de.amr.games.pacman.model.common.Ghost.RED_GHOST;
import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.Entity;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.world.ArcadeWorld;

/**
 * Spritesheet-based rendering for Pac-Man and Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public abstract class Rendering2D {

	public Color getGhostColor(int ghostID) {
		return switch (ghostID) {
		case RED_GHOST -> Color.RED;
		case PINK_GHOST -> new Color(252, 181, 255);
		case CYAN_GHOST -> Color.CYAN;
		case ORANGE_GHOST -> new Color(253, 192, 90);
		default -> Color.WHITE;
		};
	}

	public abstract BufferedImage s(int tileX, int tileY);

	public abstract Map<Direction, TimedSeq<BufferedImage>> createPlayerMunchingAnimations();

	public abstract TimedSeq<BufferedImage> createPlayerDyingAnimation();

	public abstract Map<Direction, TimedSeq<BufferedImage>> createGhostKickingAnimations(int ghostID);

	public abstract TimedSeq<BufferedImage> createGhostFrightenedAnimation();

	public abstract TimedSeq<BufferedImage> createGhostFlashingAnimation();

	public abstract Map<Direction, TimedSeq<BufferedImage>> createGhostReturningHomeAnimations();

	public abstract Map<Integer, BufferedImage> getSymbolSpritesMap();

	public abstract Map<Integer, BufferedImage> getBountyNumberSprites();

	public abstract Map<Integer, BufferedImage> getBonusNumberSprites();

	public abstract BufferedImage symbolSprite(int symbol);

	public abstract BufferedImage lifeSprite();

	public abstract TimedSeq<BufferedImage> mazeFlashing(int mazeNumber);

	public abstract Color getMazeWallColor(int mazeIndex);

	public abstract Color getMazeWallBorderColor(int mazeIndex);

	/**
	 * @param mazeNumber the 1-based maze number
	 * @return color of pellets in this maze
	 */
	public abstract Color getFoodColor(int mazeNumber);

	public abstract Font getArcadeFont();

	// only use in Pac-Man:

	public TimedSeq<BufferedImage> createBlinkyStretchedAnimation() {
		return null;
	}

	public TimedSeq<BufferedImage> createBlinkyDamagedAnimation() {
		return null;
	}

	// only used in Ms. Pac-Man:

	public TimedSeq<Integer> createBonusAnimation() {
		return null;
	}

	public Map<Direction, TimedSeq<BufferedImage>> createSpouseMunchingAnimations() {
		return null;
	}

	public TimedSeq<BufferedImage> createFlapAnimation() {
		return null;
	}

	public TimedSeq<BufferedImage> createStorkFlyingAnimation() {
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

	public void renderEntity(Graphics2D g, Entity entity, BufferedImage sprite) {
		if (entity.visible && sprite != null) {
			renderSprite(g, sprite, (int) entity.position.x, (int) entity.position.y);
		}
	}

	public void renderSprite(Graphics2D g, BufferedImage sprite, int x, int y) {
		int dx = HTS - sprite.getWidth() / 2, dy = HTS - sprite.getHeight() / 2;
		g.drawImage(sprite, x + dx, y + dy, null);
	}

	public void hideEatenFood(Graphics2D g, Stream<V2i> tiles, Predicate<V2i> eaten) {
		g.setColor(Color.BLACK);
		tiles.filter(eaten).forEach(tile -> {
			g.fillRect(tile.x * TS, tile.y * TS, TS, TS);
		});
	}

	public abstract void drawMaze(Graphics2D g, int mazeNumber, int i, int t, boolean running);

	public void drawCredit(Graphics2D g, int credit) {
		g.setFont(getArcadeFont());
		g.setColor(Color.WHITE);
		g.drawString("CREDIT  %d".formatted(credit), t(2), t(ArcadeWorld.TILES_Y) - 2);
	}

	public abstract void drawCopyright(Graphics2D g, int x, int y);

	public void drawScore(Graphics2D g, GameModel game, boolean showHiscoreOnly) {
		g.setFont(getArcadeFont());
		g.translate(0, 2);
		g.setColor(Color.WHITE);
		g.drawString("SCORE", t(1), t(1));
		g.drawString("HIGH SCORE", t(15), t(1));
		g.translate(0, 1);
		// Score
		g.setColor(Color.WHITE);
		if (showHiscoreOnly) {
			g.drawString("00", t(6), t(2));
		} else {
			g.drawString(String.format("%7d", game.score), t(1), t(2));
			g.setColor(Color.LIGHT_GRAY);
			g.drawString(String.format("L%d", game.level.number), t(9), t(2));
		}
		// Highscore
		g.setColor(Color.WHITE);
		g.drawString(String.format("%7d", game.highscorePoints), t(15), t(2));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(String.format("L%d", game.highscoreLevel), t(23), t(2));
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
		int firstLevel = Math.max(1, game.level.number - 6);
		for (int levelNumber = firstLevel; levelNumber <= game.level.number; ++levelNumber) {
			int symbol = game.levelCounter.get(levelNumber - 1);
			g.drawImage(symbolSprite(symbol), x, y, null);
			x -= t(2);
		}
	}

	public void drawGameState(Graphics2D g, GameModel game, GameState gameState) {
		if (gameState == GameState.READY) {
			g.setFont(getArcadeFont());
			g.setColor(Color.YELLOW);
			g.drawString("READY!", t(11), t(21));
		} else if (gameState == GameState.GAME_OVER) {
			g.setFont(getArcadeFont());
			g.setColor(Color.RED);
			g.drawString("GAME", t(9), t(21));
			g.drawString("OVER", t(15), t(21));
		}
	}
}