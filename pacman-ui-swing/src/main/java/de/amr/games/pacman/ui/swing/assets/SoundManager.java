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

import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import de.amr.games.pacman.ui.GameSounds;

/**
 * Sound manager for Pac-Man game variants.
 * 
 * TODO how to avoid warning about potential resource leak?
 * 
 * @author Armin Reichert
 */
public class SoundManager {

	private final Map<GameSounds, URL> urlMap;
	private final Map<GameSounds, Clip> clipCache = new EnumMap<>(GameSounds.class);
	private Clip munch0, munch1;
	private int munchIndex;
	private boolean muted;

	public SoundManager(Map<GameSounds, String> pathMap) {
		urlMap = new HashMap<>();
		for (var entry : pathMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		munchIndex = 0;
		munch0 = createAndOpenClip(urlMap.get(GameSounds.PACMAN_MUNCH));
		munch1 = createAndOpenClip(urlMap.get(GameSounds.PACMAN_MUNCH));
	}

	private void put(GameSounds sound, String path) {
		URL url = getClass().getResource(path);
		if (url == null) {
			throw new RuntimeException("Sound resource not found: " + path);
		}
		urlMap.put(sound, url);
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

	private Clip getClip(GameSounds sound) {
		Clip clip = null;
		if (sound == GameSounds.PACMAN_MUNCH) {
			clip = munchIndex == 0 ? munch0 : munch1;
			munchIndex = (munchIndex + 1) % 2;
		} else if (clipCache.containsKey(sound)) {
			clip = clipCache.get(sound);
		} else {
			clip = createAndOpenClip(urlMap.get(sound));
			clipCache.put(sound, clip);
		}
		clip.setFramePosition(0);
		return clip;
	}

	public void play(GameSounds sound) {
		if (!muted) {
			getClip(sound).start();
		}
	}

	public void loop(GameSounds sound, int repetitions) {
		if (!muted) {
			Clip clip = getClip(sound);
			clip.setFramePosition(0);
			clip.loop(repetitions);
		}
	}

	public void stop(GameSounds sound) {
		getClip(sound).stop();
	}

	public void stopAll() {
		for (Clip clip : clipCache.values()) {
			clip.stop();
		}
		clipCache.clear();
		munch0.stop();
		munch1.stop();
	}
}