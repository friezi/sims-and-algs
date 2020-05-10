/**
 * 
 */
package de.zintel.gfx.g3d;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * @author Friedemann
 *
 */
public class LinearPointInterpolater3D extends APointInterpolater3Dold {

	private final Point3D p1;

	private final Point3D p2;

	private final boolean avoidEmptyMoiree;

	private final Queue<StepUnit3Dold> queue = new ArrayDeque<>(2);

	int diffX;
	int diffY;
	int diffZ;
	int sX;
	int sY;
	int sZ;
	int diff;

	int oldX = 0, oldY = 0, oldZ = 0;
	Random rand = new Random();

	int delta = 0;

	public LinearPointInterpolater3D(Point3D p1, Point3D p2, boolean avoidEmptyMoiree) {
		super();
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

		if (p2.z >= p1.z) {
			diffZ = p2.z - p1.z;
			sZ = 1;
		} else {
			diffZ = p1.z - p2.z;
			sZ = -1;
		}

		diff = Math.max(Math.max(diffX, diffY), diffZ);
	}

	/**
	 * @return
	 */
	@Override
	public boolean hasNext() {
		return (delta <= diff) || !queue.isEmpty();
	}

	/**
	 * 
	 */
	@Override
	public StepUnit3Dold next() {

		if (queue.isEmpty()) {

			int x;
			int y;
			int z;

			if (diff == 0) {

				x = p1.x;
				y = p1.y;
				z = p1.z;

			} else {

				if (diff == diffX) {
					x = p1.x + sX * delta;
					y = p1.y + sY * ((delta * diffY) / diff);
					z = p1.z + sZ * ((delta * diffZ) / diff);
				} else if (diff == diffY) {
					x = p1.x + sX * ((delta * diffX) / diff);
					y = p1.y + sY * delta;
					z = p1.z + sZ * ((delta * diffZ) / diff);
				} else {
					x = p1.x + sX * ((delta * diffX) / diff);
					y = p1.y + sY * ((delta * diffY) / diff);
					z = p1.z + sZ * delta;
				}

				if (avoidEmptyMoiree) {
					if (delta != 0) {
						// Vermeidung der Leermuster
						if ((diffX != diff && x != oldX) || (diffY != diff && y != oldY) || (diffZ != diff && z != oldZ)) {
							int rndpos = rand.nextInt(3);

							StepUnit3Dold fillUnit;
							if (rndpos == 1) {
								fillUnit = new StepUnit3Dold(new Point3D(oldX, y, z), delta - rndpos, diff);
							} else if (rndpos == 2) {
								fillUnit = new StepUnit3Dold(new Point3D(x, oldY, z), delta - rndpos, diff);
							} else {
								fillUnit = new StepUnit3Dold(new Point3D(x, y, oldZ), delta - rndpos, diff);
							}
							queue.add(fillUnit);
						}
					}
				}

				if (avoidEmptyMoiree) {
					oldX = x;
					oldY = y;
					oldZ = z;
				}
			}

			queue.add(new StepUnit3Dold(new Point3D(x, y, z), delta++, diff));

		}

		return queue.poll();

	}

	public Point3D getP1() {
		return p1;
	}

	public Point3D getP2() {
		return p2;
	}

}
