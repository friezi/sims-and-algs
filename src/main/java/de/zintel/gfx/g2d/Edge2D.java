/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class Edge2D implements IEdgeContainer2D {

	private Vertex2D first;

	private Vertex2D second;

	private double length;

	private Color color;

	private IRenderer<Edge2D> renderer;

	private ColorModifier<Edge2D> colorModifier = null;

	public Edge2D(Vertex2D first, Vertex2D second, IRenderer<Edge2D> renderer) {
		this(first, second, Color.WHITE, renderer);
	}

	public Edge2D(Vertex2D first, Vertex2D second, Color color, IRenderer<Edge2D> renderer) {
		this.first = first;
		this.second = second;
		this.color = color;
		this.renderer = renderer;
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

		if (colorModifier != null) {
			return colorModifier.getColor(this);
		} else {
			return color;
		}
	}

	public Edge2D setColor(Color color) {
		this.color = color;
		return this;
	}

	public ColorModifier<Edge2D> getColorModifier() {
		return colorModifier;
	}

	public Edge2D setColorModifier(ColorModifier<Edge2D> colorModifier) {
		this.colorModifier = colorModifier;
		return this;
	}

	@Override
	public String toString() {
		return "Edge2D [first=" + first + ", second=" + second + ", length=" + length + "]";
	}

	@Override
	public void render() {
		if (renderer != null) {
			renderer.render(this);
		}
	}

	@Override
	public List<Edge2D> getEdges() {
		return Arrays.asList(this);
	}

}
