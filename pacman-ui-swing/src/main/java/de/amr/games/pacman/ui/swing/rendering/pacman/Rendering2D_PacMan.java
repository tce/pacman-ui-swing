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
package de.amr.games.pacman.ui.swing.rendering.pacman;

import static de.amr.games.pacman.lib.Direction.DOWN;
import static de.amr.games.pacman.lib.Direction.LEFT;
import static de.amr.games.pacman.lib.Direction.RIGHT;
import static de.amr.games.pacman.lib.Direction.UP;
import static de.amr.games.pacman.ui.swing.assets.AssetLoader.font;
import static de.amr.games.pacman.ui.swing.assets.AssetLoader.image;
import static java.util.Map.entry;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.model.common.GameEntity;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.model.pacman.PacManGame;
import de.amr.games.pacman.ui.swing.assets.Spritesheet;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Sprite-based rendering for the Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class Rendering2D_PacMan extends Rendering2D {

	private static final Color FOOD_COLOR = new Color(254, 189, 180);

	/** Sprite sheet order of directions. */
	static final List<Direction> order = Arrays.asList(RIGHT, LEFT, UP, DOWN);

	private static int index(Direction dir) {
		return order.indexOf(dir);
	}

	public final Spritesheet sheet;
	public final Font scoreFont;

	public final BufferedImage mazeFullImage;
	public final BufferedImage mazeEmptyImage;
	public final TimedSeq<BufferedImage> mazeFlashingAnim;
	public final Map<Integer, BufferedImage> symbolSprites;
	public final Map<Integer, BufferedImage> numberSprites;
	public final TimedSeq<BufferedImage> blinkyHalfNaked;
	public final TimedSeq<BufferedImage> blinkyPatched;
	public final BufferedImage nailSprite;

	public Rendering2D_PacMan() {
		sheet = new Spritesheet(image("/pacman/graphics/sprites.png"), 16);

		scoreFont = font("/emulogic.ttf", 8);

		// Sprites and images

		mazeFullImage = image("/pacman/graphics/maze_full.png");
		mazeEmptyImage = image("/pacman/graphics/maze_empty.png");

		//@formatter:off
		symbolSprites = Map.of(
				PacManGame.CHERRIES,   sheet.sprite(2, 3),
				PacManGame.STRAWBERRY, sheet.sprite(3, 3),
				PacManGame.PEACH,      sheet.sprite(4, 3),
				PacManGame.APPLE,      sheet.sprite(5, 3),
				PacManGame.GRAPES,     sheet.sprite(6, 3),
				PacManGame.GALAXIAN,   sheet.sprite(7, 3),
				PacManGame.BELL,       sheet.sprite(8, 3),
				PacManGame.KEY,        sheet.sprite(9, 3)
		);

		numberSprites = Map.ofEntries(
			entry(200,  sheet.sprite(0, 8)),
			entry(400,  sheet.sprite(1, 8)),
			entry(800,  sheet.sprite(2, 8)),
			entry(1600, sheet.sprite(3, 8)),
			
			entry(100,  sheet.sprite(0, 9)),
			entry(300,  sheet.sprite(1, 9)),
			entry(500,  sheet.sprite(2, 9)),
			entry(700,  sheet.sprite(3, 9)),
			
			entry(1000, sheet.spriteRegion(4, 9, 2, 1)), // left-aligned
			entry(2000, sheet.spriteRegion(3, 10, 3, 1)),
			entry(3000, sheet.spriteRegion(3, 11, 3, 1)),
			entry(5000, sheet.spriteRegion(3, 12, 3, 1))
		);
		//@formatter:on

		// Animations

		BufferedImage mazeEmptyDarkImage = image("/pacman/graphics/maze_empty.png");
		BufferedImage mazeEmptyBrightImage = sheet.createBrightEffect(mazeEmptyDarkImage, new Color(33, 33, 255),
				Color.BLACK);
		mazeFlashingAnim = TimedSeq.of(mazeEmptyBrightImage, mazeEmptyDarkImage).frameDuration(15);

		blinkyPatched = TimedSeq.of(sheet.sprite(10, 7), sheet.sprite(11, 7)).restart().frameDuration(4).endless();
		blinkyHalfNaked = TimedSeq.of(sheet.spriteRegion(8, 8, 2, 1), sheet.spriteRegion(10, 8, 2, 1)).endless()
				.frameDuration(4).restart();

		nailSprite = sheet.sprite(8, 6);
	}

	@Override
	public Font getArcadeFont() {
		return scoreFont;
	}

	@Override
	public Map<Integer, BufferedImage> getBonusNumberSprites() {
		return numberSprites;
	}

	@Override
	public Map<Integer, BufferedImage> getBountyNumberSprites() {
		return numberSprites;
	}

	@Override
	public Map<Integer, BufferedImage> getSymbolSpritesMap() {
		return symbolSprites;
	}

	public BufferedImage ghostImageByGhostByDir(int ghostID, Direction dir) {
		return sheet.sprite(2 * index(dir), 4 + ghostID);
	}

	@Override
	public TimedSeq<BufferedImage> createPlayerDyingAnimation() {
		return TimedSeq.of( //
				sheet.sprite(3, 0), sheet.sprite(4, 0), sheet.sprite(5, 0), sheet.sprite(6, 0), //
				sheet.sprite(7, 0), sheet.sprite(8, 0), sheet.sprite(9, 0), sheet.sprite(10, 0), //
				sheet.sprite(11, 0), sheet.sprite(12, 0), sheet.sprite(13, 0)) //
				.frameDuration(8);
	}

	@Override
	public Map<Direction, TimedSeq<BufferedImage>> createPlayerMunchingAnimations() {
		EnumMap<Direction, TimedSeq<BufferedImage>> munching = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			int d = index(dir);
			BufferedImage wide_open = sheet.sprite(0, d), open = sheet.sprite(1, d), closed = sheet.sprite(2, 0);
			var animation = TimedSeq.of(closed, open, wide_open, open).frameDuration(2).endless().run();
			munching.put(dir, animation);
		}
		return munching;
	}

	@Override
	public Map<Direction, TimedSeq<BufferedImage>> createGhostKickingAnimations(int ghostID) {
		EnumMap<Direction, TimedSeq<BufferedImage>> walkingTo = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			TimedSeq<BufferedImage> anim = TimedSeq.of( //
					sheet.sprite(2 * index(dir), 4 + ghostID), //
					sheet.sprite(2 * index(dir) + 1, 4 + ghostID));
			anim.frameDuration(8).endless();
			walkingTo.put(dir, anim);
		}
		return walkingTo;
	}

	@Override
	public TimedSeq<BufferedImage> createGhostFrightenedAnimation() {
		TimedSeq<BufferedImage> animation = TimedSeq.of(sheet.sprite(8, 4), sheet.sprite(9, 4));
		animation.frameDuration(8).endless();
		return animation;
	}

	@Override
	public TimedSeq<BufferedImage> createGhostFlashingAnimation() {
		return TimedSeq.of( //
				sheet.sprite(8, 4), sheet.sprite(9, 4), sheet.sprite(10, 4), sheet.sprite(11, 4) //
		).frameDuration(4);
	}

	@Override
	public Map<Direction, TimedSeq<BufferedImage>> createGhostReturningHomeAnimations() {
		Map<Direction, TimedSeq<BufferedImage>> ghostEyesAnimsByDir = new EnumMap<>(Direction.class);
		for (Direction dir : Direction.values()) {
			ghostEyesAnimsByDir.put(dir, TimedSeq.of(sheet.sprite(8 + index(dir), 5)));
		}
		return ghostEyesAnimsByDir;
	}

	public TimedSeq<BufferedImage> createBigPacManMunchingAnimation() {
		return TimedSeq
				.of(sheet.spriteRegion(2, 1, 2, 2), sheet.spriteRegion(4, 1, 2, 2), sheet.spriteRegion(6, 1, 2, 2))
				.frameDuration(4).endless().run();
	}

	@Override
	public TimedSeq<BufferedImage> createBlinkyStretchedAnimation() {
		return TimedSeq.of(sheet.sprite(9, 6), sheet.sprite(10, 6), sheet.sprite(11, 6), sheet.sprite(12, 6));
	}

	@Override
	public TimedSeq<BufferedImage> createBlinkyDamagedAnimation() {
		return TimedSeq.of(sheet.sprite(8, 7), sheet.sprite(9, 7));
	}

	@Override
	public Color getMazeWallBorderColor(int mazeIndex) {
		return new Color(33, 33, 255);
	}

	@Override
	public Color getMazeWallColor(int mazeIndex) {
		return Color.BLACK;
	}

	@Override
	public Color getFoodColor(int mazeNumber) {
		return FOOD_COLOR;
	}

	@Override
	public TimedSeq<BufferedImage> mazeFlashing(int mazeNumber) {
		return mazeFlashingAnim;
	}

	@Override
	public void drawMaze(Graphics2D g, int mazeNumber, int x, int y, boolean flashing) {
		if (flashing) {
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y, null);
		} else {
			g.drawImage(mazeFullImage, x, y, null);
		}
	}

	public void drawNail(Graphics2D g, GameEntity nail) {
		renderEntity(g, nail, nailSprite);
	}

	public void drawBlinkyPatched(Graphics2D g, Ghost blinky) {
		renderEntity(g, blinky, blinkyPatched.animate());
	}

	public void drawBlinkyNaked(Graphics2D g, Ghost blinky) {
		renderEntity(g, blinky, blinkyHalfNaked.animate());
	}

	@Override
	public BufferedImage lifeSprite() {
		return sheet.sprite(8, 1);
	}

	@Override
	public BufferedImage symbolSprite(int symbol) {
		return symbolSprites.get(symbol);
	}
}