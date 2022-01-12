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
package de.amr.games.pacman.ui.swing.rendering.mspacman;

import static de.amr.games.pacman.ui.swing.assets.AssetLoader.font;
import static de.amr.games.pacman.ui.swing.assets.AssetLoader.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.mspacman.MsPacManGame;
import de.amr.games.pacman.ui.swing.assets.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Rendering for the Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class Rendering2D_MsPacMan extends Rendering2D {

	/** Sprite sheet order of directions. */
	static final List<Direction> order = List.of(Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN);

	static int index(Direction dir) {
		return order.indexOf(dir);
	}

	//@formatter:off

	static final Color[] FOOD_COLOR = {
		new Color(222, 222, 255),
		new Color(255, 255, 0),
		new Color(255, 0, 0),
		new Color(222, 222, 255),
		new Color(0, 255, 255),
		new Color(222, 222, 255),
	};

	static final Color[] MAZE_WALL_COLORS = { 
		new Color(255, 183, 174), 
		new Color(71, 183, 255), 
		new Color(222, 151, 81),
		new Color(33, 33, 255), 
		new Color(255, 183, 255), 
		new Color(255, 183, 174)
	};

	static final Color[] MAZE_WALL_BORDER_COLORS = { 
		new Color(255, 0, 0), 
		new Color(222, 222, 255),
		new Color(222, 222, 255), 
		new Color(255, 183, 81), 
		new Color(255, 255, 0), 
		new Color(255, 0, 0),
	};
	
	//@formatter:on

	final Spritesheet sheet;
	final Font scoreFont;
	final Map<String, BufferedImage> symbolSprites;
	final Map<Integer, BufferedImage> bonusNumberSprites;
	final Map<Integer, BufferedImage> bountyNumberSprites;
	final List<BufferedImage> mazeEmptyImages;
	final List<BufferedImage> mazeFullImages;
	final List<TimedSequence<BufferedImage>> mazesFlashingAnims;

	public Rendering2D_MsPacMan() {
		sheet = new Spritesheet(image("/mspacman/graphics/sprites.png"), 16);

		scoreFont = font("/emulogic.ttf", 8);

		// Left part of spritesheet contains the 6 mazes, rest is on the right
		mazeEmptyImages = new ArrayList<>(6);
		mazeFullImages = new ArrayList<>(6);
		mazesFlashingAnims = new ArrayList<>(6);
		for (int i = 0; i < 6; ++i) {
			mazeFullImages.add(sheet.image.getSubimage(0, i * 248, 226, 248));
			mazeEmptyImages.add(sheet.image.getSubimage(226, i * 248, 226, 248));
			BufferedImage mazeEmptyBright = sheet.createBrightEffect(mazeEmptyImages.get(i), getMazeWallBorderColor(i),
					getMazeWallColor(i));
			mazesFlashingAnims.add(TimedSequence.of(mazeEmptyBright, mazeEmptyImages.get(i)).frameDuration(15));
		}

		//@formatter:off
		symbolSprites = Map.of(
			MsPacManGame.CHERRIES,   s(3,0),
			MsPacManGame.STRAWBERRY, s(4,0),
			MsPacManGame.PEACH,      s(5,0),
			MsPacManGame.PRETZEL,    s(6,0),
			MsPacManGame.APPLE,      s(7,0),
			MsPacManGame.PEAR,       s(8,0),
			MsPacManGame.BANANA,     s(9,0)
		);

		bonusNumberSprites = Map.of(
			100, s(3, 1), 
			200, s(4, 1), 
			500, s(5, 1), 
			700, s(6, 1), 
			1000, s(7, 1), 
			2000, s(8, 1),
			5000, s(9, 1)
		);

		bountyNumberSprites = Map.of(
			200, s(0, 8), 
			400, s(1, 8), 
			800, s(2, 8), 
			1600, s(3, 8)
		);
		//@formatter:on
	}

	/**
	 * Picks sprite from the right part of the sheet, on the left are the maze images
	 */
	public BufferedImage s(int tileX, int tileY) {
		return sheet.sprite(456, 0, tileX, tileY);
	}

	/**
	 * Note: maze numbers are 1-based, maze index as stored here is 0-based.
	 * 
	 * @param mazeIndex 0-based maze index
	 * @return color of maze walls
	 */
	@Override
	public Color getMazeWallColor(int mazeIndex) {
		return MAZE_WALL_COLORS[mazeIndex];
	}

	/**
	 * Note: maze numbers are 1-based, maze index as stored here is 0-based.
	 * 
	 * @param mazeIndex 0-based maze index
	 * @return color of maze wall borders
	 */
	@Override
	public Color getMazeWallBorderColor(int mazeIndex) {
		return MAZE_WALL_BORDER_COLORS[mazeIndex];
	}

	@Override
	public TimedSequence<BufferedImage> createPlayerDyingAnimation() {
		return TimedSequence.of(s(0, 3), s(0, 0), s(0, 1), s(0, 2)).frameDuration(10).repetitions(2);
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createPlayerMunchingAnimations() {
		Map<Direction, TimedSequence<BufferedImage>> munchings = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			BufferedImage wide_open = s(0, d), open = s(1, d), closed = s(2, d);
			var animation = TimedSequence.of(open, closed, open, wide_open).frameDuration(2).endless().run();
			munchings.put(dir, animation);
		}
		return munchings;
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createSpouseMunchingAnimations() {
		Map<Direction, TimedSequence<BufferedImage>> munchings = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var munching = TimedSequence.of(s(0, 9 + d), s(1, 9 + d), s(2, 9)).frameDuration(2).endless();
			munchings.put(dir, munching);
		}
		return munchings;
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createGhostKickingAnimations(int ghostID) {
		EnumMap<Direction, TimedSequence<BufferedImage>> kickingByDir = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var kicking = TimedSequence.of(s(2 * d, 4 + ghostID), s(2 * d + 1, 4 + ghostID)).frameDuration(4).endless();
			kickingByDir.put(dir, kicking);
		}
		return kickingByDir;
	}

	@Override
	public TimedSequence<BufferedImage> createGhostFrightenedAnimation() {
		return TimedSequence.of(s(8, 4), s(9, 4)).frameDuration(8).endless().run();
	}

	@Override
	public TimedSequence<BufferedImage> createGhostFlashingAnimation() {
		return TimedSequence.of(s(8, 4), s(9, 4), s(10, 4), s(11, 4)).frameDuration(4);
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createGhostReturningHomeAnimations() {
		Map<Direction, TimedSequence<BufferedImage>> ghostEyesAnimByDir = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimByDir.put(dir, TimedSequence.of(s(8 + index(dir), 5)));
		}
		return ghostEyesAnimByDir;
	}

	@Override
	public TimedSequence<BufferedImage> mazeFlashing(int mazeNumber) {
		return mazesFlashingAnims.get(mazeNumber - 1);
	}

	@Override
	public TimedSequence<Integer> createBonusAnimation() {
		return TimedSequence.of(2, -2).frameDuration(15).endless();
	}

	@Override
	public TimedSequence<BufferedImage> createFlapAnimation() {
		return TimedSequence.of( //
				sheet.region(456, 208, 32, 32), //
				sheet.region(488, 208, 32, 32), //
				sheet.region(520, 208, 32, 32), //
				sheet.region(488, 208, 32, 32), //
				sheet.region(456, 208, 32, 32)//
		).repetitions(1).frameDuration(4);
	}

	@Override
	public TimedSequence<BufferedImage> createStorkFlyingAnimation() {
		return TimedSequence.of( //
				sheet.region(489, 176, 32, 16), //
				sheet.region(521, 176, 32, 16) //
		).endless().frameDuration(10);
	}

	@Override
	public BufferedImage getBlueBag() {
		return sheet.region(488, 199, 8, 8);
	}

	@Override
	public BufferedImage getJunior() {
		return sheet.region(509, 200, 8, 8);
	}

	@Override
	public BufferedImage getHeart() {
		return s(2, 10);
	}

	@Override
	public Map<Integer, BufferedImage> getBountyNumberSprites() {
		return bountyNumberSprites;
	}

	@Override
	public Map<Integer, BufferedImage> getBonusNumberSprites() {
		return bonusNumberSprites;
	}

	@Override
	public Map<String, BufferedImage> getSymbolSpritesMap() {
		return symbolSprites;
	}

	@Override
	public Font getScoreFont() {
		return scoreFont;
	}

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLOR[mazeNumber - 1];
	}

	@Override
	public BufferedImage lifeSprite() {
		return s(1, 0);
	}

	@Override
	public BufferedImage symbolSprite(String symbol) {
		return symbolSprites.get(symbol);
	}

	@Override
	public void drawMaze(Graphics2D g, int mazeNumber, int x, int y, boolean flashing) {
		if (flashing) {
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y, null);
		} else {
			g.drawImage(mazeFullImages.get(mazeNumber - 1), x, y, null);
		}
	}
}