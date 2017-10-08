/**
 * 
 */
package de.zintel.math;

import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public interface Field<N> {

	List<Integer> getDimensions();

	N getValue(List<Integer> pos);

}
