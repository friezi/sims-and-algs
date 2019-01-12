/**
 * 
 */
package de.zintel.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author friedemann.zintel
 *
 */
public class TestMath {

	private static final List<List<Double>> coords = Arrays.asList(Arrays.asList(0.0, 0.0, 0.0), Arrays.asList(1.0, 0.0, 0.0),
			Arrays.asList(1.0, 1.0, 0.7853981633974483), Arrays.asList(0.0, 1.0, 1.5707963267948966),
			Arrays.asList(-1.0, 1.0, 2.356194490192345), Arrays.asList(-1.0, 0.0, 3.141592653589793),
			Arrays.asList(-1.0, -1.0, -2.356194490192345), Arrays.asList(0.0, -1.0, -1.5707963267948966),
			Arrays.asList(1.5, 1.0, 0.5880026035475675), Arrays.asList(1.0, 1.5, 0.982793723247329),
			Arrays.asList(-1.5, 1.0, 2.5535900500422257), Arrays.asList(-1.0, 1.5, 2.1587989303424644),
			Arrays.asList(-1.5, -1.0, -2.5535900500422257), Arrays.asList(-1.0, -1.5, -2.1587989303424644),
			Arrays.asList(1.5, -1.0, -0.5880026035475675), Arrays.asList(1.0, -1.5, -0.982793723247329));

	/**
	 * 
	 */
	public TestMath() {
	}

	@Test
	public void testToPolar2D() throws Exception {

		for (List<Double> coord : coords) {

			final double x = coord.get(0);
			final double y = coord.get(1);

			Vector2D v2d = new Vector2D(x, y);
			final Polar2D p2d = v2d.toPolar();

			assertEquals(p2d.getAngle(), coord.get(2).doubleValue(), "v2d: " + v2d);

		}
	}

	@Test
	public void testToPolarND() throws Exception {

		for (List<Double> coord : coords) {

			final double x = coord.get(0);
			final double y = coord.get(1);

			Vector2D v2d = new Vector2D(x, y);
			VectorND vnd = v2d.toND();

			final Polar2D p2d = v2d.toPolar();
			final PolarND pnd = vnd.toPolar();

			assertEquals(p2d.getRadius(), pnd.getRadius(), "v2d: " + v2d + "  vnd: " + vnd);
			assertEquals(p2d.getAngle(), pnd.getAngles().get(0).doubleValue(), "v2d: " + v2d + "  vnd: " + vnd);

		}
	}

	@Test
	public void testToCartesian2D() throws Exception {

		for (List<Double> coord : coords) {

			final double x = coord.get(0);
			final double y = coord.get(1);

			Vector2D v2d = new Vector2D(x, y);

			final Polar2D p2d = v2d.toPolar();

			final Vector2D c2d = p2d.toCartesian();

			assertTrue(isRelativelyEqual(coord.get(0).doubleValue(), c2d.x), "v2d: " + v2d);
			assertTrue(isRelativelyEqual(coord.get(1).doubleValue(), c2d.y), "v2d: " + v2d);

		}
	}

	@Test
	public void testToCartesianND() throws Exception {

		for (List<Double> coord : coords) {

			final double x = coord.get(0);
			final double y = coord.get(1);

			Vector2D v2d = new Vector2D(x, y);
			VectorND vnd = v2d.toND();

			final PolarND pnd = vnd.toPolar();

			final VectorND cnd = pnd.toCartesian();

			assertTrue(isRelativelyEqual(coord.get(0).doubleValue(), cnd.get(0)), "vnd: " + vnd);
			assertTrue(isRelativelyEqual(coord.get(1).doubleValue(), cnd.get(1)), "vnd: " + vnd);

		}
	}

	private boolean isRelativelyEqual(double x1, double x2) {
		return Math.max(x1, x2) - Math.min(x1, x2) < 0.00001;
	}

	@Test
	public void testTrig() throws Exception {

		double value=3/4.0;
		System.out.println("x: " + Math.acos(value) + "  -x: " + Math.acos(-value));

	}

}
