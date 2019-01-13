/**
 * 
 */
package de.zintel.math.transform;

import java.util.Arrays;
import java.util.function.Function;

import de.zintel.math.AMatrix.Order;
import de.zintel.math.matrix.DMatrix;
import de.zintel.math.Vector3D;
import de.zintel.math.AVectorND;

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

	private double angleX;

	private double angleY;

	private double angleZ;

	private DMatrix<Vector3D> rotationMatrix;

	public Rotator3D(double angleX, double angleY, double angleZ) {
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;

		rotationMatrix = makeRotationMatrix();

	}

	private Rotator3D(DMatrix<Vector3D> rotationMatrix, double angleX, double angleY, double angleZ) {

		this.rotationMatrix = rotationMatrix;

		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;

	}

	private DMatrix<Vector3D> makeRotationMatrix() {

		final double sinX = Math.sin(angleX);
		final double sinY = Math.sin(angleY);
		final double sinZ = Math.sin(angleZ);
		final double cosX = Math.cos(angleX);
		final double cosY = Math.cos(angleY);
		final double cosZ = Math.cos(angleZ);

		final DMatrix<Vector3D> matrix = new DMatrix<>(Arrays.asList(new Vector3D(cosY * cosZ, -cosY * sinZ, -sinY),
				new Vector3D(cosX * sinZ - sinX * sinY * cosZ, sinX * sinY * sinZ + cosX * cosZ, -sinX * cosY),
				new Vector3D(cosX * sinY * cosZ + sinX * sinZ, sinX * cosZ - cosX * sinY * sinZ, cosX * cosY)), Order.ROWS);
		return matrix;

	}

	public void add(double angleX, double angleY, double angleZ) {

		this.angleX += angleX;
		this.angleY += angleY;
		this.angleZ += angleZ;

		rotationMatrix = makeRotationMatrix();

	}

	@Override
	public Vector3D apply(final Vector3D vector) {
		return AVectorND.mmult(rotationMatrix, vector);
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
