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
package de.amr.games.pacman.ui.swing.entity.common;

import static de.amr.games.pacman.model.world.World.TS;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;

/**
 * Blinking energizer pellet.
 * 
 * @author Armin Reichert
 */
public class Energizer2D {

	private V2i tile;
	private TimedSeq<Boolean> animation;

	public Energizer2D(V2i tile) {
		this.tile = tile;
		animation = TimedSeq.pulse().frameDuration(10);
	}

	public TimedSeq<Boolean> getAnimation() {
		return animation;
	}

	public void render(Graphics2D g) {
		if (!animation.frame()) {
			g.setColor(Color.BLACK);
			g.fillRect(tile.x * TS, tile.y * TS, TS, TS);
		}
		animation.advance();
	}
}