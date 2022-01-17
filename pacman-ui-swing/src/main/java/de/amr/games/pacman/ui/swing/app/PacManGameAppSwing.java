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

import static java.awt.EventQueue.invokeLater;

import de.amr.games.pacman.controller.PacManGameController;
import de.amr.games.pacman.ui.swing.shell.ManualPlayerControl;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

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

	public static void main(String[] args) {
		Options options = new Options(args);
		invokeLater(() -> new PacManGameAppSwing(options));
	}

	private final PacManGameController controller;
	private final PacManGameUI_Swing view;
	private final GameLoop gameLoop = new GameLoop();

	public PacManGameAppSwing(Options options) {
		controller = new PacManGameController(options.gameVariant);
		view = new PacManGameUI_Swing(gameLoop, controller, options.height);
		controller.addGameEventListener(view);
		controller.setPlayerControl(new ManualPlayerControl(view.keyboard, "Up", "Down", "Left", "Right"));
		gameLoop.action = () -> {
			gameLoop.clock.frame(controller::updateState);
			view.onTick();
		};
		gameLoop.start();
	}
}