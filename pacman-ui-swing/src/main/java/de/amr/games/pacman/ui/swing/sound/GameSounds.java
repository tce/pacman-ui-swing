/*
MIT License

Copyright (c) 2022 Armin Reichert

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

package de.amr.games.pacman.ui.swing.sound;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 */
public class GameSounds {

	private static final Logger LOG = LogManager.getFormatterLogger();

	protected final Map<GameSound, Clip> clips = new EnumMap<>(GameSound.class);
	protected boolean muted;

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
		if (muted) {
			stopAll();
		}
	}

	private Clip createAndOpenClip(URL url) {
		try (AudioInputStream as = AudioSystem.getAudioInputStream(url)) {
			Clip clip = AudioSystem.getClip();
			clip.open(as);
			return clip;
		} catch (Exception x) {
			throw new GameSoundException("Error opening audio clip from URL '%s'", url);
		}
	}

	protected void put(Map<GameSound, Clip> map, GameSound sound, String path) {
		URL url = getClass().getResource(path);
		if (url == null) {
			throw new GameSoundException("Sound resource '%s' does not exist", path);
		}
		Clip clip = createAndOpenClip(url);
		map.put(sound, clip);
	}

	protected void startClip(Clip clip) {
		if (!muted) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	protected void loopClip(Clip clip, int repetitions) {
		if (!muted) {
			clip.loop(repetitions);
		}
	}

	protected Clip getClip(GameSound sound) {
		if (!clips.containsKey(sound)) {
			throw new GameSoundException("No clip found for '%s'", sound);
		}
		return clips.get(sound);
	}

	public boolean isPlaying(GameSound sound) {
		return getClip(sound).isRunning();
	}

	public void ensurePlaying(GameSound sound) {
		if (!isPlaying(sound)) {
			play(sound);
		}
	}

	public void play(GameSound sound) {
		startClip(getClip(sound));
	}

	public void ensureLoop(GameSound sound, int repetitions) {
		if (!isPlaying(sound)) {
			loop(sound, repetitions);
		}
	}

	public void loop(GameSound sound, int repetitions) {
		loopClip(getClip(sound), repetitions);
	}

	public void stop(GameSound sound) {
		getClip(sound).stop();
	}

	public void stopAll() {
		for (var clip : clips.values()) {
			clip.stop();
		}
	}

	// -----------------

	public void startSiren(int sirenIndex) {
		stopSirens();
		var siren = switch (sirenIndex) {
		case 0 -> GameSound.SIREN_1;
		case 1 -> GameSound.SIREN_2;
		case 2 -> GameSound.SIREN_3;
		case 3 -> GameSound.SIREN_4;
		default -> throw new GameSoundException("Illegal siren index: %d", sirenIndex);
		};
		loop(siren, Clip.LOOP_CONTINUOUSLY);
		LOG.info("Siren %s started", siren);
	}

	public Stream<GameSound> sirens() {
		return Stream.of(GameSound.SIREN_1, GameSound.SIREN_2, GameSound.SIREN_3, GameSound.SIREN_4);
	}

	public void ensureSirenStarted(int sirenIndex) {
		if (sirens().noneMatch(this::isPlaying)) {
			startSiren(sirenIndex);
		}
	}

	public void stopSirens() {
		sirens().forEach(siren -> {
			if (isPlaying(siren)) {
				getClip(siren).stop();
				LOG.info("Siren %s stopped", siren);
			}
		});
	}
}