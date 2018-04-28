/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author friedemann.zintel
 *
 */
public class VLFacet2D implements IVLEdgeContainer2D {

	private final VLEdge2D edge1;

	private final VLEdge2D edge2;

	private final VLEdge2D edge3;

	private final Consumer<VLFacet2D> renderer;

	/**
	 * 
	 */
	public VLFacet2D(final VLVertex2D v1, final VLVertex2D v2, final VLVertex2D v3, Consumer<VLEdge2D> edgeRenderer, Consumer<VLFacet2D> renderer) {

		this.edge1 = new VLEdge2D(v1, v2, edgeRenderer);
		this.edge2 = new VLEdge2D(v2, v3, edgeRenderer);
		this.edge3 = new VLEdge2D(v3, v1, edgeRenderer);
		this.renderer = renderer;

	}

	/**
	 * 
	 */
	private VLFacet2D(final VLEdge2D e1, final VLEdge2D e2, final VLEdge2D e3, Consumer<VLFacet2D> renderer) {

		this.edge1 = e1;
		this.edge2 = e2;
		this.edge3 = e3;
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
	public IVLEdgeContainer2D dcopy() {
		return new VLFacet2D(edge1.dcopy(), edge2.dcopy(), edge3.dcopy(), renderer).setColor(edge1.getColor());
	}

}
