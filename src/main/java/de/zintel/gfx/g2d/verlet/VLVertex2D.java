/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.ArrayList;
import java.util.Collection;

import de.zintel.math.Vector2DPlain;

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

	private Vector2DPlain current;

	private Vector2DPlain previous;

	private Collection<Vector2DPlain> deltas = new ArrayList<>();

	/**
	 * 
	 */
	public VLVertex2D(Vector2DPlain current) {
		this(current, current);
	}

	/**
	 * 
	 */
	public VLVertex2D(Vector2DPlain current, Vector2DPlain previous) {
		this.current = current;
		this.previous = previous;
	}

	public Vector2DPlain getCurrent() {
		return current;
	}

	public void setCurrent(Vector2DPlain current) {
		this.current = current;
	}

	public Vector2DPlain getPrevious() {
		return previous;
	}

	public void setPrevious(Vector2DPlain previous) {
		this.previous = previous;
	}

	@Override
	public String toString() {
		return "Vertex2D [current=" + current + ", previous=" + previous + "]";
	}

	public VLVertex2D dcopy() {
		return new VLVertex2D(new Vector2DPlain(current), new Vector2DPlain(previous));
	}

	public VLVertex2D addDelta(final Vector2DPlain delta) {
		deltas.add(delta);
		return this;
	}

	public VLVertex2D clearDeltas() {
		deltas.clear();
		return this;
	}

	public Collection<Vector2DPlain> getDeltas() {
		return deltas;
	}

}
