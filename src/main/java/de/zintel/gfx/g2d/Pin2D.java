/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Point;

import de.zintel.gfx.texture.TxCrd;

/**
 * Verbindet Punkt und Texturkoordinate.
 * 
 * @author Friedemann
 *
 */
public class Pin2D {

	public final Point point;

	public final TxCrd txCrd;

	public Pin2D(Point point, TxCrd txCrd) {
		super();
		this.point = point;
		this.txCrd = txCrd;
	}

}
