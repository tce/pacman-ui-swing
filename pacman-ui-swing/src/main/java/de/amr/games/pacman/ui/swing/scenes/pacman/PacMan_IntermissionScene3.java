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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.GameController;
import de.amr.games.pacman.controller.pacman.Intermission3Context;
import de.amr.games.pacman.controller.pacman.Intermission3Controller;
import de.amr.games.pacman.controller.pacman.Intermission3State;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * Third intermission scene: Blinky in shred dress chases Pac-Man, comes back half-naked drawing dress over the floor.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene3 extends GameScene {

	private final Intermission3Controller sc;
	private final Intermission3Context context;

	private Player2D pacMan2D;

	public PacMan_IntermissionScene3(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sc = new Intermission3Controller(gameController);
		context = sc.getContext();
		context.playIntermissionSound = () -> SoundManager.get().loop(GameSound.INTERMISSION_3, 1);
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sc.init();
		pacMan2D = new Player2D(context.pac, game, r2D);
	}

	@Override
	public void update() {
		sc.updateState();
	}

	@Override
	public void render(Graphics2D g) {
		Rendering2D_PacMan r = (Rendering2D_PacMan) r2D;
		r.drawLevelCounter(g, gameController.game(), t(25), t(34));
		pacMan2D.render(g);
		if (sc.state == Intermission3State.CHASING) {
			r.drawBlinkyPatched(g, context.blinky);
		} else {
			r.drawBlinkyNaked(g, context.blinky);
		}
	}
}