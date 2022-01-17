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
package de.amr.games.pacman.ui.swing.scenes.mspacman;

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
import de.amr.games.pacman.ui.PacManGameUI;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.mspacman.Rendering2D_MsPacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;

/**
 * Ms. Pac-Man game scenes.
 * 
 * @author Armin Reichert
 */
public class ScenesMsPacMan {

	public static final Dimension UNSCALED_SIZE = new Dimension(t(28), t(36));
	public static final Rendering2D_MsPacMan RENDERING = new Rendering2D_MsPacMan();
	public static SoundManager SOUNDS;
	public static List<GameScene> SCENES;

	private static Entry<PacManGameSound, String> entry(PacManGameSound sound, String path) {
		return new SimpleEntry<>(sound, "/mspacman/sound/" + path);
	}

	public static void init(PacManGameUI ui) {
		Map<PacManGameSound, String> soundPaths = Map.ofEntries(//
				entry(CREDIT, "Coin Credit.wav"), //
				entry(EXTRA_LIFE, "Extra Life.wav"), //
				entry(GAME_READY, "Start.wav"), //
				entry(BONUS_EATEN, "Fruit.wav"), //
				entry(PACMAN_MUNCH, "Ms. Pac Man Pill.wav"), //
				entry(PACMAN_DEATH, "Died.wav"), //
				entry(PACMAN_POWER, "Scared Ghost.wav"), //
				entry(GHOST_EATEN, "Ghost.wav"), //
				entry(GHOST_RETURNING, "Ghost Eyes.wav"), //
				entry(GHOST_SIREN_1, "Ghost Noise 1.wav"), //
				entry(GHOST_SIREN_2, "Ghost Noise 2.wav"), //
				entry(GHOST_SIREN_3, "Ghost Noise 3.wav"), //
				entry(GHOST_SIREN_4, "Ghost Noise 4.wav"), //
				entry(INTERMISSION_1, "They Meet Act 1.wav"), //
				entry(INTERMISSION_2, "The Chase Act 2.wav"), //
				entry(INTERMISSION_3, "Junior Act 3.wav") //
		);
		SOUNDS = new SoundManager(soundPaths);

		SCENES = List.of(new MsPacMan_IntroScene(ui, UNSCALED_SIZE), //
				new MsPacMan_IntermissionScene1(ui, UNSCALED_SIZE), //
				new MsPacMan_IntermissionScene2(ui, UNSCALED_SIZE), //
				new MsPacMan_IntermissionScene3(ui, UNSCALED_SIZE), //
				new PlayScene(ui, UNSCALED_SIZE, RENDERING, SOUNDS)//
		);
	}
}