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
public class Line3D implements IObject3D {

	private final Point3D p1;

	private final Point3D p2;

	public Line3D(Point3D p1, Point3D p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g3d.IObject3D#draw(de.zintel.gfx.g3d.Point3D,
	 * java.awt.Graphics, de.zintel.gfx.g3d.View3D)
	 */
	@Override
	public void draw(Point3D point, Graphics graphics, View3D view) {

		Point sp1 = view.getProjector().project(p1.add(view.getNullpoint()));
		Point sp2 = view.getProjector().project(p2.add(view.getNullpoint()));

		graphics.drawLine(sp1.x, sp1.y, sp2.x, sp2.y);

	}

}
