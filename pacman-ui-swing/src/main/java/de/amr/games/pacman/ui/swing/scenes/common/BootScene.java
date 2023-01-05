/*
MIT License

Copyright (c) 2022 Armin Reichert

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

package de.amr.games.pacman.ui.swing.scenes.common;

import static de.amr.games.pacman.model.common.world.World.TS;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;

/**
 * @author Armin Reichert
 */
public class BootScene extends GameScene {

	private final Random rnd = new Random();
	private final Graphics2D gc;
	private final BufferedImage currentImage;

	public BootScene() {
		currentImage = new BufferedImage(size.x(), size.y(), BufferedImage.TYPE_INT_RGB);
		gc = (Graphics2D) currentImage.getGraphics();
	}

	@Override
	public void init() {
		clearBuffer();
	}

	@Override
	public void update() {
		var timer = gameController.state().timer();
		if (timer.betweenSeconds(1.0, 2.0) && timer.tick() % 5 == 0) {
			drawRandomHexCodes();
		} else if (timer.betweenSeconds(2.0, 3.5) && timer.tick() % 5 == 0) {
			drawRandomSprites();
		} else if (timer.atSecond(3.5)) {
			drawGrid();
		} else if (timer.atSecond(4.0)) {
			gameController.terminateCurrentState();
		}
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(currentImage, 0, 0, null);
	}

	private void clearBuffer() {
		gc.setColor(Color.BLACK);
		gc.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());
	}

	private void drawRandomHexCodes() {
		clearBuffer();
		gc.setColor(new Color(222, 222, 255));
		gc.setFont(SpritesheetPacMan.get().getArcadeFont());
		for (int row = 0; row < ArcadeWorld.SIZE_TILES.y(); ++row) {
			for (int col = 0; col < ArcadeWorld.SIZE_TILES.x(); ++col) {
				var hexCode = Integer.toHexString(rnd.nextInt(16));
				gc.drawString(hexCode, col * 8, row * 8 + 8);
			}
		}
	}

	private void drawRandomSprites() {
		clearBuffer();
		var sheet = gameController.game().variant() == GameVariant.MS_PACMAN ? SpritesheetMsPacMan.get()
				: SpritesheetPacMan.get();
		var image = sheet.getSourceImage();
		var w = image.getWidth();
		var h = image.getHeight();
		var cellSize = 16;
		var numRows = ArcadeWorld.SIZE_TILES.y() / 2;
		var numCols = ArcadeWorld.SIZE_TILES.x() / 2;
		for (int row = 0; row < numRows; ++row) {
			if (rnd.nextInt(100) < 10) {
				continue;
			}
			for (int col = 0; col < numCols; ++col) {
				var x = rnd.nextInt(w);
				if (x + cellSize > w) {
					x -= cellSize;
				}
				var y = rnd.nextInt(h);
				if (y + cellSize > h) {
					y -= cellSize;
				}
				var rect = image.getSubimage(x, y, cellSize, cellSize);
				sheet.drawSprite(gc, rect, cellSize * col, cellSize * row);
			}
		}
	}

	private void drawGrid() {
		clearBuffer();
		var cellSize = 16;
		var numRows = ArcadeWorld.SIZE_TILES.y() / 2;
		var numCols = ArcadeWorld.SIZE_TILES.x() / 2;
		gc.setColor(new Color(222, 222, 255));
		gc.setStroke(new BasicStroke(2));
		for (int row = 0; row < numRows; ++row) {
			gc.drawLine(0, row * cellSize, ArcadeWorld.SIZE_TILES.x() * TS, row * cellSize);
		}
		for (int col = 0; col <= numCols; ++col) {
			gc.drawLine(col * cellSize, 0, col * cellSize, ArcadeWorld.SIZE_PX.y());
		}
	}
}