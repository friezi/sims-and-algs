/**
 * 
 */
package de.zintel.math.transform;

import java.util.Arrays;
import java.util.Collections;

import de.zintel.math.Vector3D;
import de.zintel.math.matrix.DMatrix;
import de.zintel.math.matrix.AMatrix.Order;

/**
 * @author friedemann.zintel
 *
 */
public class Utils3D {

	public static final DMatrix<Vector3D> IDENTITY_MATRIX = new DMatrix<>(
			Collections.unmodifiableList(Arrays.asList(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0), new Vector3D(0, 0, 1))), Order.ROWS);

	/**
	 * 
	 */
	private Utils3D() {
	}

}
