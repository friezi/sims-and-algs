/**
 * 
 */
package de.zintel.math.transform;

import java.util.Arrays;

import de.zintel.math.Vector3D;
import de.zintel.math.matrix.AMatrix.Order;
import de.zintel.math.matrix.DMatrix;

/**
 * @author friedemann.zintel
 *
 */
public final class RotationMatrix3DProvider {

	private RotationMatrix3DProvider() {
	}

	public static DMatrix<Vector3D> getRmX(ITrigonomFnProvider trigonomFnProvider) {

		final double sin = trigonomFnProvider.sinProvider().get();
		final double cos = trigonomFnProvider.cosProvider().get();

		return new DMatrix<>(Arrays.asList(new Vector3D(1, 0, 0), new Vector3D(0, cos, -sin), new Vector3D(0, sin, cos)), Order.ROWS);
	}

	public static DMatrix<Vector3D> getRmY(ITrigonomFnProvider trigonomFnProvider) {

		final double sin = trigonomFnProvider.sinProvider().get();
		final double cos = trigonomFnProvider.cosProvider().get();

		return new DMatrix<>(Arrays.asList(new Vector3D(cos, 0, -sin), new Vector3D(0, 1, 0), new Vector3D(sin, 0, cos)), Order.ROWS);
	}

	public static DMatrix<Vector3D> getRmZ(ITrigonomFnProvider trigonomFnProvider) {

		final double sin = trigonomFnProvider.sinProvider().get();
		final double cos = trigonomFnProvider.cosProvider().get();

		return new DMatrix<>(Arrays.asList(new Vector3D(cos, -sin, 0), new Vector3D(sin, cos, 0), new Vector3D(0, 0, 1)), Order.ROWS);
	}

}
