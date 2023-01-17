/*
MIT License

Copyright (c) 2022 Armin Reichert

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

package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Graphics2D;

import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * @author Armin Reichert
 */
public class PacManCreditScene extends GameScene {

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.state().addCredit(game);
		} else if (Keyboard.keyPressed("1")) {
			gameController.state().requestGame(game);
		}
	}

	@Override
	public void render(Graphics2D g) {
		r2D.drawScores(g, game, true);
		var arcade8 = r2D.getArcadeFont();
		var arcade6 = arcade8.deriveFont(6f);
		r2D.drawText(g, "PUSH START BUTTON", r2D.getGhostColor(Ghost.ID_ORANGE_GHOST), arcade8, t(6), t(17));
		r2D.drawText(g, "1 PLAYER ONLY", r2D.getGhostColor(Ghost.ID_CYAN_GHOST), arcade8, t(8), t(21));
		r2D.drawText(g, "1 PLAYER ONLY", r2D.getGhostColor(Ghost.ID_CYAN_GHOST), arcade8, t(8), t(21));
		r2D.drawText(g, "BONUS PAC-MAN FOR 10000", new Color(255, 184, 174), arcade8, t(1), t(25));
		r2D.drawText(g, "PTS", new Color(255, 184, 174), arcade6, t(25), t(25));
		r2D.drawCopyright(g, t(4), t(29));
		r2D.drawCredit(g, game.credit());
		r2D.drawLevelCounter(g, game.levelCounter());
	}
}