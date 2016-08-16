/**
 * 
 */
package de.zintel.gfx.g3d;

import java.awt.Point;

/**
 * @author Friedemann
 *
 */
public class Projector2D {

	private final Point3D viewpoint;

	private final int height;

	public Projector2D(Point3D viewpoint, int height) {
		super();
		this.viewpoint = viewpoint;
		this.height = height;
	}

	public Point project(final Point3D point) {

		int diffZ = viewpoint.z - point.z;
		if (diffZ == 0) {
			return new Point(0, 0);
		}
		return new Point((point.x * viewpoint.z - point.z * viewpoint.x)
				/ diffZ, translateY((point.y * viewpoint.z - point.z
				* viewpoint.y)
				/ diffZ));

	}

	private int translateY(final int y) {
		return height - y;
	}

	public Point3D getViewpoint() {
		return viewpoint;
	}

	public int getHeight() {
		return height;
	}

}
