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
package de.amr.games.pacman.ui.swing.rendering.common;

import static de.amr.games.pacman.lib.TickTimer.ticksToString;
import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.lib.V2d;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;

/**
 * @author Armin Reichert
 */
public class DebugDraw {

	private DebugDraw() {
	}

	public static void drawPlaySceneDebugInfo(Graphics2D g, GameController controller) {
		final Color[] ghostColors = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
		var game = controller.game();
		var state = controller.state();
		int scatterPhase = game.huntingTimer.scatterPhase();
		int chasingPhase = game.huntingTimer.chasingPhase();
		String stateText;
		if (state == GameState.HUNTING && game.huntingTimer.inScatterPhase()) {
			var ticks = game.huntingTimer.remaining();
			stateText = "Scattering Phase %d Remaining: %s".formatted(scatterPhase, ticksToString(ticks));
		} else if (state == GameState.HUNTING && game.huntingTimer.inChasingPhase()) {
			var ticks = game.huntingTimer.remaining();
			stateText = "Chasing Phase %d Remaining: %s".formatted(chasingPhase, ticksToString(ticks));
		} else {
			var ticks = state.timer().tick();
			stateText = "State %s Running: %s".formatted(controller.state(), ticksToString(ticks));
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 6));
		g.drawString(stateText, t(1), t(3));
		game.ghosts().forEach(ghost -> {
			g.setColor(Color.WHITE);
			g.drawRect((int) ghost.getPosition().x(), (int) ghost.getPosition().y(), TS, TS);
			if (ghost.targetTile != null) {
				Color c = ghostColors[ghost.id];
				g.setColor(c);
				g.fillRect(t(ghost.targetTile.x()) + HTS / 2, t(ghost.targetTile.y()) + HTS / 2, HTS, HTS);
				g.setStroke(new BasicStroke(0.5f));
				V2d targetPosition = new V2d(ghost.targetTile.scaled(TS)).plus(HTS, HTS);
				g.drawLine((int) ghost.getPosition().x(), (int) ghost.getPosition().y(), (int) targetPosition.x(),
						(int) targetPosition.y());
			}
		});
		if (game.pac.targetTile != null) {
			g.setColor(new Color(255, 255, 0, 200));
			g.fillRect(t(game.pac.targetTile.x()), t(game.pac.targetTile.y()), TS, TS);
		}
	}

	public static void drawMazeStructure(Graphics2D g, GameModel game) {
		Color dark = new Color(80, 80, 80);
		for (int x = 0; x < game.level.world.numCols(); ++x) {
			for (int y = 0; y < game.level.world.numRows(); ++y) {
				V2i tile = new V2i(x, y);
				if (game.level.world.isIntersection(tile)) {
					g.setColor(dark);
					g.drawOval(t(x), t(y), TS, TS);
				}
			}
		}
	}
}