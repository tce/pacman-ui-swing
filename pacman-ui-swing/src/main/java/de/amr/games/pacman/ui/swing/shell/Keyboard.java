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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

/**
 * Keyboard handler.
 * 
 * @author Armin Reichert
 */
public class Keyboard {

	private static final Keyboard theKeyboard = new Keyboard();

	public static Keyboard get() {
		return theKeyboard;
	}

	public static final byte ALT = 0x1;
	public static final byte CONTROL = 0x2;
	public static final byte SHIFT = 0x4;

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";

	private static int code(String keySpec) {
		if (keySpec.length() == 1) {
			int symbol = keySpec.charAt(0);
			int index = LETTERS.indexOf(symbol);
			if (index != -1) {
				return KeyEvent.VK_A + index;
			}
			index = DIGITS.indexOf(symbol);
			if (index != -1) {
				return KeyEvent.VK_0 + index;
			}
		}
		return switch (keySpec) {
		case "Up" -> KeyEvent.VK_UP;
		case "Down" -> KeyEvent.VK_DOWN;
		case "Left" -> KeyEvent.VK_LEFT;
		case "Right" -> KeyEvent.VK_RIGHT;
		case "Esc" -> KeyEvent.VK_ESCAPE;
		case "Space" -> KeyEvent.VK_SPACE;
		case "Plus" -> KeyEvent.VK_PLUS;
		case "Minus" -> KeyEvent.VK_MINUS;
		default -> throw new IllegalArgumentException(String.format("Unknown key specification: %s", keySpec));
		};
	}

	private final KeyAdapter handler;
	private final BitSet downState = new BitSet(256);
	private byte modifierMask;

	public Keyboard() {
		handler = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (0 < e.getKeyCode() && e.getKeyCode() < 256) {
					downState.set(e.getKeyCode());
				}
				modifierMask = 0;
				if (e.isAltDown()) {
					modifierMask |= ALT;
				}
				if (e.isControlDown()) {
					modifierMask |= CONTROL;
				}
				if (e.isShiftDown()) {
					modifierMask |= SHIFT;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				downState.clear(e.getKeyCode());
				modifierMask = 0;
				if (e.isAltDown()) {
					modifierMask |= ALT;
				}
				if (e.isControlDown()) {
					modifierMask |= CONTROL;
				}
				if (e.isShiftDown()) {
					modifierMask |= SHIFT;
				}
			}
		};
	}

	public KeyListener keyListener() {
		return handler;
	}

	/**
	 * @param keySpec key specifier
	 * @return {@code true} if the specified key is pressed without any modifier key (ALT, CONTROL. SHIFT)
	 */
	public boolean pressed(String keySpec) {
		return modifierMask == 0 && consume(keySpec);
	}

	/**
	 * @param keySpec key specifier
	 * @return {@code true} if the specified key is pressed with the specified modifier mask (ALT | CONTROL | SHIFT)
	 */
	public boolean pressed(int modifiers, String keySpec) {
		return modifierMask == modifiers && consume(keySpec);
	}

	private boolean consume(String spec) {
		int code = code(spec);
		boolean down = downState.get(code);
		if (down) {
			downState.clear(code); // TODO check this
		}
		return down;
	}

	/**
	 * Clears the keyboard state.
	 */
	public void clear() {
		downState.clear();
		modifierMask = 0;
	}
}