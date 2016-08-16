/**
 * 
 */
package de.zintel.gfx.g3d;

/**
 * @author Friedemann
 *
 */
public class View3D {

	private final Point3D nullpoint;
	private final Projector2D projector;

	public View3D(Point3D nullpoint, Projector2D projector) {
		this.nullpoint = nullpoint;
		this.projector = projector;
	}

	public Projector2D getProjector() {
		return projector;
	}

	public Point3D getNullpoint() {
		return nullpoint;
	}

}
