/**
 * 
 */
package de.zintel.sim.epicycles;

import java.util.Collection;

import de.zintel.gfx.g3d.EpicyclesPointGenerator.Epicycle;

/**
 * @author friedo
 *
 */
public interface IECArgumentSet {

	int getSpeed();

	Collection<Epicycle> getCycles();

	int getIterations();

}
