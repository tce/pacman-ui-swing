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

import static de.amr.games.pacman.model.world.World.t;
import static de.amr.games.pacman.ui.GameSounds.BONUS_EATEN;
import static de.amr.games.pacman.ui.GameSounds.CREDIT;
import static de.amr.games.pacman.ui.GameSounds.EXTRA_LIFE;
import static de.amr.games.pacman.ui.GameSounds.GAME_READY;
import static de.amr.games.pacman.ui.GameSounds.GHOST_EATEN;
import static de.amr.games.pacman.ui.GameSounds.GHOST_RETURNING;
import static de.amr.games.pacman.ui.GameSounds.SIREN_1;
import static de.amr.games.pacman.ui.GameSounds.SIREN_2;
import static de.amr.games.pacman.ui.GameSounds.SIREN_3;
import static de.amr.games.pacman.ui.GameSounds.SIREN_4;
import static de.amr.games.pacman.ui.GameSounds.INTERMISSION_1;
import static de.amr.games.pacman.ui.GameSounds.INTERMISSION_2;
import static de.amr.games.pacman.ui.GameSounds.INTERMISSION_3;
import static de.amr.games.pacman.ui.GameSounds.PACMAN_DEATH;
import static de.amr.games.pacman.ui.GameSounds.PACMAN_MUNCH;
import static de.amr.games.pacman.ui.GameSounds.PACMAN_POWER;

import java.awt.Dimension;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.amr.games.pacman.ui.GameSounds;
import de.amr.games.pacman.ui.swing.assets.SoundManager;
import de.amr.games.pacman.ui.swing.rendering.pacman.Rendering2D_PacMan;
import de.amr.games.pacman.ui.swing.scenes.common.GameScene;
import de.amr.games.pacman.ui.swing.scenes.common.PlayScene;
import de.amr.games.pacman.ui.swing.shell.PacManGameUI_Swing;

/**
 * Pac-Man game scenes.
 * 
 * @author Armin Reichert
 */
public class ScenesPacMan {

	public static final Dimension UNSCALED_SIZE = new Dimension(t(28), t(36));
	public static Rendering2D_PacMan RENDERING = new Rendering2D_PacMan();
	public static SoundManager SOUNDS;
	public static List<GameScene> SCENES;

	private static Entry<GameSounds, String> entry(GameSounds sound, String path) {
		return new SimpleEntry<>(sound, "/pacman/sound/" + path);
	}

	public static void init(PacManGameUI_Swing ui) {
		Map<GameSounds, String> soundPaths = Map.ofEntries(//
				entry(CREDIT, "credit.wav"), //
				entry(EXTRA_LIFE, "extend.wav"), //
				entry(GAME_READY, "game_start.wav"), //
				entry(BONUS_EATEN, "eat_fruit.wav"), //
				entry(PACMAN_MUNCH, "munch_1.wav"), //
				entry(PACMAN_DEATH, "pacman_death.wav"), //
				entry(PACMAN_POWER, "power_pellet.wav"), //
				entry(GHOST_EATEN, "eat_ghost.wav"), //
				entry(GHOST_RETURNING, "retreating.wav"), //
				entry(SIREN_1, "siren_1.wav"), //
				entry(SIREN_2, "siren_2.wav"), //
				entry(SIREN_3, "siren_3.wav"), //
				entry(SIREN_4, "siren_4.wav"), //
				entry(INTERMISSION_1, "intermission.wav"), //
				entry(INTERMISSION_2, "intermission.wav"), //
				entry(INTERMISSION_3, "intermission.wav") //
		);
		SOUNDS = new SoundManager(soundPaths);

		SCENES = List.of( //
				new PacMan_IntroScene(ui, UNSCALED_SIZE), //
				new PacMan_IntermissionScene1(ui, UNSCALED_SIZE), //
				new PacMan_IntermissionScene2(ui, UNSCALED_SIZE), //
				new PacMan_IntermissionScene3(ui, UNSCALED_SIZE), //
				new PlayScene(ui, UNSCALED_SIZE, RENDERING, SOUNDS) //
		);
	}
}