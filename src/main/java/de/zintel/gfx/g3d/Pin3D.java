/**
 * 
 */
package de.zintel.gfx.g3d;

import de.zintel.gfx.texture.TxCrd;

/**
 * Verbindet Punkt und Texturkoordinate.
 * 
 * @author Friedemann
 *
 */
public class Pin3D {

	public final Point3D point;

	public final TxCrd txCrd;

	public Pin3D(Point3D point, TxCrd txCrd) {
		super();
		this.point = point;
		this.txCrd = txCrd;
	}

}
