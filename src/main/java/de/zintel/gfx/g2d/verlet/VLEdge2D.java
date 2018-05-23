/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.math.Vector2D;

/**
 * Verlet Edge
 * 
 * @author friedemann.zintel
 *
 */
public class VLEdge2D implements IVLEdgeContainer2D {

	private VLVertexSkid first;

	private VLVertexSkid second;

	private double preferredLength;

	private Color color;

	private Consumer<VLEdge2D> renderer;

	public VLEdge2D(VLVertex2D first, VLVertex2D second, Color color, Consumer<VLEdge2D> renderer) {
		this(new VLVertexSkid(first), new VLVertexSkid(second), color, renderer);
	}

	public VLEdge2D(VLVertexSkid first, VLVertexSkid second, Color color, Consumer<VLEdge2D> renderer) {
		this.first = first;
		this.second = second;
		this.color = color;
		this.renderer = renderer;
		this.preferredLength = Vector2D.distance(first.getVertex().getCurrent(), second.getVertex().getCurrent());
	}

	private VLEdge2D(VLVertexSkid first, VLVertexSkid second, double length, Color color, Consumer<VLEdge2D> renderer) {
		this.first = first;
		this.second = second;
		this.preferredLength = length;
		this.color = color;
		this.renderer = renderer;
	}

	public VLVertexSkid getFirst() {
		return first;
	}

	public VLVertexSkid getSecond() {
		return second;
	}

	public double getPreferredLength() {
		return preferredLength;
	}

	public void setFirst(VLVertexSkid first) {
		this.first = first;
	}

	public void setSecond(VLVertexSkid second) {
		this.second = second;
	}

	public void setPreferredLength(double length) {
		this.preferredLength = length;
	}

	public Color getColor() {
		return color;
	}

	public VLEdge2D setColor(Color color) {
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
			renderer.accept(this);
		}
	}

	@Override
	public List<VLEdge2D> getEdges() {
		return Arrays.asList(this);
	}

	@Override
	public VLEdge2D dcopy() {
		return new VLEdge2D(first.dcopy(), second.dcopy(), preferredLength, color, renderer);
	}

	public Consumer<VLEdge2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(Consumer<VLEdge2D> renderer) {
		this.renderer = renderer;
	}

	public Vector2D currentToVector2D() {
		return Vector2D.substract(second.getVertex().getCurrent(), first.getVertex().getCurrent());
	}

}
