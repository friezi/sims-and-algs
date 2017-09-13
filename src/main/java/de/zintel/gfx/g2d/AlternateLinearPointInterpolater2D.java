/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import de.zintel.math.MathUtils;

/**
 * @author Friedemann
 *
 */
public class AlternateLinearPointInterpolater2D extends APointInterpolater2D {

	private final Point p1;

	private final Point p2;

	private final boolean avoidEmptyMoiree;

	private final Queue<IterationUnit2D> queue = new ArrayDeque<>(2);

	private int diffX;
	private int diffY;
	private int diff;

	private int oldX = 0, oldY = 0;
	private Random rand = new Random();

	private int delta = 0;
	private int stepMax = 0;

	public AlternateLinearPointInterpolater2D(Point p1, Point p2, boolean avoidEmptyMoiree) {
		super(p1, p2);
		this.p1 = p1;
		this.p2 = p2;
		this.avoidEmptyMoiree = avoidEmptyMoiree;

		diffX = Math.abs(p2.x - p1.x);
		diffY = Math.abs(p2.y - p1.y);

		diff = Math.max(diffX, diffY);
		stepMax = diff + 1;
	}

	/**
	 * @return
	 */
	public boolean hasNext() {
		return (delta <= diff) || !queue.isEmpty();
	}

	/**
	 * 
	 */
	public IterationUnit2D next() {

		if (queue.isEmpty()) {

			int x;
			int y;

			final int step = delta + 1;
			x = MathUtils.interpolateLinear(p1.x, p2.x, step, stepMax);
			y = MathUtils.interpolateLinear(p1.y, p2.y, step, stepMax);

			if (avoidEmptyMoiree) {
				if (delta != 0) {
					// Vermeidung der Leermuster
					if ((diffX != diff && x != oldX) || (diffY != diff && y != oldY)) {
						int rndpos = rand.nextInt(2);

						int fillX;
						int fillY;
						if (rndpos == 1) {
							fillX = oldX;
							fillY = y;
						} else {
							fillX = x;
							fillY = oldY;
						}

						queue.add(new IterationUnit2D(new Point(fillX, fillY), step - rndpos, stepMax));
					}
				}
			}

			if (avoidEmptyMoiree) {
				oldX = x;
				oldY = y;
			}

			queue.add(new IterationUnit2D(new Point(x, y), step, stepMax));
			delta++;

		}

		return queue.poll();

	}

}
