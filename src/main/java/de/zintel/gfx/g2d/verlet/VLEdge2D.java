/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import de.zintel.gfx.IRenderer;
import de.zintel.math.Vector2D;

/**
 * Verlet Edge
 * 
 * @author friedemann.zintel
 *
 */
public class VLEdge2D implements IVLEdgeContainer2D {

	private VLVertex2D first;

	private VLVertex2D second;

	private double preferredLength;

	private Color color;

	private IRenderer<VLEdge2D> renderer;

	public VLEdge2D(VLVertex2D first, VLVertex2D second, IRenderer<VLEdge2D> renderer) {
		this(first, second, Color.WHITE, renderer);
	}

	public VLEdge2D(VLVertex2D first, VLVertex2D second, Color color, IRenderer<VLEdge2D> renderer) {
		this.first = first;
		this.second = second;
		this.color = color;
		this.renderer = renderer;
		this.preferredLength = Vector2D.distance(first.getCurrent(), second.getCurrent());
	}

	private VLEdge2D(VLVertex2D first, VLVertex2D second, double length, Color color, IRenderer<VLEdge2D> renderer) {
		this.first = first;
		this.second = second;
		this.preferredLength = length;
		this.color = color;
		this.renderer = renderer;
	}

	public VLVertex2D getFirst() {
		return first;
	}

	public VLVertex2D getSecond() {
		return second;
	}

	public double getPreferredLength() {
		return preferredLength;
	}

	public void setFirst(VLVertex2D first) {
		this.first = first;
	}

	public void setSecond(VLVertex2D second) {
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
			renderer.render(this);
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

	public IRenderer<VLEdge2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(IRenderer<VLEdge2D> renderer) {
		this.renderer = renderer;
	}

}
