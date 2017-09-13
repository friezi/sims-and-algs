/**
 * 
 */
package de.zintel.test;

import java.awt.Point;

import de.zintel.gfx.g2d.AlternateLinearPointInterpolater2D;
import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.math.MathUtils;

/**
 * @author Friedemann
 *
 */
public class TestLIP {

	public static void main(String[] args) {

		Point p1 = new Point(1, 10);
		Point p2 = new Point(15, 3);

		LinearPointInterpolater2D i1 = new LinearPointInterpolater2D(p1, p2, true);
		AlternateLinearPointInterpolater2D i2 = new AlternateLinearPointInterpolater2D(p1, p2, true);

		int i = 1;
		while (i1.hasNext() || i2.hasNext()) {

			IterationUnit2D s1 = null;
			IterationUnit2D s2 = null;
			if (i1.hasNext()) {
				s1 = i1.next();
			}
			if (i2.hasNext()) {
				s2 = i2.next();
			}

			System.out.println("i=" + i++ + ": s1:" + s1 + " s2:" + s2);

		}

		System.out.println(MathUtils.interpolateLinear(1, 15, 14, 14));

	}

}
