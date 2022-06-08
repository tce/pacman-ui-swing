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
package de.amr.games.pacman.ui.swing.entity.mspacman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.lib.animation.GenericAnimation;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.mspacman.Flap;
import de.amr.games.pacman.ui.swing.entity.common.GameEntity2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;

/**
 * The film flap used at the beginning of the Ms. Pac-Man intermission scenes.
 * 
 * @author Armin Reichert
 */
public class Flap2D extends GameEntity2D {

	public final Flap flap;
	public GenericAnimation<BufferedImage> animation;

	public Flap2D(Flap flap, GameModel game) {
		super(game);
		this.flap = flap;
		animation = Rendering2D_MsPacMan.get().createFlapAnimation();
	}

	@Override
	public void render(Graphics2D g, Rendering2D r2D) {
		if (flap.visible) {
			BufferedImage sprite = animation.animate();
			r2D.drawEntity(g, flap, sprite);
			g.setFont(r2D.getArcadeFont());
			g.setColor(new Color(222, 222, 255));
			g.drawString(flap.number + "", (int) flap.position.x + sprite.getWidth() - 25, (int) flap.position.y + 18);
			g.drawString(flap.text, (int) flap.position.x + sprite.getWidth(), (int) flap.position.y);
		}
	}
}