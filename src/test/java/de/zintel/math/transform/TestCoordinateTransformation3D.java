/**
 * 
 */
package de.zintel.math.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.zintel.math.Vector3D;

/**
 * @author friedemann.zintel
 *
 */
public class TestCoordinateTransformation3D {

	private static final double EPSILON = 0.00000001;

	/**
	 * 
	 */
	public TestCoordinateTransformation3D() {
	}

	@Test
	public void testTransformPoint() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).translate(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.transformPoint(new Vector3D(2, 1, 1));

		assertEquals(-4.0, transformedPoint.x(), "x");
		assertEquals(19.0, transformedPoint.y(), "y");
		assertEquals(-29.0, transformedPoint.z(), "z");

	}

	@Test
	public void testInverseTransformPoint() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).translate(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.inverseTransformPoint(transformation.transformPoint(new Vector3D(2, 1, 1)));

		assertEquals(2.0, transformedPoint.x(), "x");
		assertEquals(1.0, transformedPoint.y(), "y");
		assertEquals(1.0, transformedPoint.z(), "z");

	}

	@Test
	public void testTransformVector() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).translate(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.transformVector(new Vector3D(2, 3, 4));

		assertEquals(1.0, transformedPoint.x(), "x");
		assertEquals(-3.0, transformedPoint.y(), "y");
		assertEquals(4.0, transformedPoint.z(), "z");

	}

	@Test
	public void testInverseTransformVector() throws Exception {

		CoordinateTransformation3D transformation = new CoordinateTransformation3D();
		transformation.setScaling(new Vector3D(2, -1, 1)).translate(new Vector3D(10, 20, 30));

		Vector3D transformedPoint = transformation.inverseTransformVector(transformation.transformVector(new Vector3D(2, 3, 4)));

		assertEquals(2.0, transformedPoint.x(), "x");
		assertEquals(3.0, transformedPoint.y(), "y");
		assertEquals(4.0, transformedPoint.z(), "z");

	}
	//
	// @Test
	// public void testInverseTransformVectorWithTranslation() throws Exception
	// {
	//
	// CoordinateTransformation3DNew transformation = new
	// CoordinateTransformation3DNew();
	// transformation.setScaling(new Vector3D(2, -1, 1)).translate(new
	// Vector3D(10, 20, 30)).setRotation(1, 2, 1.5)
	// .translateRotation(new Vector3D(1, 2, 3));
	//
	// Vector3D transformedPoint =
	// transformation.inverseTransformVector(transformation.transformVector(new
	// Vector3D(2, 3, 4)));
	//
	// assertTrue(Math.abs(2.0 - transformedPoint.x()) < EPSILON, "x");
	// assertTrue(Math.abs(3.0 - transformedPoint.y()) < EPSILON, "y");
	// assertTrue(Math.abs(4.0 - transformedPoint.z()) < EPSILON, "z");
	//
	// }
}
