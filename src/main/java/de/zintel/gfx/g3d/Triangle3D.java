/**
 * 
 */
package de.zintel.gfx.g3d;

import de.zintel.gfx.texture.ITexture;

/**
 * @author Friedemann
 *
 */
public class Triangle3D extends Tetragon3D {

	/**
	 * @param pin1
	 * @param pin2
	 * @param pin3
	 * @param p22
	 * @param texture
	 */
	public Triangle3D(Pin3D pin1, Pin3D pin2, Pin3D pin3, ITexture texture) {
		super(pin1, pin2, pin3, pin2, texture);
	}

}
