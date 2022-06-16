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
import de.amr.games.pacman.lib.animation.SingleSpriteAnimation;
import de.amr.games.pacman.lib.animation.SpriteAnimationMap;
import de.amr.games.pacman.lib.animation.SpriteArray;
import de.amr.games.pacman.model.common.actors.Entity;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.lib.Spritesheet;
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
	private final BufferedImage nailSprite;
	private final SingleSpriteAnimation<BufferedImage> mazeFlashingAnim;
	private final Font font;

	private Rendering2D_PacMan(String path, int rasterSize) {
		ss = new Spritesheet(image(path), rasterSize);
		font = font("/common/emulogic.ttf", 8);

		mazeFull = image("/pacman/graphics/maze_full.png");
		var mazeEmptyDark = image("/pacman/graphics/maze_empty.png");
		var mazeEmptyBright = ss.createBrightEffect(mazeEmptyDark, new Color(33, 33, 255), Color.BLACK);
		mazeFlashingAnim = new SingleSpriteAnimation<>(mazeEmptyBright, mazeEmptyDark);
		mazeFlashingAnim.frameDuration(15);
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
	public BufferedImage getBonusSymbolSprite(int symbol) {
		return ss.tile(2 + symbol, 3);
	}

	@Override
	public BufferedImage getBonusValueSprite(int symbol) {
		return symbol <= 3 ? ss.tile(symbol, 9) : symbol == 4 ? ss.tiles(4, 9, 2, 1) : ss.tiles(3, symbol, 3, 1);
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
	public SingleSpriteAnimation<BufferedImage> createPacDyingAnimation() {
		var animation = new SingleSpriteAnimation<>( //
				ss.tile(3, 0), ss.tile(4, 0), ss.tile(5, 0), ss.tile(6, 0), //
				ss.tile(7, 0), ss.tile(8, 0), ss.tile(9, 0), ss.tile(10, 0), //
				ss.tile(11, 0), ss.tile(12, 0), ss.tile(13, 0));
		animation.frameDuration(8);
		return animation;
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createPacMunchingAnimationMap() {
		SpriteAnimationMap<Direction, BufferedImage> munching = new SpriteAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			BufferedImage wide_open = ss.tile(0, d), open = ss.tile(1, d), closed = ss.tile(2, 0);
			var animation = new SingleSpriteAnimation<>(closed, open, wide_open, open);
			animation.frameDuration(2);
			animation.repeatForever();
			munching.put(dir, animation);
		}
		return munching;
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createGhostColorAnimationMap(int ghostID) {
		SpriteAnimationMap<Direction, BufferedImage> map = new SpriteAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			var animation = new SingleSpriteAnimation<>(ss.tile(2 * index(dir), 4 + ghostID),
					ss.tile(2 * index(dir) + 1, 4 + ghostID));
			animation.frameDuration(8);
			animation.repeatForever();
			map.put(dir, animation);
		}
		return map;
	}

	@Override
	public SingleSpriteAnimation<BufferedImage> createGhostBlueAnimation() {
		var animation = new SingleSpriteAnimation<>(ss.tile(8, 4), ss.tile(9, 4));
		animation.frameDuration(8);
		animation.repeatForever();
		return animation;
	}

	@Override
	public SingleSpriteAnimation<BufferedImage> createGhostFlashingAnimation() {
		var animation = new SingleSpriteAnimation<>(ss.tile(8, 4), ss.tile(9, 4), ss.tile(10, 4), ss.tile(11, 4));
		animation.frameDuration(4);
		return animation;
	}

	@Override
	public SpriteAnimationMap<Direction, BufferedImage> createGhostEyesAnimationMap() {
		SpriteAnimationMap<Direction, BufferedImage> ghostEyesAnimsByDir = new SpriteAnimationMap<>(4);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimsByDir.put(dir, new SingleSpriteAnimation<>(ss.tile(8 + index(dir), 5)));
		}
		return ghostEyesAnimsByDir;
	}

	@Override
	public SpriteArray<BufferedImage> createGhostValueList() {
		return new SpriteArray<>(ss.tile(0, 8), ss.tile(1, 8), ss.tile(2, 8), ss.tile(3, 8));
	}

	// Pac-Man specific

	public SingleSpriteAnimation<BufferedImage> createBigPacManMunchingAnimation() {
		var animation = new SingleSpriteAnimation<>(ss.tiles(2, 1, 2, 2), ss.tiles(4, 1, 2, 2), ss.tiles(6, 1, 2, 2));
		animation.frameDuration(4);
		animation.repeatForever();
		return animation;
	}

	public SingleSpriteAnimation<BufferedImage> createBlinkyStretchedAnimation() {
		return new SingleSpriteAnimation<>(ss.tile(9, 6), ss.tile(10, 6), ss.tile(11, 6), ss.tile(12, 6));
	}

	public SingleSpriteAnimation<BufferedImage> createBlinkyDamagedAnimation() {
		return new SingleSpriteAnimation<>(ss.tile(8, 7), ss.tile(9, 7));
	}

	public SingleSpriteAnimation<BufferedImage> createBlinkyPatchedAnimation() {
		var blinkyPatched = new SingleSpriteAnimation<>(ss.tile(10, 7), ss.tile(11, 7));
		blinkyPatched.frameDuration(4);
		blinkyPatched.repeatForever();
		return blinkyPatched;
	}

	public SingleSpriteAnimation<BufferedImage> createBlinkyNakedAnimation() {
		var blinkyNaked = new SingleSpriteAnimation<>(ss.tiles(8, 8, 2, 1), ss.tiles(10, 8, 2, 1));
		blinkyNaked.frameDuration(4);
		blinkyNaked.repeatForever();
		return blinkyNaked;
	}

	// Maze

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLOR;
	}

	@Override
	public SingleSpriteAnimation<BufferedImage> createMazeFlashingAnimation(int mazeNumber) {
		return mazeFlashingAnim;
	}

	// Drawing

	@Override
	public void drawFullMaze(Graphics2D g, int mazeNumber, int x, int y) {
		g.drawImage(mazeFull, x, y, null);
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
}