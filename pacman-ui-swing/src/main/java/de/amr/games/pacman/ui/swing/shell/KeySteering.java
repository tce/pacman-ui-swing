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
import de.amr.games.pacman.model.common.GameLevel;
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

	private double control_error_percent = 0.2;

	private boolean skipControls = false;

	public KeySteering(String up, String down, String left, String right) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	public void setSkipControls(boolean value)
	{
		skipControls = value;
	}


	private boolean skip()
	{
		return (skipControls && Math.random() < control_error_percent);
	}
	@Override
	public void steer(GameLevel level, Creature pac) {
		if (Keyboard.keyPressed(up)) {
			if (!skip()) pac.setWishDir(Direction.UP);
		} else if (Keyboard.keyPressed(down)) {
			if (!skip()) pac.setWishDir(Direction.DOWN);
		} else if (Keyboard.keyPressed(left)) {
			if (!skip()) pac.setWishDir(Direction.LEFT);
		} else if (Keyboard.keyPressed(right)) {
			if (!skip()) pac.setWishDir(Direction.RIGHT);
		}
	}
}