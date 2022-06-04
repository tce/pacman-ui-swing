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
package de.amr.games.pacman.ui.swing.rendering.mspacman;

import static de.amr.games.pacman.model.common.world.World.t;
import static de.amr.games.pacman.ui.swing.lib.U.font;
import static de.amr.games.pacman.ui.swing.lib.U.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.SpriteAnimation;
import de.amr.games.pacman.lib.SpriteAnimationMap;
import de.amr.games.pacman.lib.GenericAnimation;
import de.amr.games.pacman.model.mspacman.MsPacManGame;
import de.amr.games.pacman.ui.swing.assets.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Rendering for the Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class Rendering2D_MsPacMan implements Rendering2D {

	/** Sprite sheet order of directions. */
	static final List<Direction> order = List.of(Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN);

	static int index(Direction dir) {
		return order.indexOf(dir);
	}

	//@formatter:off
	static final Color[] GHOST_COLORS = {
		Color.RED,
		new Color(252, 181, 255),
		Color.CYAN,
		new Color(253, 192, 90)
	};
	
	static final Color[] MAZE_TOP_COLORS = { 
		new Color(255, 183, 174), 
		new Color(71, 183, 255), 
		new Color(222, 151, 81),
		new Color(33, 33, 255), 
		new Color(255, 183, 255), 
		new Color(255, 183, 174)
	};

	static final Color[] MAZE_SIDE_COLORS = { 
		new Color(255, 0, 0), 
		new Color(222, 222, 255),
		new Color(222, 222, 255), 
		new Color(255, 183, 81), 
		new Color(255, 255, 0), 
		new Color(255, 0, 0),
	};

	static final Color[] FOOD_COLORS = {
		new Color(222, 222, 255),
		new Color(255, 255, 0),
		new Color(255, 0, 0),
		new Color(222, 222, 255),
		new Color(0, 255, 255),
		new Color(222, 222, 255),
	};
	//@formatter:on

	private static final Rendering2D_MsPacMan theThing = new Rendering2D_MsPacMan("/mspacman/graphics/sprites.png", 16);

	public static Rendering2D_MsPacMan get() {
		return theThing;
	}

	private final Spritesheet ss;
	private final BufferedImage midwayLogo;
	private final BufferedImage[] mazeFull;
	private final BufferedImage[] mazeEmpty;
	private final List<GenericAnimation<BufferedImage>> mazeFlashings;
	private final BufferedImage[] symbols;
	private final Map<Integer, BufferedImage> bonusValues;
	private final Map<Integer, BufferedImage> bountyNumbers;
	private final Font font;

	private Rendering2D_MsPacMan(String path, int rasterSize) {
		ss = new Spritesheet(image(path), rasterSize);
		font = font("/common/emulogic.ttf", 8);
		midwayLogo = image("/mspacman/graphics/midway.png");

		//@formatter:off
		symbols = new BufferedImage[7];
		symbols[MsPacManGame.CHERRIES]   = rhs(3,0);
		symbols[MsPacManGame.STRAWBERRY] = rhs(4,0);
		symbols[MsPacManGame.PEACH]      = rhs(5,0);
		symbols[MsPacManGame.PRETZEL]    = rhs(6,0);
		symbols[MsPacManGame.APPLE]      = rhs(7,0);
		symbols[MsPacManGame.PEAR]       = rhs(8,0);
		symbols[MsPacManGame.BANANA]     = rhs(9,0);

		bonusValues = Map.of(
			100,  rhs(3, 1), 
			200,  rhs(4, 1), 
			500,  rhs(5, 1), 
			700,  rhs(6, 1), 
			1000, rhs(7, 1), 
			2000, rhs(8, 1),
			5000, rhs(9, 1)
		);

		bountyNumbers = Map.of(
			200,  rhs(0, 8), 
			400,  rhs(1, 8), 
			800,  rhs(2, 8), 
			1600, rhs(3, 8)
		);
		//@formatter:on

		int numMazes = 6;
		mazeEmpty = new BufferedImage[numMazes];
		mazeFull = new BufferedImage[numMazes];
		mazeFlashings = new ArrayList<>(numMazes);
		for (int mazeIndex = 0; mazeIndex < 6; ++mazeIndex) {
			mazeFull[mazeIndex] = ss.image.getSubimage(0, mazeIndex * 248, 226, 248);
			mazeEmpty[mazeIndex] = ss.image.getSubimage(228, mazeIndex * 248, 226, 248);
			var mazeEmptyBright = ss.createBrightEffect(mazeEmpty[mazeIndex], MAZE_SIDE_COLORS[mazeIndex],
					MAZE_TOP_COLORS[mazeIndex]);
			mazeFlashings.add(GenericAnimation.of(mazeEmptyBright, mazeEmpty[mazeIndex]).frameDuration(20));
		}
	}

	/**
	 * Picks sprite from the right part of the sheet, on the left are the maze images
	 */
	public BufferedImage rhs(int tileX, int tileY) {
		return ss.tilesFrom(456, 0, tileX, tileY, 1, 1);
	}

	@Override
	public Spritesheet spritesheet() {
		return ss;
	}

	@Override
	public Font getArcadeFont() {
		return font;
	}

	@Override
	public Color getGhostColor(int ghostID) {
		return GHOST_COLORS[ghostID];
	}

	@Override
	public int mazeNumber(int levelNumber) {
		return switch (levelNumber) {
		case 1, 2 -> 1;
		case 3, 4, 5 -> 2;
		case 6, 7, 8, 9 -> 3;
		case 10, 11, 12, 13 -> 4;
		default -> (levelNumber - 14) % 8 < 4 ? 5 : 6;
		};
	}

	@Override
	public SpriteAnimation<BufferedImage> createPacDyingAnimation() {
		return new SpriteAnimation<>(rhs(0, 3), rhs(0, 0), rhs(0, 1), rhs(0, 2)).frameDuration(10).repetitions(2);
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createPacMunchingAnimations() {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var wide = rhs(0, d);
			var middle = rhs(1, d);
			var closed = rhs(2, d);
			var animation = new SpriteAnimation<>(middle, closed, middle, wide).frameDuration(2).endless();
			map.put(dir, animation);
		}
		return map;
	}

	public SpriteAnimationMap<Direction, BufferedImage> createSpouseMunchingAnimations() {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var munching = new SpriteAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9)).frameDuration(2).endless();
			map.put(dir, munching);
		}
		return map;
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createGhostColorAnimation(int ghostID) {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var color = new SpriteAnimation<>(rhs(2 * d, 4 + ghostID), rhs(2 * d + 1, 4 + ghostID)).frameDuration(4)
					.endless();
			map.put(dir, color);
		}
		return map;
	}

	@Override
	public SpriteAnimation<BufferedImage> createGhostBlueAnimation() {
		return new SpriteAnimation<>(rhs(8, 4), rhs(9, 4)).frameDuration(8).endless();
	}

	@Override
	public SpriteAnimation<BufferedImage> createGhostFlashingAnimation() {
		return new SpriteAnimation<>(rhs(8, 4), rhs(9, 4), rhs(10, 4), rhs(11, 4)).frameDuration(4);
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createGhostEyesAnimation() {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			map.put(dir, new SpriteAnimation<>(rhs(8 + index(dir), 5)));
		}
		return map;
	}

	@Override
	public GenericAnimation<BufferedImage> mazeFlashing(int mazeNumber) {
		return mazeFlashings.get(mazeNumber - 1);
	}

	public GenericAnimation<Integer> createBonusAnimation() {
		return GenericAnimation.of(2, -2).frameDuration(10).endless();
	}

	public SpriteAnimationMap<Direction, BufferedImage> createHusbandMunchingAnimations() {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (var dir : Direction.values()) {
			int d = index(dir);
			map.put(dir, new SpriteAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9)).frameDuration(2).endless());
		}
		return map;
	}

	public GenericAnimation<BufferedImage> createFlapAnimation() {
		return GenericAnimation.of( //
				ss.si(456, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(520, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(456, 208, 32, 32)//
		).repetitions(1).frameDuration(4);
	}

	public GenericAnimation<BufferedImage> createStorkFlyingAnimation() {
		return GenericAnimation.of( //
				ss.si(489, 176, 32, 16), //
				ss.si(521, 176, 32, 16) //
		).endless().frameDuration(10);
	}

	public BufferedImage getBlueBag() {
		return ss.si(488, 199, 8, 8);
	}

	public BufferedImage getJunior() {
		return ss.si(509, 200, 8, 8);
	}

	public BufferedImage getHeart() {
		return rhs(2, 10);
	}

	@Override
	public BufferedImage getNumberSprite(int number) {
		return bountyNumbers.get(number);
	}

	@Override
	public BufferedImage getBonusValueSprite(int number) {
		return bonusValues.get(number);
	}

	@Override
	public BufferedImage getSymbolSprite(int symbol) {
		return symbols[symbol];
	}

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLORS[mazeNumber - 1];
	}

	@Override
	public BufferedImage getLifeSprite() {
		return rhs(1, 0);
	}

	@Override
	public void drawMaze(Graphics2D g, int mazeNumber, int x, int y, boolean flashing) {
		if (flashing) {
			g.drawImage(mazeFlashings.get(mazeNumber - 1).animate(), x, y, null);
		} else {
			g.drawImage(mazeFull[mazeNumber - 1], x, y, null);
		}
	}

	@Override
	public void drawCopyright(Graphics2D g, int x, int y) {
		g.drawImage(midwayLogo, x, y + 3, 30, 32, null);
		g.setColor(Color.RED);
		g.setFont(new Font("Dialog", Font.PLAIN, 11));
		g.drawString("\u00a9", x + t(5), y + t(2) + 2); // (c) symbol
		g.setFont(getArcadeFont());
		g.drawString("MIDWAY MFG CO", x + t(7), y + t(2));
		g.drawString("1980/1981", x + t(8), y + t(4));
	}
}