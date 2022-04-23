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
package de.amr.games.pacman.ui.swing.assets;

import static de.amr.games.pacman.lib.Logging.log;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import de.amr.games.pacman.model.common.GameVariant;

/**
 * Sound manager for Pac-Man game variants.
 * 
 * TODO how to avoid warning about potential resource leak?
 * 
 * @author Armin Reichert
 */
public class SoundManager {

	private static SoundManager it = new SoundManager();

	public static SoundManager get() {
		return it;
	}

	private Map<GameSound, Clip> sm_PacMan = new EnumMap<>(GameSound.class);
	private Map<GameSound, Clip> sm_MsPacMan = new EnumMap<>(GameSound.class);
	private Map<GameSound, Clip> sm;
	private boolean muted;

	public SoundManager() {
		//@formatter:off
		put(sm_MsPacMan, GameSound.CREDIT,          "/mspacman/sound/Coin Credit.wav");
		put(sm_MsPacMan, GameSound.EXTRA_LIFE,      "/mspacman/sound/Extra Life.wav");
		put(sm_MsPacMan, GameSound.GAME_READY,      "/mspacman/sound/Start.wav");
		put(sm_MsPacMan, GameSound.BONUS_EATEN,     "/mspacman/sound/Fruit.wav");
		put(sm_MsPacMan, GameSound.PACMAN_MUNCH,    "/mspacman/sound/Ms. Pac Man Pill.wav");
		put(sm_MsPacMan, GameSound.PACMAN_DEATH,    "/mspacman/sound/Died.wav");
		put(sm_MsPacMan, GameSound.PACMAN_POWER,    "/mspacman/sound/Scared Ghost.wav");
		put(sm_MsPacMan, GameSound.GHOST_EATEN,     "/mspacman/sound/Ghost.wav");
		put(sm_MsPacMan, GameSound.GHOST_RETURNING, "/mspacman/sound/Ghost Eyes.wav");
		put(sm_MsPacMan, GameSound.SIREN_1,         "/mspacman/sound/Ghost Noise 1.wav");
		put(sm_MsPacMan, GameSound.SIREN_2,         "/mspacman/sound/Ghost Noise 2.wav");
		put(sm_MsPacMan, GameSound.SIREN_3,         "/mspacman/sound/Ghost Noise 3.wav");
		put(sm_MsPacMan, GameSound.SIREN_4,         "/mspacman/sound/Ghost Noise 4.wav");
		put(sm_MsPacMan, GameSound.INTERMISSION_1,  "/mspacman/sound/They Meet Act 1.wav");
		put(sm_MsPacMan, GameSound.INTERMISSION_2,  "/mspacman/sound/The Chase Act 2.wav");
		put(sm_MsPacMan, GameSound.INTERMISSION_3,  "/mspacman/sound/Junior Act 3.wav");
		//@formatter:on
		log("Ms. Pac-Man sounds loaded");

		//@formatter:off
		put(sm_PacMan, GameSound.CREDIT,          "/pacman/sound/credit.wav");
		put(sm_PacMan, GameSound.EXTRA_LIFE,      "/pacman/sound/extend.wav");
		put(sm_PacMan, GameSound.GAME_READY,      "/pacman/sound/game_start.wav");
		put(sm_PacMan, GameSound.BONUS_EATEN,     "/pacman/sound/eat_fruit.wav");
		put(sm_PacMan, GameSound.PACMAN_MUNCH,    "/pacman/sound/munch_1.wav");
		put(sm_PacMan, GameSound.PACMAN_DEATH,    "/pacman/sound/pacman_death.wav");
		put(sm_PacMan, GameSound.PACMAN_POWER,    "/pacman/sound/power_pellet.wav");
		put(sm_PacMan, GameSound.GHOST_EATEN,     "/pacman/sound/eat_ghost.wav");
		put(sm_PacMan, GameSound.GHOST_RETURNING, "/pacman/sound/retreating.wav");
		put(sm_PacMan, GameSound.SIREN_1,         "/pacman/sound/siren_1.wav");
		put(sm_PacMan, GameSound.SIREN_2,         "/pacman/sound/siren_2.wav");
		put(sm_PacMan, GameSound.SIREN_3,         "/pacman/sound/siren_3.wav");
		put(sm_PacMan, GameSound.SIREN_4,         "/pacman/sound/siren_4.wav");
		put(sm_PacMan, GameSound.INTERMISSION_1,  "/pacman/sound/intermission.wav");
		put(sm_PacMan, GameSound.INTERMISSION_2,  "/pacman/sound/intermission.wav");
		put(sm_PacMan, GameSound.INTERMISSION_3,  "/pacman/sound/intermission.wav");
		//@formatter:on
		log("Pac-Man sounds loaded");
	}

	public void selectGameVariant(GameVariant variant) {
		sm = switch (variant) {
		case MS_PACMAN -> sm_MsPacMan;
		case PACMAN -> sm_PacMan;
		default -> throw new IllegalArgumentException();
		};
	}

	private void put(Map<GameSound, Clip> map, GameSound sound, String path) {
		URL url = getClass().getResource(path);
		if (url != null) {
			map.put(sound, createAndOpenClip(url));
		} else {
			throw new RuntimeException("Sound resource does not exist: " + path);
		}
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	private Clip createAndOpenClip(URL url) {
		try (AudioInputStream as = AudioSystem.getAudioInputStream(url)) {
			Clip clip = AudioSystem.getClip();
			clip.open(as);
			return clip;
		} catch (Exception x) {
			throw new RuntimeException("Error opening audio clip from URL " + url, x);
		}
	}

	private Clip getClip(GameSound sound) {
		return sm.get(sound);
	}

	public void play(GameSound sound) {
		if (!muted) {
			Clip clip = getClip(sound);
			clip.stop();
			clip.setFramePosition(0);
			getClip(sound).start();
		}
	}

	public void loop(GameSound sound, int repetitions) {
		if (!muted) {
			Clip clip = getClip(sound);
			clip.stop();
			clip.setFramePosition(0);
			clip.loop(repetitions);
		}
	}

	public void stop(GameSound sound) {
		getClip(sound).stop();
	}

	public void stopAll() {
		for (Clip clip : sm.values()) {
			clip.stop();
		}
	}

	public Stream<GameSound> sirens() {
		return Stream.of(GameSound.SIREN_1, GameSound.SIREN_2, GameSound.SIREN_3, GameSound.SIREN_4);
	}

	public void startSiren(int scatterPhase) {
		var siren = switch (scatterPhase) {
		case 0 -> GameSound.SIREN_1;
		case 1 -> GameSound.SIREN_2;
		case 2 -> GameSound.SIREN_3;
		case 3 -> GameSound.SIREN_4;
		default -> throw new IllegalArgumentException();
		};
		loop(siren, Clip.LOOP_CONTINUOUSLY);
		log("Siren %s started", siren);
	}

	public void stopSirens() {
		sirens().map(this::getClip).forEach(Clip::stop);
		log("All sirens stopped");
	}

	public boolean isAnySirenPlaying() {
		return sirens().map(this::getClip).anyMatch(Clip::isRunning);
	}
}