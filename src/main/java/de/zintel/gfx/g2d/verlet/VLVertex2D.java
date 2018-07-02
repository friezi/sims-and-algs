/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.ArrayList;
import java.util.Collection;

import de.zintel.math.Vector2D;

/**
 * Verlet Vertex
 * 
 * @author friedemann.zintel
 *
 */
public class VLVertex2D {

	/**
	 * current and previous purposing verlet-integration.
	 * 
	 */

	private Vector2D current;

	private Vector2D previous;

	private Collection<Vector2D> deltas = new ArrayList<>();

	/**
	 * 
	 */
	public VLVertex2D(Vector2D current) {
		this(current, current);
	}

	/**
	 * 
	 */
	public VLVertex2D(Vector2D current, Vector2D previous) {
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

	@Override
	public String toString() {
		return "Vertex2D [current=" + current + ", previous=" + previous + "]";
	}

	public VLVertex2D dcopy() {
		return new VLVertex2D(new Vector2D(current), new Vector2D(previous));
	}

	public VLVertex2D addDelta(final Vector2D delta) {
		deltas.add(delta);
		return this;
	}

	public VLVertex2D clearDeltas() {
		deltas.clear();
		return this;
	}

	public Collection<Vector2D> getDeltas() {
		return deltas;
	}

}
