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

import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.lib.anim.EntityAnimationByDirection;
import de.amr.games.pacman.lib.anim.FixedEntityAnimation;
import de.amr.games.pacman.lib.anim.SingleEntityAnimation;
import de.amr.games.pacman.lib.math.Vector2i;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Bonus;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.model.mspacman.MovingBonus;
import de.amr.games.pacman.model.pacman.StaticBonus;

/**
 * Spritesheet-based rendering for Pac-Man and Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public interface Rendering2D {

	public enum Mouth {
		CLOSED, OPEN, WIDE_OPEN
	}

	Font getArcadeFont();

	Color getGhostColor(int ghostID);

	// Sprites

	BufferedImage getSourceImage();

	BufferedImage getGhostSprite(int ghostID, Direction dir);

	BufferedImage getLifeSprite();

	BufferedImage getBonusSymbolSprite(int symbol);

	BufferedImage getBonusValueSprite(int symbol);

	// Animations

	SingleEntityAnimation<BufferedImage> createMazeFlashingAnimation(int mazeNumber);

	EntityAnimationByDirection createPacMunchingAnimationMap(Pac pac);

	SingleEntityAnimation<BufferedImage> createPacDyingAnimation();

	EntityAnimationByDirection createGhostColorAnimationMap(Ghost ghost);

	SingleEntityAnimation<BufferedImage> createGhostBlueAnimation();

	SingleEntityAnimation<BufferedImage> createGhostFlashingAnimation();

	EntityAnimationByDirection createGhostEyesAnimationMap(Ghost ghost);

	FixedEntityAnimation<BufferedImage> createGhostValueList();

	// Maze

	int mazeNumber(int levelNumber);

	Color getFoodColor(int mazeNumber);

	void drawFullMaze(Graphics2D g, int mazeNumber, int x, int y);

	// Drawing

	default void drawText(Graphics2D g, String text, Color color, Font font, int x, int y) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, x, y);
	}

	default void drawSprite(Graphics2D g, BufferedImage sprite, int x, int y) {
		if (sprite != null) {
			g.drawImage(sprite, x, y, null);
		}
	}

	default void drawSpriteCenteredOverBox(Graphics2D g, BufferedImage sprite, double x, double y) {
		if (sprite != null) {
			int dx = HTS - sprite.getWidth() / 2;
			int dy = HTS - sprite.getHeight() / 2;
			g.drawImage(sprite, (int) (x + dx), (int) (y + dy), null);
		}
	}

	default void drawEntity(Graphics2D g, Entity entity, BufferedImage sprite) {
		if (entity.isVisible() && sprite != null) {
			drawSpriteCenteredOverBox(g, sprite, (int) entity.position().x(), (int) entity.position().y());
		}
	}

	default void drawPac(Graphics2D g, Pac pac) {
		pac.animation().ifPresent(animation -> drawEntity(g, pac, (BufferedImage) animation.frame()));
	}

	default void drawGhost(Graphics2D g, Ghost ghost) {
		ghost.animation().ifPresent(animation -> drawEntity(g, ghost, (BufferedImage) animation.frame()));
	}

	default void drawBonus(Graphics2D g, Bonus bonus) {
		var sprite = switch (bonus.state()) {
		case INACTIVE -> null;
		case EDIBLE -> getBonusSymbolSprite(bonus.symbol());
		case EATEN -> getBonusValueSprite(bonus.symbol());
		};
		if (bonus instanceof StaticBonus) {
			drawEntity(g, bonus.entity(), sprite);
		} else if (bonus instanceof MovingBonus movingBonus) {
			float dy = movingBonus.dy();
			g.translate(0, dy);
			drawEntity(g, bonus.entity(), sprite);
			g.translate(0, -dy);
		}
	}

	default void drawDarkTiles(Graphics2D g, Stream<Vector2i> tiles, Predicate<Vector2i> fnDark) {
		g.setColor(Color.BLACK);
		tiles.filter(fnDark).forEach(tile -> g.fillRect(tile.x() * TS, tile.y() * TS, TS, TS));
	}

	default void drawCredit(Graphics2D g, int credit) {
		g.setFont(getArcadeFont());
		g.setColor(Color.WHITE);
		g.drawString("CREDIT  %d".formatted(credit), t(2), t(ArcadeWorld.SIZE_TILES.y()) - 2);
	}

	void drawCopyright(Graphics2D g, int x, int y);

	default void drawScores(Graphics2D g, GameModel game, boolean showHiscoreOnly) {
		g.setFont(getArcadeFont());
		g.translate(0, 2);
		g.setColor(new Color(222, 222, 255));
		g.drawString("SCORE", t(1), t(1));
		g.drawString("HIGH SCORE", t(15), t(1));
		g.translate(0, 1);
		if (showHiscoreOnly) {
			g.drawString("00", t(6), t(2));
		} else {
			game.score().ifPresent(score -> {
				g.drawString(String.format("%7d", score.points()), t(1), t(2));
				g.drawString(String.format("L%d", score.levelNumber()), t(9), t(2));
			});
		}
		var highScore = game.highScore();
		if (highScore.isPresent() && highScore.get().points() > 0) {
			g.drawString(String.format("%7d", highScore.get().points()), t(15), t(2));
			g.drawString(String.format("L%d", highScore.get().levelNumber()), t(23), t(2));
		} else {
			g.drawString("00", t(20), t(2));
		}
		g.translate(0, -3);
	}

	default void drawLivesCounter(Graphics2D g, GameModel game) {
		int numLivesDisplayed = game.isOneLessLifeDisplayed() ? game.lives() - 1 : game.lives();
		if (numLivesDisplayed <= 0) {
			return;
		}
		int x = t(2);
		int y = t(34);
		int maxLivesDisplayed = 5;
		for (int i = 0; i < Math.min(numLivesDisplayed, maxLivesDisplayed); ++i) {
			g.drawImage(getLifeSprite(), x + t(2 * i), y, null);
		}
		if (numLivesDisplayed > maxLivesDisplayed) {
			g.setColor(Color.YELLOW);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 6));
			g.drawString("+" + (numLivesDisplayed - maxLivesDisplayed), x + t(10), y + t(1) - 2);
		}
	}

	default void drawLevelCounter(Graphics2D g, List<Byte> levelCounter) {
		int x = t(24);
		for (var symbol : levelCounter) {
			drawSprite(g, getBonusSymbolSprite(symbol), x, t(34));
			x -= t(2);
		}
	}

	default void drawGameState(Graphics2D g, GameModel game, GameState gameState) {
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