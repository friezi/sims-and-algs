/**
 * 
 */
package de.zintel.gfx.g3d;

import java.awt.Graphics;
import java.awt.Point;

/**
 * @author Friedemann
 *
 */
public class Point3D implements IObject3D {

	public int x;

	public int y;

	public int z;

	public Point3D(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3D add(final Point3D point) {
		return new Point3D(x + point.x, y + point.y, z + point.z);
	}

	public void draw(Graphics graphics, final View3D view) {
		draw(null, graphics, view);
	}

	@Override
	public void draw(final Point3D point, Graphics graphics, final View3D view) {

		Point3D shiftedPoint = add(view.getNullpoint());
		Point planePoint = view.getProjector().project(
				point == null ? shiftedPoint : shiftedPoint.add(point));

		graphics.drawLine(planePoint.x, planePoint.y, planePoint.x,
				planePoint.y);

	}

}
