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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.games.pacman.model.common.GameSound;

/**
 * @author Armin Reichert
 *
 */
public class MsPacManGameSounds extends AbstractGameSounds {

	private static final Logger logger = LogManager.getFormatterLogger();

	public MsPacManGameSounds() {
		//@formatter:off
		put(clips, GameSound.BONUS_EATEN,     "/mspacman/sound/Fruit.wav");
		put(clips, GameSound.CREDIT,          "/mspacman/sound/Coin Credit.wav");
		put(clips, GameSound.EXTRA_LIFE,      "/mspacman/sound/Extra Life.wav");
		put(clips, GameSound.GAME_READY,      "/mspacman/sound/Start.wav");
		put(clips, GameSound.GHOST_EATEN,     "/mspacman/sound/Ghost.wav");
		put(clips, GameSound.GHOST_RETURNING, "/mspacman/sound/Ghost Eyes.wav");
		put(clips, GameSound.INTERMISSION_1,  "/mspacman/sound/They Meet Act 1.wav");
		put(clips, GameSound.INTERMISSION_2,  "/mspacman/sound/The Chase Act 2.wav");
		put(clips, GameSound.INTERMISSION_3,  "/mspacman/sound/Junior Act 3.wav");
		put(clips, GameSound.PACMAN_MUNCH,    "/mspacman/sound/Ms. Pac Man Pill.wav");
		put(clips, GameSound.PACMAN_DEATH,    "/mspacman/sound/Died.wav");
		put(clips, GameSound.PACMAN_POWER,    "/mspacman/sound/Scared Ghost.wav");
		put(clips, GameSound.SIREN_1,         "/mspacman/sound/Ghost Noise 1.wav");
		put(clips, GameSound.SIREN_2,         "/mspacman/sound/Ghost Noise 2.wav");
		put(clips, GameSound.SIREN_3,         "/mspacman/sound/Ghost Noise 3.wav");
		put(clips, GameSound.SIREN_4,         "/mspacman/sound/Ghost Noise 4.wav");
		//@formatter:on
		logger.info("Ms. Pac-Man audio clips loaded");
	}
}
