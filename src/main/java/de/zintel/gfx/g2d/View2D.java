/**
 * 
 */
package de.zintel.gfx.g2d;

/**
 * @author Friedemann
 *
 */
public class View2D {

	private final int height;
	private final int xnull;
	private final int ynull;
	
	public View2D(int height, int xnull, int ynull) {
		super();
		this.height = height;
		this.xnull = xnull;
		this.ynull = ynull;
	}

	public int translateX(final int x) {
		return xnull + x;
	}

	public int translateY(final int y) {
		return (height - ynull) - y;
	}

	public int getHeight() {
		return height;
	}

	public int getXnull() {
		return xnull;
	}

	public int getYnull() {
		return ynull;
	}

}
