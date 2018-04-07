/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class Edge2D implements IEdgeContainer2D {

	private Vertex2D first;

	private Vertex2D second;

	private double preferredLength;

	private Color color;

	private IRenderer<Edge2D> renderer;

	public Edge2D(Vertex2D first, Vertex2D second, IRenderer<Edge2D> renderer) {
		this(first, second, Color.WHITE, renderer);
	}

	public Edge2D(Vertex2D first, Vertex2D second, Color color, IRenderer<Edge2D> renderer) {
		this.first = first;
		this.second = second;
		this.color = color;
		this.renderer = renderer;
		this.preferredLength = Vector2D.distance(first.getCurrent(), second.getCurrent());
	}

	private Edge2D(Vertex2D first, Vertex2D second, double length, Color color, IRenderer<Edge2D> renderer) {
		this.first = first;
		this.second = second;
		this.preferredLength = length;
		this.color = color;
		this.renderer = renderer;
	}

	public Vertex2D getFirst() {
		return first;
	}

	public Vertex2D getSecond() {
		return second;
	}

	public double getPreferredLength() {
		return preferredLength;
	}

	public void setFirst(Vertex2D first) {
		this.first = first;
	}

	public void setSecond(Vertex2D second) {
		this.second = second;
	}

	public void setPreferredLength(double length) {
		this.preferredLength = length;
	}

	public Color getColor() {
		return color;
	}

	public Edge2D setColor(Color color) {
		this.color = color;
		return this;
	}

	@Override
	public String toString() {
		return "Edge2D [first=" + first + ", second=" + second + ", length=" + preferredLength + "]";
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

	@Override
	public Edge2D dcopy() {
		return new Edge2D(first.dcopy(), second.dcopy(), preferredLength, color, renderer);
	}

	public IRenderer<Edge2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(IRenderer<Edge2D> renderer) {
		this.renderer = renderer;
	}

}
