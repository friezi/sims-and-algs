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
public class Facet2D implements IEdgeContainer2D {

	private final Edge2D edge1;

	private final Edge2D edge2;

	private final Edge2D edge3;

	private final IRenderer<Facet2D> renderer;

	/**
	 * 
	 */
	public Facet2D(final Vertex2D v1, final Vertex2D v2, final Vertex2D v3, IRenderer<Edge2D> edgeRenderer, IRenderer<Facet2D> renderer) {

		this.edge1 = new Edge2D(v1, v2, edgeRenderer);
		this.edge2 = new Edge2D(v2, v3, edgeRenderer);
		this.edge3 = new Edge2D(v3, v1, edgeRenderer);
		this.renderer = renderer;

	}

	/**
	 * 
	 */
	private Facet2D(final Edge2D e1, final Edge2D e2, final Edge2D e3, IRenderer<Facet2D> renderer) {

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
			renderer.render(this);
		}

	}

	public Facet2D setColor(Color color) {
		for (Edge2D edge : getEdges()) {
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
	public List<Edge2D> getEdges() {
		return Arrays.asList(edge1, edge2, edge3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IEdgeContainer2D#dcopy()
	 */
	@Override
	public IEdgeContainer2D dcopy() {
		return new Facet2D(edge1.dcopy(), edge2.dcopy(), edge3.dcopy(), renderer).setColor(edge1.getColor());
	}

}
