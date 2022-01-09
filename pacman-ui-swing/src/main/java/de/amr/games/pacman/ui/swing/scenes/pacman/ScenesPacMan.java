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

import static de.amr.games.pacman.model.world.PacManGameWorld.t;
import static de.amr.games.pacman.ui.PacManGameSound.BONUS_EATEN;
import static de.amr.games.pacman.ui.PacManGameSound.CREDIT;
import static de.amr.games.pacman.ui.PacManGameSound.EXTRA_LIFE;
import static de.amr.games.pacman.ui.PacManGameSound.GAME_READY;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_EATEN;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_RETURNING;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_SIREN_1;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_SIREN_2;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_SIREN_3;
import static de.amr.games.pacman.ui.PacManGameSound.GHOST_SIREN_4;
import static de.amr.games.pacman.ui.PacManGameSound.INTERMISSION_1;
import static de.amr.games.pacman.ui.PacManGameSound.INTERMISSION_2;
import static de.amr.games.pacman.ui.PacManGameSound.INTERMISSION_3;
import static de.amr.games.pacman.ui.PacManGameSound.PACMAN_DEATH;
import static de.amr.games.pacman.ui.PacManGameSound.PACMAN_MUNCH;
import static de.amr.games.pacman.ui.PacManGameSound.PACMAN_POWER;

import java.awt.Dimension;
import java.util.List;

import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.pacman.PacManGameRendering;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;

/**
 * Pac-Man game scenes.
 * 
 * @author Armin Reichert
 */
public class ScenesPacMan {

	public static final Dimension UNSCALED_SIZE = new Dimension(t(28), t(36));
	public static PacManGameRendering RENDERING = new PacManGameRendering();

	public static final SoundManager SOUNDS = new SoundManager();
	static {
		SOUNDS.put(CREDIT, "/pacman/sound/credit.wav");
		SOUNDS.put(EXTRA_LIFE, "/pacman/sound/extend.wav");
		SOUNDS.put(GAME_READY, "/pacman/sound/game_start.wav");
		SOUNDS.put(BONUS_EATEN, "/pacman/sound/eat_fruit.wav");
		SOUNDS.put(PACMAN_MUNCH, "/pacman/sound/munch_1.wav");
		SOUNDS.put(PACMAN_DEATH, "/pacman/sound/death_1.wav");
		SOUNDS.put(PACMAN_POWER, "/pacman/sound/power_pellet.wav");
		SOUNDS.put(GHOST_EATEN, "/pacman/sound/eat_ghost.wav");
		SOUNDS.put(GHOST_RETURNING, "/pacman/sound/retreating.wav");
		SOUNDS.put(GHOST_SIREN_1, "/pacman/sound/siren_1.wav");
		SOUNDS.put(GHOST_SIREN_2, "/pacman/sound/siren_2.wav");
		SOUNDS.put(GHOST_SIREN_3, "/pacman/sound/siren_3.wav");
		SOUNDS.put(GHOST_SIREN_4, "/pacman/sound/siren_4.wav");
		SOUNDS.put(INTERMISSION_1, "/pacman/sound/intermission.wav");
		SOUNDS.put(INTERMISSION_2, "/pacman/sound/intermission.wav");
		SOUNDS.put(INTERMISSION_3, "/pacman/sound/intermission.wav");
	}

	public static final List<GameScene> SCENES = List.of( //
			new PacMan_IntroScene(UNSCALED_SIZE), //
			new PacMan_IntermissionScene1(UNSCALED_SIZE), //
			new PacMan_IntermissionScene2(UNSCALED_SIZE), //
			new PacMan_IntermissionScene3(UNSCALED_SIZE), //
			new PlayScene(UNSCALED_SIZE, RENDERING, SOUNDS) //
	);
}