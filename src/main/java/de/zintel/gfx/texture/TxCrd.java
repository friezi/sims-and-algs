/**
 * 
 */
package de.zintel.gfx.texture;

/**
 * Texturkoordinate.
 * 
 * (0,0): upper left; (1,1): lower right
 * 
 * @author Friedemann
 *
 */
public class TxCrd {

	public final double x; // u

	public final double y; // v

	/**
	 * @param x
	 *            0 <= x <= 1
	 * @param y
	 *            0 <= x <= 1
	 */
	public TxCrd(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

}
