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
package de.amr.games.pacman.ui.swing;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.BitSet;

/**
 * Keyboard handler.
 * 
 * @author Armin Reichert
 */
public class Keyboard {

	private final BitSet pressedKeys = new BitSet(256);

	public Keyboard(Component component) {
		component.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() != 0 && e.getKeyCode() < 256) {
					pressedKeys.set(e.getKeyCode());
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				pressedKeys.clear(e.getKeyCode());
			}
		});
	}

	public boolean keyPressed(String keySpec) {
		return pressedKeys.get(keyCode(keySpec));
	}

	public boolean anyKeyPressed() {
		return !pressedKeys.isEmpty();
	}

	public void clear() {
		pressedKeys.clear();
	}

	public void clearKey(String keySpec) {
		pressedKeys.clear(keyCode(keySpec));
	}

	private int keyCode(String keySpec) {
		if (keySpec.length() == 1) {
			int c = keySpec.charAt(0);
			int index = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c);
			if (index != -1) {
				return KeyEvent.VK_A + index;
			}
			index = "0123456789".indexOf(c);
			if (index != -1) {
				return KeyEvent.VK_0 + index;
			}
		}
		switch (keySpec) {
		case "Up":
			return KeyEvent.VK_UP;
		case "Down":
			return KeyEvent.VK_DOWN;
		case "Left":
			return KeyEvent.VK_LEFT;
		case "Right":
			return KeyEvent.VK_RIGHT;
		case "Esc":
			return KeyEvent.VK_ESCAPE;
		case "Space":
			return KeyEvent.VK_SPACE;
		case "Plus":
			return KeyEvent.VK_PLUS;
		case "Minus":
			return KeyEvent.VK_MINUS;
		default:
			throw new IllegalArgumentException(String.format("Unknown key specification: %s", keySpec));
		}
	}
}