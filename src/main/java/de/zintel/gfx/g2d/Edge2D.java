/**
 * 
 */
package de.zintel.gfx.g2d;

/**
 * @author friedemann.zintel
 *
 */
public class Edge2D {

	private final Vertex2D first;

	private final Vertex2D second;

	private final double length;

	public Edge2D(Vertex2D first, Vertex2D second) {
		this.first = first;
		this.second = second;
		this.length = Vector2D.distance(first.getCurrent(), second.getCurrent());
	}

	public Vertex2D getFirst() {
		return first;
	}

	public Vertex2D getSecond() {
		return second;
	}

	public double getLength() {
		return length;
	}

}
