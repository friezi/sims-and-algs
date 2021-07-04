/**
 * 
 */
package de.zintel.animator;

/**
 * @author friedo
 *
 */
public interface IAnimator {

	void reinit();
	
	void step();

	boolean finished();

}
