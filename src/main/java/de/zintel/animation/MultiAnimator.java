/**
 * 
 */
package de.zintel.animation;

import java.util.Collection;

/**
 * @author friedo
 *
 */
public class MultiAnimator implements IAnimator {

	private final Collection<IAnimator> animators;

	public MultiAnimator(Collection<IAnimator> animators) {
		this.animators = animators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.animation.IAnimator#reinit()
	 */
	@Override
	public void reinit() {
		animators.stream().forEach(IAnimator::reinit);
	}

	@Override
	public void step() {
		animators.stream().forEach(IAnimator::step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.animation.IAnimator#finished()
	 */
	@Override
	public boolean finished() {
		return animators.stream().allMatch(IAnimator::finished);
	}

}
