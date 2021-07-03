/**
 * 
 */
package de.zintel.gfx.g3d;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import de.zintel.math.Vector3D;

/**
 * @author Friedemann
 *
 */
public class LinearPointInterpolater3D extends APointInterpolater3D {

	private final boolean avoidEmptyMoiree;

	private final Queue<StepUnit3D> queue = new ArrayDeque<>(2);

	double diffX;
	double diffY;
	double diffZ;
	int sX;
	int sY;
	int sZ;
	double diff;

	double oldX = 0, oldY = 0, oldZ = 0;
	Random rand = new Random();

	int delta = 0;

	public LinearPointInterpolater3D(Vector3D start, Vector3D end, boolean avoidEmptyMoiree) {
		super(start, end);
		this.avoidEmptyMoiree = avoidEmptyMoiree;

		final Vector3D p1 = getStart();
		final Vector3D p2 = getEnd();

		if (p2.x() > p1.x()) {
			diffX = p2.x() - p1.x();
			sX = 1;
		} else {
			diffX = p1.x() - p2.x();
			sX = -1;
		}

		if (p2.y() >= p1.y()) {
			diffY = p2.y() - p1.y();
			sY = 1;
		} else {
			diffY = p1.y() - p2.y();
			sY = -1;
		}

		if (p2.z() >= p1.z()) {
			diffZ = p2.z() - p1.z();
			sZ = 1;
		} else {
			diffZ = p1.z() - p2.z();
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
	public StepUnit3D next() {

		final Vector3D p1 = getStart();

		if (queue.isEmpty()) {

			double x;
			double y;
			double z;

			if (diff == 0) {

				x = p1.x();
				y = p1.y();
				z = p1.z();

			} else {

				if (diff == diffX) {
					x = p1.x() + sX * delta;
					y = p1.y() + sY * ((delta * diffY) / diff);
					z = p1.z() + sZ * ((delta * diffZ) / diff);
				} else if (diff == diffY) {
					x = p1.x() + sX * ((delta * diffX) / diff);
					y = p1.y() + sY * delta;
					z = p1.z() + sZ * ((delta * diffZ) / diff);
				} else {
					x = p1.x() + sX * ((delta * diffX) / diff);
					y = p1.y() + sY * ((delta * diffY) / diff);
					z = p1.z() + sZ * delta;
				}

				if (avoidEmptyMoiree) {
					if (delta != 0) {
						// Vermeidung der Leermuster
						if ((diffX != diff && x != oldX) || (diffY != diff && y != oldY) || (diffZ != diff && z != oldZ)) {
							int rndpos = rand.nextInt(3);

							StepUnit3D fillUnit;
							if (rndpos == 1) {
								fillUnit = new StepUnit3D(new Vector3D(oldX, y, z), delta - rndpos, (int) diff);
							} else if (rndpos == 2) {
								fillUnit = new StepUnit3D(new Vector3D(x, oldY, z), delta - rndpos, (int) diff);
							} else {
								fillUnit = new StepUnit3D(new Vector3D(x, y, oldZ), delta - rndpos, (int) diff);
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

			queue.add(new StepUnit3D(new Vector3D(x, y, z), delta++, (int) diff));

		}

		return queue.poll();

	}

}
