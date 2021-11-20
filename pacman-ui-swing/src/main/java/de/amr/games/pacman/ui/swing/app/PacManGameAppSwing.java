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
import de.amr.games.pacman.ui.swing.PacManGameUI_Swing;

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

	private PacManGameController controller;
	private GameLoop gameLoop;

	public PacManGameAppSwing(Options options) {
		controller = new PacManGameController();
		controller.selectGameVariant(options.gameVariant);
		gameLoop = new GameLoop(controller);
		controller.setUI(new PacManGameUI_Swing(gameLoop, controller, options.height));
	}

	public static void main(String[] args) {
		invokeLater(() -> {
			PacManGameAppSwing app = new PacManGameAppSwing(new Options(args));
			app.gameLoop.start();
		});
	}
}