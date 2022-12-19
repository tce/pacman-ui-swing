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

import static de.amr.games.pacman.model.common.world.World.HTS;
import static de.amr.games.pacman.model.common.world.World.TS;
import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameState;
import de.amr.games.pacman.lib.Vector2d;
import de.amr.games.pacman.lib.Vector2i;
import de.amr.games.pacman.model.common.world.World;

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
		int scatterPhase = game.huntingTimer().scatterPhase();
		int chasingPhase = game.huntingTimer().chasingPhase();
		String stateText;
		if (state == GameState.HUNTING && game.huntingTimer().inScatterPhase()) {
			var ticks = game.huntingTimer().remaining();
			stateText = "Scattering Phase %d Remaining: %s".formatted(scatterPhase, state.timer().ticksToString(ticks));
		} else if (state == GameState.HUNTING && game.huntingTimer().inChasingPhase()) {
			var ticks = game.huntingTimer().remaining();
			stateText = "Chasing Phase %d Remaining: %s".formatted(chasingPhase, state.timer().ticksToString(ticks));
		} else {
			var ticks = state.timer().tick();
			stateText = "State %s Running: %s".formatted(state, state.timer().ticksToString(ticks));
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 6));
		g.drawString(stateText, t(1), t(3));
		game.ghosts().forEach(ghost -> {
			g.setColor(Color.WHITE);
			g.drawRect((int) ghost.position().x(), (int) ghost.position().y(), TS, TS);
			ghost.targetTile().ifPresent(targetTile -> {
				Color c = ghostColors[ghost.id()];
				g.setColor(c);
				g.fillRect(t(targetTile.x()) + HTS / 2, t(targetTile.y()) + HTS / 2, HTS, HTS);
				g.setStroke(new BasicStroke(0.5f));
				Vector2d targetPosition = new Vector2d(targetTile.scaled(TS)).plus(HTS, HTS);
				g.drawLine((int) ghost.position().x(), (int) ghost.position().y(), (int) targetPosition.x(),
						(int) targetPosition.y());
			});
		});
		game.pac().targetTile().ifPresent(targetTile -> {
			g.setColor(new Color(255, 255, 0, 200));
			g.fillRect(t(targetTile.x()), t(targetTile.y()), TS, TS);
		});
	}

	public static void drawMazeStructure(Graphics2D g, World world) {
		Color dark = new Color(80, 80, 80);
		for (int x = 0; x < world.numCols(); ++x) {
			for (int y = 0; y < world.numRows(); ++y) {
				Vector2i tile = new Vector2i(x, y);
				if (world.isIntersection(tile)) {
					g.setColor(dark);
					g.drawOval(t(x), t(y), TS, TS);
				}
			}
		}
	}
}