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

package de.amr.games.pacman.ui.swing.scenes.mspacman;

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.Ghost;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.model.common.world.World;
import de.amr.games.pacman.ui.swing.assets.AssetLoader;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.shell.Keyboard;

/**
 * @author Armin Reichert
 */
public class MsPacMan_CreditScene extends GameScene {

	private BufferedImage midwayLogo = AssetLoader.image("/mspacman/graphics/midway.png");

	public MsPacMan_CreditScene(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
	}

	@Override
	public void update() {
		if (Keyboard.get().pressed("5")) {
			SoundManager.get().play(GameSound.CREDIT);
			gameController.addCredit();
			return;
		} else if (Keyboard.get().pressed("Space")) {
			gameController.requestGame();
			return;
		}
	}

	@Override
	public void render(Graphics2D g) {

		r2D.drawScore(g, game, true);

		g.setFont(r2D.getArcadeFont());
		g.setColor(r2D.getGhostColor(Ghost.ORANGE_GHOST));
		g.drawString("PUSH START BUTTON", t(6), t(16));
		g.drawString("1 PLAYER ONLY", t(8), t(18));
		g.drawString("ADDITIONAL    AT 10000", t(2), t(25));
		BufferedImage msPacMan = r2D.s(1, 0);
		r2D.renderSprite(g, msPacMan, t(13) + World.HTS, t(24) - 2);
		g.setFont(r2D.getArcadeFont());
		g.setFont(r2D.getArcadeFont().deriveFont(6.0f));
		g.drawString("PTS", t(25), t(25));

		drawCopyright(g);

		r2D.drawCredit(g, gameController.credit());
	}

	private void drawCopyright(Graphics2D g) {
		double scale = (double) ArcadeWorld.TILES_Y / midwayLogo.getHeight();
		g.drawImage(midwayLogo, t(4), t(28) + 3, (int) (scale * midwayLogo.getWidth()),
				(int) (scale * midwayLogo.getHeight()), null);
		g.setColor(Color.RED);
		g.setFont(new Font("Dialog", Font.PLAIN, 11));
		g.drawString("\u00a9", t(9), t(30) + 2); // (c) symbol
		g.setFont(r2D.getArcadeFont());
		g.drawString("MIDWAY MFG CO", t(11), t(30));
		g.drawString("1980/1981", t(12), t(32));
	}

}