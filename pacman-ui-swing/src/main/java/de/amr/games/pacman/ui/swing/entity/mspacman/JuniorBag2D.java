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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.amr.games.pacman.model.mspacman.JuniorBag;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;

public class JuniorBag2D {

	private final JuniorBag bag;
	private final Rendering2D r2D;
	private BufferedImage blueBag;
	private BufferedImage junior;

	public JuniorBag2D(JuniorBag bag, Rendering2D r2D) {
		this.bag = bag;
		this.r2D = r2D;
		blueBag = Rendering2D_MsPacMan.get().getBlueBag();
		junior = Rendering2D_MsPacMan.get().getJunior();
	}

	public void render(Graphics2D g) {
		r2D.drawEntity(g, bag, bag.open ? junior : blueBag);
	}
}