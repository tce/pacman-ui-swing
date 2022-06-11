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
package de.amr.games.pacman.ui.swing.rendering.pacman;

import static de.amr.games.pacman.lib.Direction.DOWN;
import static de.amr.games.pacman.lib.Direction.LEFT;
import static de.amr.games.pacman.lib.Direction.RIGHT;
import static de.amr.games.pacman.lib.Direction.UP;
import static de.amr.games.pacman.ui.swing.lib.U.font;
import static de.amr.games.pacman.ui.swing.lib.U.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.ThingAnimationMap;
import de.amr.games.pacman.lib.animation.SimpleThingAnimation;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.assets.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Sprite-based rendering for the Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class Rendering2D_PacMan implements Rendering2D {

	private static final Rendering2D_PacMan theThing = new Rendering2D_PacMan("/pacman/graphics/sprites.png", 16);

	public static Rendering2D_PacMan get() {
		return theThing;
	}

	// TODO
	static final List<Direction> order = Arrays.asList(RIGHT, LEFT, UP, DOWN);

	private static int index(Direction dir) {
		return order.indexOf(dir);
	}

	//@formatter:off
	static final Color[] GHOST_COLORS = {
		Color.RED,
		new Color(252, 181, 255),
		Color.CYAN,
		new Color(253, 192, 90)
	};
	//@formatter:on

	private static final Color FOOD_COLOR = new Color(254, 189, 180);

	private final Spritesheet ss;
	private final BufferedImage mazeFull;
	private final SimpleThingAnimation<BufferedImage> mazeFlashingAnim;
	private final Font font;

	private final SimpleThingAnimation<BufferedImage> blinkyHalfNaked;
	private final SimpleThingAnimation<BufferedImage> blinkyPatched;
	private final BufferedImage nailSprite;

	private Rendering2D_PacMan(String path, int rasterSize) {
		ss = new Spritesheet(image(path), rasterSize);
		font = font("/common/emulogic.ttf", 8);

		mazeFull = image("/pacman/graphics/maze_full.png");
		var mazeEmptyDark = image("/pacman/graphics/maze_empty.png");
		var mazeEmptyBright = ss.createBrightEffect(mazeEmptyDark, new Color(33, 33, 255), Color.BLACK);
		mazeFlashingAnim = new SimpleThingAnimation<>(mazeEmptyBright, mazeEmptyDark);
		mazeFlashingAnim.frameDuration(15);

		blinkyPatched = new SimpleThingAnimation<>(ss.tile(10, 7), ss.tile(11, 7));
		blinkyPatched.frameDuration(4);
		blinkyPatched.repeatForever();

		blinkyHalfNaked = new SimpleThingAnimation<>(ss.tiles(8, 8, 2, 1), ss.tiles(10, 8, 2, 1));
		blinkyHalfNaked.frameDuration(4);
		blinkyHalfNaked.repeatForever();

		nailSprite = ss.tile(8, 6);
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
	public BufferedImage getGhostSprite(int ghostID, Direction dir) {
		return ss.tile(2 * index(dir), 4 + ghostID);
	}

	@Override
	public BufferedImage getLifeSprite() {
		return ss.tile(8, 1);
	}

	@Override
	public int mazeNumber(int levelNumber) {
		return 1;
	}

	public BufferedImage ghostImageByGhostByDir(int ghostID, Direction dir) {
		return ss.tile(2 * index(dir), 4 + ghostID);
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createPacDyingAnimation() {
		var animation = new SimpleThingAnimation<>( //
				ss.tile(3, 0), ss.tile(4, 0), ss.tile(5, 0), ss.tile(6, 0), //
				ss.tile(7, 0), ss.tile(8, 0), ss.tile(9, 0), ss.tile(10, 0), //
				ss.tile(11, 0), ss.tile(12, 0), ss.tile(13, 0));
		animation.frameDuration(8);
		return animation;
	}

	@Override
	public ThingAnimationMap<Direction, BufferedImage> createPacMunchingAnimations() {
		ThingAnimationMap<Direction, BufferedImage> munching = new ThingAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			BufferedImage wide_open = ss.tile(0, d), open = ss.tile(1, d), closed = ss.tile(2, 0);
			var animation = new SimpleThingAnimation<>(closed, open, wide_open, open);
			animation.frameDuration(2);
			animation.repeatForever();
			munching.put(dir, animation);
		}
		return munching;
	}

	@Override
	public ThingAnimationMap<Direction, BufferedImage> createGhostColorAnimation(int ghostID) {
		ThingAnimationMap<Direction, BufferedImage> map = new ThingAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			var animation = new SimpleThingAnimation<>(ss.tile(2 * index(dir), 4 + ghostID),
					ss.tile(2 * index(dir) + 1, 4 + ghostID));
			animation.frameDuration(8);
			animation.repeatForever();
			map.put(dir, animation);
		}
		return map;
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createGhostBlueAnimation() {
		var animation = new SimpleThingAnimation<>(ss.tile(8, 4), ss.tile(9, 4));
		animation.frameDuration(8);
		animation.repeatForever();
		return animation;
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createGhostFlashingAnimation() {
		var animation = new SimpleThingAnimation<>(ss.tile(8, 4), ss.tile(9, 4), ss.tile(10, 4), ss.tile(11, 4));
		animation.frameDuration(4);
		return animation;
	}

	@Override
	public ThingAnimationMap<Direction, BufferedImage> createGhostEyesAnimation() {
		ThingAnimationMap<Direction, BufferedImage> ghostEyesAnimsByDir = new ThingAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimsByDir.put(dir, new SimpleThingAnimation<>(ss.tile(8 + index(dir), 5)));
		}
		return ghostEyesAnimsByDir;
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createGhostValueAnimation() {
		return new SimpleThingAnimation<>(ss.tile(0, 8), ss.tile(1, 8), ss.tile(2, 8), ss.tile(3, 8));
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createBonusSymbolAnimation() {
		return new SimpleThingAnimation<>(ss.tile(2, 3), ss.tile(3, 3), ss.tile(4, 3), ss.tile(5, 3), ss.tile(6, 3),
				ss.tile(7, 3), ss.tile(8, 3), ss.tile(9, 3));
	}

	@Override
	public SimpleThingAnimation<BufferedImage> createBonusValueAnimation() {
		return new SimpleThingAnimation<>(ss.tile(0, 9), ss.tile(1, 9), ss.tile(2, 9), ss.tile(3, 9),
				ss.tiles(4, 9, 2, 1), // left-aligned
				ss.tiles(3, 10, 3, 1), ss.tiles(3, 11, 3, 1), ss.tiles(3, 12, 3, 1));
	}

	// Pac-Man specific

	public SimpleThingAnimation<BufferedImage> createBigPacManMunchingAnimation() {
		var animation = new SimpleThingAnimation<>(ss.tiles(2, 1, 2, 2), ss.tiles(4, 1, 2, 2), ss.tiles(6, 1, 2, 2));
		animation.frameDuration(4);
		animation.repeatForever();
		return animation;
	}

	public SimpleThingAnimation<BufferedImage> createBlinkyStretchedAnimation() {
		return new SimpleThingAnimation<>(ss.tile(9, 6), ss.tile(10, 6), ss.tile(11, 6), ss.tile(12, 6));
	}

	public SimpleThingAnimation<BufferedImage> createBlinkyDamagedAnimation() {
		return new SimpleThingAnimation<>(ss.tile(8, 7), ss.tile(9, 7));
	}

	// Maze

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLOR;
	}

	@Override
	public SimpleThingAnimation<BufferedImage> mazeFlashing(int mazeNumber) {
		return mazeFlashingAnim;
	}

	// Drawing

	@Override
	public void drawMaze(Graphics2D g, int mazeNumber, int x, int y, boolean flashing) {
		if (flashing) {
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y, null);
		} else {
			g.drawImage(mazeFull, x, y, null);
		}
	}

	@Override
	public void drawCopyright(Graphics2D g, int x, int y) {
		g.setFont(getArcadeFont());
		g.setColor(getGhostColor(Ghost.PINK_GHOST));
		g.drawString("\u00A9 1980 MIDWAY MFG. CO.", x, y);
	}

	public void drawNail(Graphics2D g, Entity nail) {
		drawEntity(g, nail, nailSprite);
	}

	public void drawBlinkyPatched(Graphics2D g, Ghost blinky) {
		drawEntity(g, blinky, blinkyPatched.animate());
	}

	public void drawBlinkyNaked(Graphics2D g, Ghost blinky) {
		drawEntity(g, blinky, blinkyHalfNaked.animate());
	}
}