/**
 * 
 */
package de.zintel.gfx.g3d;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Friedemann
 *
 */
public class Component3D implements IObject3D {

	private final Collection<IObject3D> objects = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g3d.IObject3D#draw(de.zintel.gfx.g3d.Point3D,
	 * java.awt.Graphics, de.zintel.gfx.g3d.View3D)
	 */
	@Override
	public void draw(Point3D point, Graphics graphics, View3D view) {
		for (IObject3D object : objects) {
			object.draw(point, graphics, view);
		}
	}

	/**
	 * @param object
	 */
	public void add(final IObject3D object) {
		objects.add(object);
	}

	public void clear() {
		objects.clear();
	}

}
