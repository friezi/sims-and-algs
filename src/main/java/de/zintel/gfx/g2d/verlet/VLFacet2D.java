/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author friedemann.zintel
 *
 */
public class VLFacet2D implements IVLPolygon2D {

	private final VLEdge2D edge1;

	private final VLEdge2D edge2;

	private final VLEdge2D edge3;

	private final VLVertex2D vertex1;

	private final VLVertex2D vertex2;

	private final VLVertex2D vertex3;

	private  Consumer<VLFacet2D> renderer;

	/**
	 * 
	 */
	public VLFacet2D(final VLVertex2D v1, final VLVertex2D v2, final VLVertex2D v3, Consumer<VLFacet2D> renderer) {
		this(new VLEdge2D(v1, v2, null), new VLEdge2D(v2, v3, null), new VLEdge2D(v3, v1, null), renderer);
	}

	/**
	 * 
	 */
	VLFacet2D(final VLEdge2D e1, final VLEdge2D e2, final VLEdge2D e3, Consumer<VLFacet2D> renderer) {

		this.edge1 = e1;
		this.edge2 = e2;
		this.edge3 = e3;
		this.vertex1 = e1.getFirst();
		this.vertex2 = e2.getFirst();
		this.vertex3 = e3.getFirst();
		this.renderer = renderer;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IRenderable#render()
	 */
	@Override
	public void render() {

		if (renderer != null) {
			renderer.accept(this);
		}

	}

	public VLFacet2D setColor(Color color) {
		for (VLEdge2D edge : getEdges()) {
			edge.setColor(color);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IEdgeContainer2D#getEdges()
	 */
	@Override
	public List<VLEdge2D> getEdges() {
		return Arrays.asList(edge1, edge2, edge3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IEdgeContainer2D#dcopy()
	 */
	@Override
	public VLFacet2D dcopy() {
		return new VLFacet2D(edge1.dcopy(), edge2.dcopy(), edge3.dcopy(), renderer).setColor(edge1.getColor());
	}

	public VLEdge2D getEdge1() {
		return edge1;
	}

	public VLEdge2D getEdge2() {
		return edge2;
	}

	public VLEdge2D getEdge3() {
		return edge3;
	}

	public VLVertex2D getVertex1() {
		return vertex1;
	}

	public VLVertex2D getVertex2() {
		return vertex2;
	}

	public VLVertex2D getVertex3() {
		return vertex3;
	}

	public Consumer<VLFacet2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(Consumer<VLFacet2D> renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<VLVertex2D> getVertices() {
		return Arrays.asList(vertex1, vertex2, vertex3);
	}

}
