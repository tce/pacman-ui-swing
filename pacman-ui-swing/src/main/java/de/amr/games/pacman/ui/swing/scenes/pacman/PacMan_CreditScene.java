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
public class PacMan_CreditScene extends GameScene {

	@Override
	public void update() {
		if (Keyboard.keyPressed("5")) {
			gameController.state().addCredit(game);
		} else if (Keyboard.keyPressed("1")) {
			gameController.requestGame();
		}
	}

	@Override
	public void render(Graphics2D g) {

		r2D.drawScores(g, game, true);

		g.setFont(r2D.getArcadeFont());
		g.setColor(r2D.getGhostColor(Ghost.ORANGE_GHOST));
		g.drawString("PUSH START BUTTON", t(6), t(17));

		g.setFont(r2D.getArcadeFont());
		g.setColor(r2D.getGhostColor(Ghost.CYAN_GHOST));
		g.drawString("1 PLAYER ONLY", t(8), t(21));

		g.setFont(r2D.getArcadeFont());
		g.setColor(new Color(255, 184, 174));
		g.drawString("BONUS PAC-MAN FOR 10000", t(1), t(25));

		g.setFont(r2D.getArcadeFont());
		g.setFont(r2D.getArcadeFont().deriveFont(6.0f));
		g.drawString("PTS", t(25), t(25));

		r2D.drawCopyright(g, t(4), t(29));

		r2D.drawCredit(g, gameController.credit());
//		r2D.drawLevelCounter(g, game, t(24), t(34));
	}
}