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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.lib.TickTimer;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;

/**
 * @author Armin Reichert
 */
public class BootScene extends GameScene {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private final Random rnd = new Random();
	private BufferedImage buffer;

	@Override
	public void init() {
		buffer = new BufferedImage(size.x(), size.y(), BufferedImage.TYPE_INT_RGB);
		clearBuffer();
	}

	private void clearBuffer() {
		var g = buffer.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
	}

	@Override
	public void update() {
		var g = buffer.createGraphics();
		var tick = gameController.state().timer().tick();
		if (between(0.5, 1.5, tick) && tick % 5 == 0) {
			clearBuffer();
			drawHexCodes(g);
		} else if (between(1.5, 3.0, tick) && tick % 10 == 0) {
			clearBuffer();
			drawRandomSprites(g);
		} else if (tick == TickTimer.secToTicks(3.0)) {
			clearBuffer();
			drawGrid(g);
		} else if (tick == TickTimer.secToTicks(3.5)) {
			gameController.terminateCurrentState();
		}
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(buffer, 0, 0, null);
	}

	private boolean between(double secLeft, double secRight, double tick) {
		return TickTimer.secToTicks(secLeft) <= tick && tick < TickTimer.secToTicks(secRight);
	}

	private void drawHexCodes(Graphics2D g) {
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(SpritesheetPacMan.get().getArcadeFont());
		for (int row = 0; row < ArcadeWorld.TILES_Y; ++row) {
			for (int col = 0; col < ArcadeWorld.TILES_X; ++col) {
				var hexCode = Integer.toHexString(rnd.nextInt(16));
				g.drawString(hexCode, col * 8, row * 8 + 8);
			}
		}
		LOGGER.trace("Hex codes");
	}

	private void drawRandomSprites(Graphics2D g) {
		var sheet = gameController.game().variant() == GameVariant.MS_PACMAN ? SpritesheetMsPacMan.get()
				: SpritesheetPacMan.get();
		var sheetWidth = sheet.getSourceImage().getWidth();
		var sheetHeight = sheet.getSourceImage().getHeight();
		for (int row = 0; row < ArcadeWorld.TILES_Y / 2; ++row) {
			if (rnd.nextInt(100) < 25) {
				continue;
			}
			for (int col = 0; col < ArcadeWorld.TILES_X / 2; ++col) {
				var x = rnd.nextInt(sheetWidth);
				if (x + 16 > sheetWidth) {
					x -= 16;
				}
				var y = rnd.nextInt(sheetHeight);
				if (y + 16 > sheetHeight) {
					y -= 16;
				}
				var rect = sheet.getSourceImage().getSubimage(x, y, 16, 16);
				sheet.drawSprite(g, rect, 16 * col, 16 * row);
			}
		}
		LOGGER.trace("Random sprites");
	}

	private void drawGrid(Graphics2D g) {
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(2));
		for (int row = 0; row < ArcadeWorld.TILES_Y / 2; ++row) {
			g.drawLine(0, row * 2 * TS, ArcadeWorld.TILES_X * TS, row * 2 * TS);
		}
		for (int col = 0; col < ArcadeWorld.TILES_X / 2; ++col) {
			g.drawLine(col * 2 * TS, 0, col * 2 * TS, ArcadeWorld.TILES_Y * TS);
		}
		LOGGER.trace("Grid");
	}
}