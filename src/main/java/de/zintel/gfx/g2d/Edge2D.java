/**
 * 
 */
package de.zintel.gfx.g2d;

/**
 * @author friedemann.zintel
 *
 */
public class Edge2D {

	private Vertex2D first;

	private Vertex2D second;

	private double length;

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

	public void setFirst(Vertex2D first) {
		this.first = first;
		this.length = Vector2D.distance(first.getCurrent(), second.getCurrent());
	}

	public void setSecond(Vertex2D second) {
		this.second = second;
		this.length = Vector2D.distance(first.getCurrent(), second.getCurrent());
	}

}
