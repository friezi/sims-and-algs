/**
 * 
 */
package de.zintel.math.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.zintel.math.Vector3D;
import de.zintel.math.transform.Rotator3D;

/**
 * @author friedemann.zintel
 *
 */
public class TestRotator3D {

	private static final double EPSILON = 0.00000001;

	/**
	 * 
	 */
	public TestRotator3D() {
	}

	@Test
	public void testRotateZero() throws Exception {

		Rotator3D rotator = new Rotator3D(0, 0, 0);
		final double x = 7;
		final double y = 19;
		final double z = 31;
		Vector3D rotated = rotator.apply(new Vector3D(x, y, z));
		assertEquals(x, rotated.x(), "x");
		assertEquals(y, rotated.y(), "y");
		assertEquals(z, rotated.z(), "z");

	}

	@Test
	public void testRotateFull() throws Exception {

		Rotator3D rotator = new Rotator3D(2 * Math.PI, 2 * Math.PI, 2 * Math.PI);
		final double x = 7;
		final double y = 19;
		final double z = 31;
		Vector3D rotated = rotator.apply(new Vector3D(x, y, z));
		assertTrue(Math.abs(x - rotated.x()) < EPSILON, "x");
		assertTrue(Math.abs(y - rotated.y()) < EPSILON, "y");
		assertTrue(Math.abs(z - rotated.z()) < EPSILON, "z");
	}

	@Test
	public void testRotate90X() throws Exception {

		Rotator3D rotator = new Rotator3D(Math.PI / 2, 0, 0);
		final double x = 7;
		final double y = 19;
		final double z = 31;
		Vector3D rotated = rotator.apply(new Vector3D(x, y, z));
		assertTrue(Math.abs(x - rotated.x()) < EPSILON, "x");
		assertTrue(Math.abs(-z - rotated.y()) < EPSILON, "y");
		assertTrue(Math.abs(y - rotated.z()) < EPSILON, "z");
	}

	@Test
	public void testRotate90Y() throws Exception {

		Rotator3D rotator = new Rotator3D(0, Math.PI / 2, 0);
		final double x = 7;
		final double y = 19;
		final double z = 31;
		Vector3D rotated = rotator.apply(new Vector3D(x, y, z));
		assertTrue(Math.abs(-z - rotated.x()) < EPSILON, "x");
		assertTrue(Math.abs(y - rotated.y()) < EPSILON, "y");
		assertTrue(Math.abs(x - rotated.z()) < EPSILON, "z");
	}

	@Test
	public void testRotate90Z() throws Exception {

		Rotator3D rotator = new Rotator3D(0, 0, Math.PI / 2);
		final double x = 7;
		final double y = 19;
		final double z = 31;
		Vector3D rotated = rotator.apply(new Vector3D(x, y, z));
		assertTrue(Math.abs(-y - rotated.x()) < EPSILON, "x");
		assertTrue(Math.abs(x - rotated.y()) < EPSILON, "y");
		assertTrue(Math.abs(z - rotated.z()) < EPSILON, "z");
	}

}
