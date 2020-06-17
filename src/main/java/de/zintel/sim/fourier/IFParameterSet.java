/**
 * 
 */
package de.zintel.sim.fourier;

import java.util.Collection;

import de.zintel.gfx.g3d.FourierPointGenerator.FourierCircle;

/**
 * @author friedo
 *
 */
public interface IFParameterSet {

	int getSpeed();

	Collection<FourierCircle> getCircles();

	int getIterations();

}
