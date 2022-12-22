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
package de.amr.games.pacman.ui.swing.shell;

import de.amr.games.pacman.controller.common.Steering;
import de.amr.games.pacman.lib.steering.Direction;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.actors.Creature;

/**
 * Controls the player using the keyboard.
 * 
 * @author Armin Reichert
 */
public class KeySteering implements Steering {

	private String up;
	private String down;
	private String left;
	private String right;

	public KeySteering(String up, String down, String left, String right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	@Override
	public void init() {
	}

	@Override
	public void steer(GameModel game, Creature pac) {
		if (Keyboard.keyPressed(up)) {
			pac.setWishDir(Direction.UP);
		} else if (Keyboard.keyPressed(down)) {
			pac.setWishDir(Direction.DOWN);
		} else if (Keyboard.keyPressed(left)) {
			pac.setWishDir(Direction.LEFT);
		} else if (Keyboard.keyPressed(right)) {
			pac.setWishDir(Direction.RIGHT);
		}
	}
}