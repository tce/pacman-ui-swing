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
package de.amr.games.pacman.ui.swing.rendering.common;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import de.amr.games.pacman.lib.Direction;
import de.amr.games.pacman.lib.animation.SimpleThingAnimation;
import de.amr.games.pacman.lib.animation.ThingAnimation;
import de.amr.games.pacman.lib.animation.ThingAnimationCollection;
import de.amr.games.pacman.lib.animation.ThingAnimationMap;
import de.amr.games.pacman.lib.animation.ThingList;
import de.amr.games.pacman.model.common.actors.Ghost;
import de.amr.games.pacman.model.common.actors.GhostAnimationKey;

/**
 * @author Armin Reichert
 */
public class GhostAnimations extends ThingAnimationCollection<Ghost, GhostAnimationKey, BufferedImage> {

	private ThingAnimationMap<Direction, BufferedImage> eyes;
	private SimpleThingAnimation<BufferedImage> flashing;
	private SimpleThingAnimation<BufferedImage> blue;
	private ThingAnimationMap<Direction, BufferedImage> color;
	private ThingList<BufferedImage> values;

	public GhostAnimations(int ghostID, Rendering2D r2D) {
		eyes = r2D.createGhostEyesAnimation();
		flashing = r2D.createGhostFlashingAnimation();
		blue = r2D.createGhostBlueAnimation();
		color = r2D.createGhostColorAnimation(ghostID);
		values = r2D.createGhostValueList();
		select(GhostAnimationKey.ANIM_COLOR);
	}

	public void startFlashing(int numFlashes, long ticksTotal) {
		long frameTicks = ticksTotal / (numFlashes * flashing.numFrames());
		flashing.frameDuration(frameTicks);
		flashing.repeat(numFlashes);
		flashing.restart();
	}

	@Override
	public ThingAnimation<BufferedImage> byKey(GhostAnimationKey key) {
		return switch (key) {
		case ANIM_EYES -> eyes;
		case ANIM_FLASHING -> flashing;
		case ANIM_BLUE -> blue;
		case ANIM_COLOR -> color;
		case ANIM_VALUE -> values;
		};
	}

	@Override
	public Stream<ThingAnimation<BufferedImage>> all() {
		return Stream.of(eyes, flashing, blue, color, values);
	}

	@Override
	public BufferedImage current(Ghost ghost) {
		return switch (selectedKey) {
		case ANIM_EYES -> eyes.get(ghost.wishDir()).frame();
		case ANIM_FLASHING -> flashing.animate();
		case ANIM_BLUE -> blue.animate();
		case ANIM_COLOR -> {
			var sprite = color.get(ghost.wishDir()).frame();
			if (ghost.velocity.length() > 0) {
				color.get(ghost.wishDir()).advance();
			}
			yield sprite;
		}
		case ANIM_VALUE -> ghost.killIndex >= 0 ? values.frame(ghost.killIndex) : null;
		};
	}
}