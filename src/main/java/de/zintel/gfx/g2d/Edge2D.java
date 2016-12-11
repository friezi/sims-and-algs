/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;

/**
 * @author friedemann.zintel
 *
 */
public class Edge2D {

	public static interface EdgeColorChooser {
		Color getColor(Edge2D edge);
	}

	private Vertex2D first;

	private Vertex2D second;

	private double length;

	private Color color;

	private EdgeColorChooser colorChooser = null;

	public Edge2D(Vertex2D first, Vertex2D second) {
		this(first, second, Color.WHITE);
	}

	public Edge2D(Vertex2D first, Vertex2D second, Color color) {
		this.first = first;
		this.second = second;
		this.color = color;
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
	}

	public void setSecond(Vertex2D second) {
		this.second = second;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public Color getOrigColor() {
		return color;
	}

	public Color getColor() {

		if (colorChooser != null) {
			return colorChooser.getColor(this);
		} else {
			return color;
		}
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public EdgeColorChooser getColorChooser() {
		return colorChooser;
	}

	public void setColorChooser(EdgeColorChooser colorChooser) {
		this.colorChooser = colorChooser;
	}

}
