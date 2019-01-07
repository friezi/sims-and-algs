/**
 * 
 */
package de.zintel.math.transform;

import java.util.Arrays;
import java.util.function.Function;

import de.zintel.math.Matrix;
import de.zintel.math.Matrix.Order;
import de.zintel.math.Vector3D;
import de.zintel.math.VectorND;

/**
 * rotation happens always in positive direction from top to bottom, i. e.
 * x->y->z. It starts at positive top element and rotations in direction of
 * positive bottom-element. So, f. i. to rotate around the y-axis, you start at
 * x-axis in positive direction and rotate in positive z-direction.
 * 
 * @author friedemann.zintel
 *
 */
public class Rotator3D implements Function<Vector3D, Vector3D> {

	private final double angleX;

	private final double angleY;

	private final double angleZ;

	private final Matrix rotationMatrix;

	public Rotator3D(double angleX, double angleY, double angleZ) {
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;

		rotationMatrix = makeRotationMatrix();

	}

	private Rotator3D(Matrix rotationMatrix, double angleX, double angleY, double angleZ) {

		this.rotationMatrix = rotationMatrix;

		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;

	}

	private Matrix makeRotationMatrix() {

		final double sinX = Math.sin(angleX);
		final double sinY = Math.sin(angleY);
		final double sinZ = Math.sin(angleZ);
		final double cosX = Math.cos(angleX);
		final double cosY = Math.cos(angleY);
		final double cosZ = Math.cos(angleZ);

		return new Matrix(Arrays.asList(new Vector3D(cosY * cosZ, -cosY * sinZ, -sinY),
				new Vector3D(cosX * sinZ - sinX * sinY * cosZ, sinX * sinY * sinZ + cosX * cosZ, -sinX * cosY),
				new Vector3D(cosX * sinY * cosZ + sinX * sinZ, sinX * cosZ - cosX * sinY * sinZ, cosX * cosY)), Order.ROWS);

	}

	@Override
	public Vector3D apply(final Vector3D vector) {
		return new Vector3D(VectorND.mmult(rotationMatrix, vector));
	}

	public double getAngleX() {
		return angleX;
	}

	public double getAngleY() {
		return angleY;
	}

	public double getAngleZ() {
		return angleZ;
	}

	public Rotator3D getInvertedRotator() {
		return new Rotator3D(rotationMatrix.transpose(), -angleX, -angleY, -angleZ);
	}

}
