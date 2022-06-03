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
package de.amr.games.pacman.ui.swing.assets;

import java.awt.Color;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.V2i;

/**
 * A spritesheet.
 * 
 * @author Armin Reichert
 */
public class Spritesheet {

	public final BufferedImage image;
	public final int raster;

	public Spritesheet(BufferedImage image, int pixels) {
		this.image = image;
		raster = pixels;
	}

	public BufferedImage si(int x, int y, int width, int height) {
		return image.getSubimage(x, y, width, height);
	}

	public BufferedImage tilesFrom(int x, int y, int tileX, int tileY, int numTilesX, int numTilesY) {
		return si(x + tileX * raster, y + tileY * raster, numTilesX * raster, numTilesY * raster);
	}

	public BufferedImage tiles(int tileX, int tileY, int numTilesX, int numTilesY) {
		return tilesFrom(0, 0, tileX, tileY, numTilesX, numTilesY);
	}

	public BufferedImage tile(int tileX, int tileY) {
		return tiles(tileX, tileY, 1, 1);
	}

	public BufferedImage tile(V2i tile) {
		return tile(tile.x, tile.y);
	}

	public BufferedImage createBrightEffect(BufferedImage src, Color borderColor, Color fillColor) {
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		dst.getGraphics().drawImage(src, 0, 0, null);
		for (int x = 0; x < src.getWidth(); ++x) {
			for (int y = 0; y < src.getHeight(); ++y) {
				if (src.getRGB(x, y) == borderColor.getRGB()) {
					dst.setRGB(x, y, Color.WHITE.getRGB());
				} else if (src.getRGB(x, y) == fillColor.getRGB()) {
					dst.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					dst.setRGB(x, y, src.getRGB(x, y));
				}
			}
		}
		return dst;
	}
}