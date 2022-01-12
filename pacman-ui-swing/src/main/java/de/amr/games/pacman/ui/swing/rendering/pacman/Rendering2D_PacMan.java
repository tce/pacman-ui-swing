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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.TimedSequence;
import de.amr.games.pacman.model.common.GameEntity;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;

/**
 * Sprite-based rendering for the Pac-Man game.
 * 
 * @author Armin Reichert
 */
public class Rendering2D_PacMan extends Rendering2D {

	private static final Color FOOD_COLOR = new Color(254, 189, 180);

	public final RenderingAssets_PacMan assets = new RenderingAssets_PacMan();

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createPlayerMunchingAnimations() {
		return assets.createPlayerMunchingAnimations();
	}

	@Override
	public TimedSequence<BufferedImage> createPlayerDyingAnimation() {
		return assets.createPlayerDyingAnimation();
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createGhostKickingAnimations(int ghostID) {
		return assets.createGhostKickingAnimations(ghostID);
	}

	@Override
	public TimedSequence<BufferedImage> createGhostFrightenedAnimation() {
		return assets.createGhostFrightenedAnimation();
	}

	@Override
	public TimedSequence<BufferedImage> createGhostFlashingAnimation() {
		return assets.createGhostFlashingAnimation();
	}

	@Override
	public Map<Direction, TimedSequence<BufferedImage>> createGhostReturningHomeAnimations() {
		return assets.createGhostsReturningHomeAnimations();
	}

	public TimedSequence<BufferedImage> createBigPacManMunchingAnimation() {
		return assets.createBigPacManMunchingAnimation();
	}

	@Override
	public TimedSequence<BufferedImage> createBlinkyStretchedAnimation() {
		return assets.createBlinkyStretchedAnimation();
	}

	@Override
	public TimedSequence<BufferedImage> createBlinkyDamagedAnimation() {
		return assets.createBlinkyDamagedAnimation();
	}

	@Override
	public Map<Integer, BufferedImage> getBountyNumberSprites() {
		return assets.numberSprites;
	}

	@Override
	public Map<Integer, BufferedImage> getBonusNumberSprites() {
		return assets.numberSprites;
	}

	@Override
	public Map<String, BufferedImage> getSymbolSpritesMap() {
		return assets.symbolSprites;
	}

	@Override
	public Font getScoreFont() {
		return assets.getScoreFont();
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
	public TimedSequence<BufferedImage> mazeFlashing(int mazeNumber) {
		return assets.mazeFlashingAnim;
	}

	@Override
	public void drawMaze(Graphics2D g, int mazeNumber, int x, int y, boolean flashing) {
		if (flashing) {
			g.drawImage(mazeFlashing(mazeNumber).animate(), x, y, null);
		} else {
			g.drawImage(assets.mazeFullImage, x, y, null);
		}
	}

//	public void drawBigPacMan(Graphics2D g, Pac bigPacMan) {
//		renderEntity(g, bigPacMan, assets.bigPacManAnim.animate());
//	}

	public void drawNail(Graphics2D g, GameEntity nail) {
		renderEntity(g, nail, assets.nailSprite);
	}

	public void drawBlinkyPatched(Graphics2D g, Ghost blinky) {
		renderEntity(g, blinky, assets.blinkyPatched.animate());
	}

	public void drawBlinkyNaked(Graphics2D g, Ghost blinky) {
		renderEntity(g, blinky, assets.blinkyHalfNaked.animate());
	}

	@Override
	public BufferedImage lifeSprite() {
		return assets.sprite(8, 1);
	}

	@Override
	public BufferedImage symbolSprite(String symbol) {
		return assets.symbolSprites.get(symbol);
	}
}