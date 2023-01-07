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

import static de.amr.games.pacman.lib.option.Option.integerOption;
import static de.amr.games.pacman.lib.option.Option.option;
import static java.awt.EventQueue.invokeLater;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.event.GameEvents;
import de.amr.games.pacman.lib.option.Option;
import de.amr.games.pacman.lib.option.OptionParser;
import de.amr.games.pacman.model.common.GameVariant;
import de.amr.games.pacman.ui.swing.shell.KeySteering;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI;

/**
 * The Pac-Man application.
 * 
 * Command-line arguments:
 * <ul>
 * <li><code>-height</code> &lt;pixels&gt;: Height of UI in pixels (default: 576)</li>
 * <li><code>-pacman</code>: Starts the game in Pac-Man mode</li>
 * <li><code>-mspacman</code>: Starts game in Ms. Pac-Man mode</li>
 * </ul>
 * 
 * @author Armin Reichert
 */
public class PacManGameAppSwing {

	static final Option<Integer> OPT_HEIGHT = integerOption("-height", 576);
	static final Option<GameVariant> OPT_VARIANT = option("-variant", GameVariant.PACMAN, GameVariant::valueOf);

	public static void main(String[] args) {
		new OptionParser(OPT_HEIGHT, OPT_VARIANT).parse(args);
		var app = new PacManGameAppSwing(OPT_VARIANT.getValue());
		invokeLater(app::createAndShowUI);
	}

	private GameController gameController;

	public PacManGameAppSwing(GameVariant gameVariant) {
		gameController = new GameController(gameVariant);
	}

	private void createAndShowUI() {
		var gameLoop = new GameLoop();
		var ui = new PacManGameUI(gameLoop, gameController, OPT_HEIGHT.getValue());
		ui.show();
		GameEvents.addListener(ui);
		gameController.setManualPacSteering(new KeySteering("Up", "Down", "Left", "Right"));
		gameLoop.action = () -> {
			gameLoop.clock.frame(gameController::update);
			ui.update();
		};
		gameLoop.start();
	}
}