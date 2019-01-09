/**
 * 
 */
package de.zintel.math.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;

/**
 * @author friedemann.zintel
 *
 */
public class TestCoordinateTransformation3D {

	/**
	 * 
	 */
	public TestCoordinateTransformation3D() {
	}

	@Test
	public void testTransformPoint() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).setTranslation(new Vector3D(10, 20, 30)).setRotation(0, 0, 0);

		Vector3D transformedPoint = transformation.transformPoint(new Vector3D(2, 1, 1));

		assertEquals(-4.0, transformedPoint.x(), "x");
		assertEquals(19.0, transformedPoint.y(), "y");
		assertEquals(-29.0, transformedPoint.z(), "z");

	}

	@Test
	public void testInverseTransformPoint() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).setTranslation(new Vector3D(10, 20, 30)).setRotation(0, 0, 0);

		Vector3D transformedPoint = transformation.inverseTransformPoint(transformation.transformPoint(new Vector3D(2, 1, 1)));

		assertEquals(2.0, transformedPoint.x(), "x");
		assertEquals(1.0, transformedPoint.y(), "y");
		assertEquals(1.0, transformedPoint.z(), "z");

	}

	@Test
	public void testTransformVector() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).setTranslation(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.transformVector(new Vector3D(2, 3, 4));

		assertEquals(1.0, transformedPoint.x(), "x");
		assertEquals(-3.0, transformedPoint.y(), "y");
		assertEquals(4.0, transformedPoint.z(), "z");

	}

	@Test
	public void testInverseTransformVector() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).setTranslation(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.inverseTransformVector(transformation.transformVector(new Vector3D(2, 3, 4)));

		assertEquals(2.0, transformedPoint.x(), "x");
		assertEquals(3.0, transformedPoint.y(), "y");
		assertEquals(4.0, transformedPoint.z(), "z");

	}

}
