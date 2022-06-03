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
import static de.amr.games.pacman.ui.swing.assets.AssetLoader.font;
import static de.amr.games.pacman.ui.swing.assets.AssetLoader.image;

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
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
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
	private final List<BufferedImage> mazeFullSprites;
	private final List<BufferedImage> mazeEmptySprites;
	private final Map<Integer, BufferedImage> symbolSprites;
	private final Map<Integer, BufferedImage> bonusValueSprites;
	private final Map<Integer, BufferedImage> bountyNumberSprites;
	private final List<TimedSeq<BufferedImage>> mazesFlashingAnims;
	private final Font font;

	private Rendering2D_MsPacMan(String path, int rasterSize) {
		ss = new Spritesheet(image(path), rasterSize);
		font = font("/emulogic.ttf", 8);
		midwayLogo = image("/mspacman/graphics/midway.png");

		//@formatter:off
		symbolSprites = Map.of(
			MsPacManGame.CHERRIES,   rhs(3,0),
			MsPacManGame.STRAWBERRY, rhs(4,0),
			MsPacManGame.PEACH,      rhs(5,0),
			MsPacManGame.PRETZEL,    rhs(6,0),
			MsPacManGame.APPLE,      rhs(7,0),
			MsPacManGame.PEAR,       rhs(8,0),
			MsPacManGame.BANANA,     rhs(9,0)
		);

		bonusValueSprites = Map.of(
			100,  rhs(3, 1), 
			200,  rhs(4, 1), 
			500,  rhs(5, 1), 
			700,  rhs(6, 1), 
			1000, rhs(7, 1), 
			2000, rhs(8, 1),
			5000, rhs(9, 1)
		);

		bountyNumberSprites = Map.of(
			200,  rhs(0, 8), 
			400,  rhs(1, 8), 
			800,  rhs(2, 8), 
			1600, rhs(3, 8)
		);
		//@formatter:on

		int numMazes = 6;
		mazeEmptySprites = new ArrayList<>(numMazes);
		mazeFullSprites = new ArrayList<>(numMazes);
		mazesFlashingAnims = new ArrayList<>(numMazes);
		for (int mazeIndex = 0; mazeIndex < 6; ++mazeIndex) {
			BufferedImage mazeFullImage = ss.image.getSubimage(0, mazeIndex * 248, 226, 248);
			mazeFullSprites.add(mazeFullImage);
			BufferedImage mazeEmptyImage = ss.image.getSubimage(226, mazeIndex * 248, 226, 248);
			mazeEmptySprites.add(mazeEmptyImage);
			BufferedImage mazeFlashImage = ss.createBrightEffect(mazeEmptySprites.get(mazeIndex), MAZE_SIDE_COLORS[mazeIndex],
					MAZE_TOP_COLORS[mazeIndex]);
			mazesFlashingAnims.add(TimedSeq.of(mazeFlashImage, mazeEmptySprites.get(mazeIndex)).frameDuration(15));
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
		SpriteAnimationMap<Direction, BufferedImage> munchings = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			BufferedImage wide_open = rhs(0, d), open = rhs(1, d), closed = rhs(2, d);
			var animation = new SpriteAnimation<>(open, closed, open, wide_open).frameDuration(2).endless();
			munchings.put(dir, animation);
		}
		return munchings;
	}

	public SpriteAnimationMap<Direction, BufferedImage> createSpouseMunchingAnimations() {
		SpriteAnimationMap<Direction, BufferedImage> munchings = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var munching = new SpriteAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9)).frameDuration(2).endless();
			munchings.put(dir, munching);
		}
		return munchings;
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createGhostColorAnimation(int ghostID) {
		SpriteAnimationMap<Direction, BufferedImage> kickingByDir = new SpriteAnimationMap<Direction, BufferedImage>(
				Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var kicking = new SpriteAnimation<>(rhs(2 * d, 4 + ghostID), rhs(2 * d + 1, 4 + ghostID)).frameDuration(4)
					.endless();
			kickingByDir.put(dir, kicking);
		}
		return kickingByDir;
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
		SpriteAnimationMap<Direction, BufferedImage> ghostEyesAnimByDir = new SpriteAnimationMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimByDir.put(dir, new SpriteAnimation<>(rhs(8 + index(dir), 5)));
		}
		return ghostEyesAnimByDir;
	}

	@Override
	public TimedSeq<BufferedImage> mazeFlashing(int mazeNumber) {
		return mazesFlashingAnims.get(mazeNumber - 1);
	}

	public TimedSeq<Integer> createBonusAnimation() {
		return TimedSeq.of(2, -2).frameDuration(15).endless();
	}

	public SpriteAnimationMap<Direction, BufferedImage> createHusbandMunchingAnimations() {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(Direction.class);
		for (var dir : Direction.values()) {
			int d = index(dir);
			map.put(dir, new SpriteAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9)).frameDuration(2).endless());
		}
		return map;
	}

	public TimedSeq<BufferedImage> createFlapAnimation() {
		return TimedSeq.of( //
				ss.si(456, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(520, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(456, 208, 32, 32)//
		).repetitions(1).frameDuration(4);
	}

	public TimedSeq<BufferedImage> createStorkFlyingAnimation() {
		return TimedSeq.of( //
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
		return bountyNumberSprites.get(number);
	}

	@Override
	public BufferedImage getBonusValueSprite(int number) {
		return bonusValueSprites.get(number);
	}

	@Override
	public BufferedImage getSymbolSprite(int symbol) {
		return symbolSprites.get(symbol);
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
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y, null);
		} else {
			g.drawImage(mazeFullSprites.get(mazeNumber - 1), x, y, null);
		}
	}

	@Override
	public void drawCopyright(Graphics2D g, int x, int y) {
		// t(6), t(28)
		double scale = (double) ArcadeWorld.TILES_Y / midwayLogo.getHeight();
		g.drawImage(midwayLogo, x, y + 3, (int) (scale * midwayLogo.getWidth()), (int) (scale * midwayLogo.getHeight()),
				null);
		g.setColor(Color.RED);
		g.setFont(new Font("Dialog", Font.PLAIN, 11));
		g.drawString("\u00a9", x + t(5), y + t(2) + 2); // (c) symbol
		g.setFont(getArcadeFont());
		g.drawString("MIDWAY MFG CO", x + t(7), y + t(2));
		g.drawString("1980/1981", x + t(8), y + t(4));
	}
}