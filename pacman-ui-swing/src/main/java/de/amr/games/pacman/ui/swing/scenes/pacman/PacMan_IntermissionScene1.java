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
package de.amr.games.pacman.ui.swing.scenes.pacman;

import static de.amr.games.pacman.model.common.world.World.t;

import java.awt.Graphics2D;

import de.amr.games.pacman.controller.common.GameController;
import de.amr.games.pacman.controller.pacman.Intermission1Context;
import de.amr.games.pacman.controller.pacman.Intermission1Controller;
import de.amr.games.pacman.controller.pacman.Intermission1State;
import de.amr.games.pacman.lib.TimedSeq;
import de.amr.games.pacman.lib.V2i;
import de.amr.games.pacman.model.common.GameModel;
import de.amr.games.pacman.ui.swing.assets.GameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.entity.common.Ghost2D;
import de.amr.games.pacman.ui.swing.entity.common.Player2D;
import de.amr.games.pacman.ui.swing.entity.pacman.BigPacMan2D;
import de.amr.games.pacman.ui.swing.rendering.common.Rendering2D;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;

/**
 * First intermission scene: Blinky chases Pac-Man and is then chased by a huge Pac-Man.
 * 
 * @author Armin Reichert
 */
public class PacMan_IntermissionScene1 extends GameScene {

	private final Intermission1Controller sc;
	private final Intermission1Context context;

	private Player2D pacMan2D;
	private Ghost2D blinky2D;
	private BigPacMan2D bigPacMan2D;

	public PacMan_IntermissionScene1(GameController gameController, V2i size, Rendering2D r2D) {
		super(gameController, size, r2D);
		sc = new Intermission1Controller(gameController);
		context = sc.getContext();
		context.playIntermissionSound = () -> SoundManager.get().loop(GameSound.INTERMISSION_1, 1);
	}

	@Override
	public void init(GameModel game) {
		super.init(game);
		sc.init();

		pacMan2D = new Player2D(context.pac, game, r2D);
		blinky2D = new Ghost2D(context.blinky, game, r2D);
		bigPacMan2D = new BigPacMan2D(context.pac, (Rendering2D_PacMan) r2D);
		pacMan2D.munchings.values().forEach(TimedSeq::restart);
		blinky2D.animKicking.values().forEach(TimedSeq::restart);
		blinky2D.animFrightened.restart();
		bigPacMan2D.munchingAnimation.restart();
	}

	@Override
	public void update() {
		sc.updateState();
	}

	@Override
	public void render(Graphics2D g) {
		Rendering2D_PacMan r = (Rendering2D_PacMan) r2D;
		blinky2D.render(g);
		if (sc.state() == Intermission1State.CHASING_PACMAN) {
			pacMan2D.render(g);
		} else {
			bigPacMan2D.render(g);
		}
		r.drawLevelCounter(g, game, t(25), t(34));
	}
}