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
import java.util.BitSet;
import java.util.Map;

/**
 * Keyboard handler.
 * 
 * @author Armin Reichert
 */
public class Keyboard {

	public static final Keyboard theKeyboard = new Keyboard();

	public static final byte MOD_NONE = 0x0;
	public static final byte MOD_ALT = 0x1;
	public static final byte MOD_CTRL = 0x2;
	public static final byte MOD_SHIFT = 0x4;

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";
	//@formatter:off
	private static final Map<String, Integer> SPECS = Map.of(
			"Up",			KeyEvent.VK_UP, 
			"Down",		KeyEvent.VK_DOWN, 
			"Left",		KeyEvent.VK_LEFT, 
			"Right",	KeyEvent.VK_RIGHT, 
			"Esc",		KeyEvent.VK_ESCAPE, 
			"Space",	KeyEvent.VK_SPACE, 
			"Plus",		KeyEvent.VK_PLUS, 
			"Minus",	KeyEvent.VK_MINUS);
	//@formatter:on

	private static int code(String spec) {
		if (spec.length() == 1) {
			int symbol = spec.charAt(0);
			int index = LETTERS.indexOf(symbol);
			if (index != -1) {
				return KeyEvent.VK_A + index;
			}
			index = DIGITS.indexOf(symbol);
			if (index != -1) {
				return KeyEvent.VK_0 + index;
			}
		}
		if (SPECS.containsKey(spec)) {
			return SPECS.get(spec);
		}
		throw new IllegalArgumentException(String.format("Unknown key specification: %s", spec));
	}

	/**
	 * @param keySpec key specifier
	 * @return {@code true} if the specified key is pressed without any modifier key (ALT, CONTROL, SHIFT)
	 */
	public static boolean keyPressed(String spec) {
		return keyPressed(MOD_NONE, spec);
	}

	/**
	 * @param keySpec key specifier like "A", "5", "Up", "Esc", "Space".
	 * @return {@code true} if the specified key is pressed with the specified modifiers (MOD_ALT | MOD_CONTROL |
	 *         MOD_SHIFT)
	 */
	public static boolean keyPressed(int modifiers, String spec) {
		boolean pressed = theKeyboard.pressedState.get(code(spec));
		if (pressed) {
			theKeyboard.pressedState.clear(code(spec)); // TODO hack
		}
		return theKeyboard.modifierMask == modifiers && pressed;
	}

	public final KeyAdapter handler;
	private final BitSet pressedState = new BitSet(256);
	private byte modifierMask;

	public Keyboard() {
		handler = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				updateModifierMask(e);
				int code = e.getKeyCode();
				if (0 < code && code <= 255) {
					pressedState.set(code);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateModifierMask(e);
				int code = e.getKeyCode();
				pressedState.clear(code);
			}
		};
	}

	private void updateModifierMask(KeyEvent e) {
		modifierMask = MOD_NONE;
		if (e.isAltDown()) {
			modifierMask |= MOD_ALT;
		}
		if (e.isControlDown()) {
			modifierMask |= MOD_CTRL;
		}
		if (e.isShiftDown()) {
			modifierMask |= MOD_SHIFT;
		}
	}
}