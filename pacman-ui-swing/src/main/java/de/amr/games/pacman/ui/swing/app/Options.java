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
package de.amr.games.pacman.ui.swing.app;

import java.util.List;

import de.amr.games.pacman.lib.OptionParser;
import de.amr.games.pacman.model.common.GameVariant;

/**
 * @author Armin Reichert
 */
class Options extends OptionParser {

	private static final String OPT_HEIGHT = "-height";

	private static final List<String> OPTION_NAMES = List.of(OPT_HEIGHT, OPT_VARIANT_MSPACMAN, OPT_VARIANT_PACMAN);

	@Override
	protected List<String> options() {
		return OPTION_NAMES;
	}

	public float height = 576;
	public GameVariant gameVariant = GameVariant.PACMAN;

	public Options(List<String> args) {
		i = 0;
		while (i < args.size()) {
			height = optNameValue(args, OPT_HEIGHT, Float::valueOf).orElse(height);
			gameVariant = optName(args, OPT_VARIANT_MSPACMAN, OptionParser::convertGameVariant).orElse(gameVariant);
			gameVariant = optName(args, OPT_VARIANT_PACMAN, OptionParser::convertGameVariant).orElse(gameVariant);
			++i;
		}
	}
}