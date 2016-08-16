/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * @author Friedemann
 *
 */
public class LinearPointInterpolater2D extends APointInterpolater2D {

	private final Point p1;

	private final Point p2;

	private final boolean avoidEmptyMoiree;

	private final Queue<IterationUnit2D> queue = new ArrayDeque<>(2);

	private int diffX;
	private int diffY;
	private int sX;
	private int sY;
	private int diff;

	private int oldX = 0, oldY = 0;
	private Random rand = new Random();

	private int delta = 0;

	public LinearPointInterpolater2D(Point p1, Point p2, boolean avoidEmptyMoiree) {
		super(p1, p2);
		this.p1 = p1;
		this.p2 = p2;
		this.avoidEmptyMoiree = avoidEmptyMoiree;

		if (p2.x > p1.x) {
			diffX = p2.x - p1.x;
			sX = 1;
		} else {
			diffX = p1.x - p2.x;
			sX = -1;
		}

		if (p2.y >= p1.y) {
			diffY = p2.y - p1.y;
			sY = 1;
		} else {
			diffY = p1.y - p2.y;
			sY = -1;
		}

		diff = Math.max(diffX, diffY);
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

			if (diff == 0) {
				x = p1.x;
				y = p1.y;
			} else {

				if (diff == diffX) {
					x = p1.x + sX * delta;
					y = p1.y + sY * ((delta * diffY) / diff);
				} else {
					x = p1.x + sX * ((delta * diffX) / diff);
					y = p1.y + sY * delta;
				}

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

							queue.add(new IterationUnit2D(new Point(fillX, fillY), delta - rndpos, diff));
						}
					}
				}

				if (avoidEmptyMoiree) {
					oldX = x;
					oldY = y;
				}
			}

			queue.add(new IterationUnit2D(new Point(x, y), delta++, diff));

		}

		return queue.poll();

	}

}
