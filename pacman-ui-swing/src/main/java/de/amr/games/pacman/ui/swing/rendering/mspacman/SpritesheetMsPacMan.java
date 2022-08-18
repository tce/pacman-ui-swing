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
import static de.amr.games.pacman.ui.swing.lib.Ujfc.font;
import static de.amr.games.pacman.ui.swing.lib.Ujfc.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.EntityAnimation;
import de.amr.games.pacman.lib.animation.EntityAnimationByDirection;
import de.amr.games.pacman.lib.animation.FixedEntityAnimation;
import de.amr.games.pacman.lib.animation.SingleEntityAnimation;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.model.mspacman.Flap;
import de.amr.games.pacman.model.mspacman.MsPacManGame;
import de.amr.games.pacman.ui.swing.lib.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Rendering for the Ms. Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class SpritesheetMsPacMan implements Rendering2D {

	/** Sprite sheet order of directions. */
	static final List<Direction> order = List.of(Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN);

	static int dirIndex(Direction dir) {
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

	private static final SpritesheetMsPacMan theThing = new SpritesheetMsPacMan("/mspacman/graphics/sprites.png", 16);

	public static SpritesheetMsPacMan get() {
		return theThing;
	}

	private final Spritesheet ss;
	private final BufferedImage midwayLogo;
	private final BufferedImage[] mazeFull;
	private final BufferedImage[] mazeEmpty;
	private final BufferedImage[] symbols;
	private final Font font;

	private SpritesheetMsPacMan(String path, int rasterSize) {
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
		//@formatter:on

		int numMazes = 6;
		mazeEmpty = new BufferedImage[numMazes];
		mazeFull = new BufferedImage[numMazes];
		for (int mazeIndex = 0; mazeIndex < 6; ++mazeIndex) {
			mazeFull[mazeIndex] = ss.image.getSubimage(0, mazeIndex * 248, 226, 248);
			mazeEmpty[mazeIndex] = ss.image.getSubimage(228, mazeIndex * 248, 226, 248);
		}
	}

	@Override
	public BufferedImage getSourceImage() {
		return ss.image;
	}

	/**
	 * Picks sprite from the right part of the sheet, on the left are the maze images
	 */
	public BufferedImage rhs(int tileX, int tileY) {
		return ss.tilesFrom(456, 0, tileX, tileY, 1, 1);
	}

	@Override
	public Font getArcadeFont() {
		return font;
	}

	@Override
	public BufferedImage getGhostSprite(int ghostID, Direction dir) {
		return rhs(2 * dirIndex(dir) + 1, 4 + ghostID);
	}

	@Override
	public Color getGhostColor(int ghostID) {
		return GHOST_COLORS[ghostID];
	}

	@Override
	public BufferedImage getBonusSymbolSprite(int symbol) {
		return rhs(3 + symbol, 0);
	}

	@Override
	public BufferedImage getBonusValueSprite(int symbol) {
		return rhs(3 + symbol, 1);
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
	public SingleEntityAnimation<BufferedImage> createPacDyingAnimation() {
		var animation = new SingleEntityAnimation<>(rhs(0, 3), rhs(0, 0), rhs(0, 1), rhs(0, 2));
		animation.setFrameDuration(10);
		animation.setRepetitions(2);
		return animation;
	}

	@Override
	public EntityAnimationByDirection createPacMunchingAnimationMap(Pac pac) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(pac::moveDir);
		for (Direction dir : Direction.values()) {
			int d = dirIndex(dir);
			var wide = rhs(0, d);
			var middle = rhs(1, d);
			var closed = rhs(2, d);
			var animation = new SingleEntityAnimation<>(middle, closed, middle, wide);
			animation.setFrameDuration(2);
			animation.repeatForever();
			map.put(dir, animation);
		}
		return map;
	}

	public EntityAnimationByDirection createSpouseMunchingAnimations(Pac pac) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(pac::moveDir);
		for (Direction dir : Direction.values()) {
			int d = dirIndex(dir);
			var munching = new SingleEntityAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9));
			munching.setFrameDuration(2);
			munching.repeatForever();
			map.put(dir, munching);
		}
		return map;
	}

	@Override
	public EntityAnimationByDirection createGhostColorAnimationMap(Ghost ghost) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(ghost::wishDir);
		for (Direction dir : Direction.values()) {
			int d = dirIndex(dir);
			var color = new SingleEntityAnimation<>(rhs(2 * d, 4 + ghost.id), rhs(2 * d + 1, 4 + ghost.id));
			color.setFrameDuration(4);
			color.repeatForever();
			map.put(dir, color);
		}
		return map;
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createGhostBlueAnimation() {
		var animation = new SingleEntityAnimation<>(rhs(8, 4), rhs(9, 4));
		animation.setFrameDuration(8);
		animation.repeatForever();
		return animation;
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createGhostFlashingAnimation() {
		var animation = new SingleEntityAnimation<>(rhs(8, 4), rhs(9, 4), rhs(10, 4), rhs(11, 4));
		animation.setFrameDuration(4);
		return animation;
	}

	@Override
	public EntityAnimationByDirection createGhostEyesAnimationMap(Ghost ghost) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(ghost::wishDir);
		for (Direction dir : Direction.values()) {
			map.put(dir, new SingleEntityAnimation<>(rhs(8 + dirIndex(dir), 5)));
		}
		return map;
	}

	@Override
	public FixedEntityAnimation<BufferedImage> createGhostValueList() {
		return new FixedEntityAnimation<>(rhs(0, 8), rhs(1, 8), rhs(2, 8), rhs(3, 8));
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createMazeFlashingAnimation(int mazeNumber) {
		int mazeIndex = mazeNumber - 1;
		var mazeEmptyBright = ss.createBrightEffect(mazeEmpty[mazeIndex], MAZE_SIDE_COLORS[mazeIndex],
				MAZE_TOP_COLORS[mazeIndex]);
		var animation = new SingleEntityAnimation<>(mazeEmptyBright, mazeEmpty[mazeIndex]);
		animation.setFrameDuration(15);
		return animation;
	}

	public EntityAnimationByDirection createHusbandMunchingAnimations(Pac pac) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(pac::moveDir);
		for (var dir : Direction.values()) {
			int d = dirIndex(dir);
			var animation = new SingleEntityAnimation<>(rhs(0, 9 + d), rhs(1, 9 + d), rhs(2, 9));
			animation.setFrameDuration(2);
			animation.repeatForever();
			map.put(dir, animation);
		}
		return map;
	}

	public SingleEntityAnimation<BufferedImage> createFlapAnimation() {
		var animation = new SingleEntityAnimation<>( //
				ss.si(456, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(520, 208, 32, 32), //
				ss.si(488, 208, 32, 32), //
				ss.si(456, 208, 32, 32)//
		);
		animation.setFrameDuration(4);
		return animation;
	}

	public SingleEntityAnimation<BufferedImage> createStorkFlyingAnimation() {
		var animation = new SingleEntityAnimation<>( //
				ss.si(489, 176, 32, 16), //
				ss.si(521, 176, 32, 16) //
		);
		animation.repeatForever();
		animation.setFrameDuration(10);
		return animation;
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
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLORS[mazeNumber - 1];
	}

	@Override
	public BufferedImage getLifeSprite() {
		return rhs(1, 0);
	}

	@Override
	public void drawFullMaze(Graphics2D g, int mazeNumber, int x, int y) {
		g.drawImage(mazeFull[mazeNumber - 1], x, y, null);
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

	public void drawFlap(Graphics2D g, Flap flap) {
		if (flap.isVisible()) {
			flap.animation().map(EntityAnimation::animate).ifPresent(spriteObj -> {
				var sprite = (BufferedImage) spriteObj;
				drawEntity(g, flap, sprite);
				g.setFont(getArcadeFont());
				g.setColor(new Color(222, 222, 255));
				g.drawString(flap.number + "", (int) flap.getPosition().x() + sprite.getWidth() - 25,
						(int) flap.getPosition().y() + 18);
				g.drawString(flap.text, (int) flap.getPosition().x() + sprite.getWidth(), (int) flap.getPosition().y());
			});
		}
	}
}