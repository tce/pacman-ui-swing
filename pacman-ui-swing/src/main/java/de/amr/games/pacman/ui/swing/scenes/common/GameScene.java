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
package de.amr.games.pacman.ui.swing.scenes.common;

import java.awt.Dimension;
import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.event.DefaultGameEventHandler;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

/**
 * Common game scene base class.
 * 
 * @author Armin Reichert
 */
public abstract class GameScene extends DefaultGameEventHandler {

	protected final PacManGameUI_Swing ui;
	protected final Dimension size;
	protected final Rendering2D rendering;
	protected final SoundManager sounds;
	protected GameController gameController;
	protected GameModel game;

	public GameScene(PacManGameUI_Swing ui, Dimension size, Rendering2D rendering, SoundManager sounds) {
		this.ui = ui;
		this.size = size;
		this.rendering = rendering;
		this.sounds = sounds;
	}

	public Dimension size() {
		return size;
	}

	public void init(GameController gameController) {
		this.gameController = gameController;
		this.game = gameController.game;
	}

	public abstract void update();

	public void end() {
	}

	public abstract void render(Graphics2D g);
}