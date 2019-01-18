/**
 * 
 */
package de.zintel.animation;

/**
 * @author friedo
 *
 */
public interface IAnimator {

	void reinit();
	
	void step();

	boolean finished();

}
