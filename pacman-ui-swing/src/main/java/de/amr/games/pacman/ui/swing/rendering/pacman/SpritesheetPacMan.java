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

import static de.amr.games.pacman.lib.steering.Direction.DOWN;
import static de.amr.games.pacman.lib.steering.Direction.LEFT;
import static de.amr.games.pacman.lib.steering.Direction.RIGHT;
import static de.amr.games.pacman.lib.steering.Direction.UP;
import static de.amr.games.pacman.ui.swing.lib.Ujfc.font;
import static de.amr.games.pacman.ui.swing.lib.Ujfc.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import de.amr.games.pacman.lib.anim.EntityAnimationByDirection;
import de.amr.games.pacman.lib.anim.FixedEntityAnimation;
import de.amr.games.pacman.lib.anim.SingleEntityAnimation;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.Pac;
import de.amr.games.pacman.ui.swing.lib.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Sprite-based rendering for the Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class SpritesheetPacMan implements Rendering2D {

	private static final SpritesheetPacMan theThing = new SpritesheetPacMan("/pacman/graphics/sprites.png", 16);

	public static SpritesheetPacMan get() {
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
	private final SingleEntityAnimation<BufferedImage> mazeFlashingAnim;
	private final Font font;

	private SpritesheetPacMan(String path, int rasterSize) {
		ss = new Spritesheet(image(path), rasterSize);
		font = font("/common/emulogic.ttf", 8);

		mazeFull = image("/pacman/graphics/maze_full.png");
		var mazeEmptyDark = image("/pacman/graphics/maze_empty.png");
		var mazeEmptyBright = ss.createBrightEffect(mazeEmptyDark, new Color(33, 33, 255), Color.BLACK);
		mazeFlashingAnim = new SingleEntityAnimation<>(mazeEmptyBright, mazeEmptyDark);
		mazeFlashingAnim.setFrameDuration(15);
	}

	@Override
	public BufferedImage getSourceImage() {
		return ss.image;
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
		if (symbol <= 3) {
			return ss.tile(symbol, 9);
		}
		return symbol == 4 ? ss.tiles(4, 9, 2, 1) : ss.tiles(3, symbol, 3, 1);
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
	public SingleEntityAnimation<BufferedImage> createPacDyingAnimation() {
		var animation = new SingleEntityAnimation<>( //
				ss.tile(3, 0), ss.tile(4, 0), ss.tile(5, 0), ss.tile(6, 0), //
				ss.tile(7, 0), ss.tile(8, 0), ss.tile(9, 0), ss.tile(10, 0), //
				ss.tile(11, 0), ss.tile(12, 0), ss.tile(13, 0));
		animation.setFrameDuration(8);
		return animation;
	}

	@Override
	public EntityAnimationByDirection createPacMunchingAnimationMap(Pac pac) {
		EntityAnimationByDirection munching = new EntityAnimationByDirection(pac::moveDir);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			var wide = ss.tile(0, d);
			var open = ss.tile(1, d);
			var closed = ss.tile(2, 0);
			var animation = new SingleEntityAnimation<>(closed, open, wide, open);
			animation.setFrameDuration(2);
			animation.repeatForever();
			munching.put(dir, animation);
		}
		return munching;
	}

	@Override
	public EntityAnimationByDirection createGhostColorAnimationMap(Ghost ghost) {
		EntityAnimationByDirection map = new EntityAnimationByDirection(ghost::wishDir);
		for (Direction dir : Direction.values()) {
			var animation = new SingleEntityAnimation<>(ss.tile(2 * index(dir), 4 + ghost.id()),
					ss.tile(2 * index(dir) + 1, 4 + ghost.id()));
			animation.setFrameDuration(8);
			animation.repeatForever();
			map.put(dir, animation);
		}
		return map;
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createGhostBlueAnimation() {
		var animation = new SingleEntityAnimation<>(ss.tile(8, 4), ss.tile(9, 4));
		animation.setFrameDuration(8);
		animation.repeatForever();
		return animation;
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createGhostFlashingAnimation() {
		var animation = new SingleEntityAnimation<>(ss.tile(8, 4), ss.tile(9, 4), ss.tile(10, 4), ss.tile(11, 4));
		animation.setFrameDuration(4);
		return animation;
	}

	@Override
	public EntityAnimationByDirection createGhostEyesAnimationMap(Ghost ghost) {
		EntityAnimationByDirection ghostEyesAnimsByDir = new EntityAnimationByDirection(ghost::wishDir);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimsByDir.put(dir, new SingleEntityAnimation<>(ss.tile(8 + index(dir), 5)));
		}
		return ghostEyesAnimsByDir;
	}

	@Override
	public FixedEntityAnimation<BufferedImage> createGhostValueList() {
		return new FixedEntityAnimation<>(ss.tile(0, 8), ss.tile(1, 8), ss.tile(2, 8), ss.tile(3, 8));
	}

	// Pac-Man specific

	public SingleEntityAnimation<BufferedImage> createBigPacManMunchingAnimation() {
		var animation = new SingleEntityAnimation<>(ss.tiles(2, 1, 2, 2), ss.tiles(4, 1, 2, 2), ss.tiles(6, 1, 2, 2));
		animation.setFrameDuration(4);
		animation.repeatForever();
		return animation;
	}

	public SingleEntityAnimation<BufferedImage> createBlinkyStretchedAnimation() {
		return new SingleEntityAnimation<>(ss.tile(8, 6), ss.tile(9, 6), ss.tile(10, 6), ss.tile(11, 6), ss.tile(12, 6));
	}

	public SingleEntityAnimation<BufferedImage> createBlinkyDamagedAnimation() {
		return new SingleEntityAnimation<>(ss.tile(8, 7), ss.tile(9, 7));
	}

	public SingleEntityAnimation<BufferedImage> createBlinkyPatchedAnimation() {
		var blinkyPatched = new SingleEntityAnimation<>(ss.tile(10, 7), ss.tile(11, 7));
		blinkyPatched.setFrameDuration(4);
		blinkyPatched.repeatForever();
		return blinkyPatched;
	}

	public SingleEntityAnimation<BufferedImage> createBlinkyNakedAnimation() {
		var blinkyNaked = new SingleEntityAnimation<>(ss.tiles(8, 8, 2, 1), ss.tiles(10, 8, 2, 1));
		blinkyNaked.setFrameDuration(4);
		blinkyNaked.repeatForever();
		return blinkyNaked;
	}

	// Maze

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLOR;
	}

	@Override
	public SingleEntityAnimation<BufferedImage> createMazeFlashingAnimation(int mazeNumber) {
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
		g.setColor(getGhostColor(Ghost.ID_PINK_GHOST));
		g.drawString("\u00A9 1980 MIDWAY MFG. CO.", x, y);
	}
}