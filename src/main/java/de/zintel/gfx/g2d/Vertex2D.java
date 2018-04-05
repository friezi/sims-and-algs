/**
 * 
 */
package de.zintel.gfx.g2d;

import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class Vertex2D {

	/**
	 * current and previous purposing verlet-integration.
	 * 
	 */

	private Vector2D current;

	private Vector2D previous;

	private boolean pinned = false;

	/**
	 * 
	 */
	public Vertex2D(Vector2D current) {
		this(current, current);
	}

	/**
	 * 
	 */
	public Vertex2D(Vector2D current, Vector2D previous) {
		this.current = current;
		this.previous = previous;
	}

	public Vector2D getCurrent() {
		return current;
	}

	public void setCurrent(Vector2D current) {
		this.current = current;
	}

	public Vector2D getPrevious() {
		return previous;
	}

	public void setPrevious(Vector2D previous) {
		this.previous = previous;
	}

	public boolean isPinned() {
		return pinned;
	}

	public Vertex2D setPinned(boolean pinned) {
		this.pinned = pinned;
		return this;
	}

	@Override
	public String toString() {
		return "Vertex2D [current=" + current + ", previous=" + previous + ", pinned=" + pinned + "]";
	}

	public Vertex2D dcopy() {
		return new Vertex2D(new Vector2D(current), new Vector2D(previous)).setPinned(isPinned());
	}

}
