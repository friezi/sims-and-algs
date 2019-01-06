/**
 * 
 */
package de.zintel.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author friedemann.zintel
 *
 */
public class TestVector3D {

	/**
	 * 
	 */
	public TestVector3D() {
	}

	@Test
	public void testVector3D() throws Exception {

		Vector3D vector = new Vector3D(1, 2, 3);
		assertEquals(3, vector.getDim(), "dim");
		assertEquals(1D, vector.x(), "x");
		assertEquals(2D, vector.y(), "y");
		assertEquals(3D, vector.z(), "z");

	}

}
