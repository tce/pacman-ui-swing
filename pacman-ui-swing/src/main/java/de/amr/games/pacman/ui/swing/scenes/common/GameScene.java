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
package de.amr.games.pacman.ui.swing.scenes.common;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.common.GameSoundController;
import de.amr.games.pacman.event.GameEventAdapter;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.model.common.world.ArcadeWorld;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.mspacman.SpritesheetMsPacMan;
import de.amr.games.pacman.ui.swing.rendering.pacman.SpritesheetPacMan;
import de.amr.games.pacman.ui.swing.sound.MsPacManGameSounds;
import de.amr.games.pacman.ui.swing.sound.PacManGameSounds;

/**
 * Common game scene base class.
 * 
 * @author Armin Reichert
 */
public abstract class GameScene implements GameEventAdapter {

	private static final GameSoundController PACMAN_SOUNDS = new PacManGameSounds();
	private static final GameSoundController MS_PACMAN_SOUNDS = new MsPacManGameSounds();

	protected GameController gameController;
	protected V2i size = ArcadeWorld.WORLD_SIZE;
	protected GameModel game;
	protected Rendering2D r2D;

	public void setContext(GameController gameController) {
		this.gameController = gameController;
		this.game = gameController.game();
		this.r2D = switch (game.variant) {
		case MS_PACMAN -> SpritesheetMsPacMan.get();
		case PACMAN -> SpritesheetPacMan.get();
		};
		var sounds = switch (game.variant) {
		case MS_PACMAN -> MS_PACMAN_SOUNDS;
		case PACMAN -> PACMAN_SOUNDS;
		};
		gameController.setSounds(sounds);
	}

	public V2i size() {
		return size;
	}

	public void init() {
	}

	public void update() {
	}

	public void end() {
	}

	public void render(Graphics2D g) {
	}
}