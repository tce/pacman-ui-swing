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
package de.amr.games.pacman.ui.swing.app;

import static de.amr.games.pacman.lib.Logging.log;

import de.amr.games.pacman.model.common.GameVariant;

class Options {

	double height = 576;
	GameVariant gameVariant = GameVariant.PACMAN;

	public Options(String[] args) {
		int i = -1;
		while (++i < args.length) {
			if ("-pacman".equals(args[i])) {
				gameVariant = GameVariant.PACMAN;
				continue;
			}
			if ("-mspacman".equals(args[i])) {
				gameVariant = GameVariant.MS_PACMAN;
				continue;
			}
			if ("-height".equals(args[i])) {
				if (++i == args.length) {
					log("Error parsing options: missing height value.");
					break;
				}
				try {
					height = Double.parseDouble(args[i]);
				} catch (NumberFormatException x) {
					log("Error parsing options: '%s' is no legal height value.", args[i]);
				}
				continue;
			}
			log("Error parsing options: Found garbage '%s'", args[i]);
		}
	}
}