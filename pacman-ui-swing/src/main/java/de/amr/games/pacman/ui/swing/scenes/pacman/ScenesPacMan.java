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
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.amr.games.pacman.ui.PacManGameSound;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;

/**
 * Pac-Man game scenes.
 * 
 * @author Armin Reichert
 */
public class ScenesPacMan {

	public static final Dimension UNSCALED_SIZE = new Dimension(t(28), t(36));
	public static Rendering2D_PacMan RENDERING = new Rendering2D_PacMan();

	private static Entry<PacManGameSound, String> entry(PacManGameSound sound, String path) {
		return new SimpleEntry<>(sound, path);
	}

	private static Map<PacManGameSound, String> soundPaths = Map.ofEntries(//
			entry(CREDIT, "/pacman/sound/credit.wav"), //
			entry(EXTRA_LIFE, "/pacman/sound/extend.wav"), //
			entry(GAME_READY, "/pacman/sound/game_start.wav"), //
			entry(BONUS_EATEN, "/pacman/sound/eat_fruit.wav"), //
			entry(PACMAN_MUNCH, "/pacman/sound/munch_1.wav"), //
			entry(PACMAN_DEATH, "/pacman/sound/pacman_death.wav"), //
			entry(PACMAN_POWER, "/pacman/sound/power_pellet.wav"), //
			entry(GHOST_EATEN, "/pacman/sound/eat_ghost.wav"), //
			entry(GHOST_RETURNING, "/pacman/sound/retreating.wav"), //
			entry(GHOST_SIREN_1, "/pacman/sound/siren_1.wav"), //
			entry(GHOST_SIREN_2, "/pacman/sound/siren_2.wav"), //
			entry(GHOST_SIREN_3, "/pacman/sound/siren_3.wav"), //
			entry(GHOST_SIREN_4, "/pacman/sound/siren_4.wav"), //
			entry(INTERMISSION_1, "/pacman/sound/intermission.wav"), //
			entry(INTERMISSION_2, "/pacman/sound/intermission.wav"), //
			entry(INTERMISSION_3, "/pacman/sound/intermission.wav") //
	);

	public static final SoundManager SOUNDS = new SoundManager(soundPaths);

	public static final List<GameScene> SCENES = List.of( //
			new PacMan_IntroScene(UNSCALED_SIZE), //
			new PacMan_IntermissionScene1(UNSCALED_SIZE), //
			new PacMan_IntermissionScene2(UNSCALED_SIZE), //
			new PacMan_IntermissionScene3(UNSCALED_SIZE), //
			new PlayScene(UNSCALED_SIZE, RENDERING, SOUNDS) //
	);
}