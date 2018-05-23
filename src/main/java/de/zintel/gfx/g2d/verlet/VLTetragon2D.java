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
public class VLTetragon2D implements IVLEdgeContainer2D {

	private final VLEdge2D edge1;

	private final VLEdge2D edge2;

	private final VLEdge2D edge3;

	private final VLEdge2D edge4;

	private final VLEdge2D diagonal;

	private final VLFacet2D facet1;

	private final VLFacet2D facet2;

	private final Consumer<VLTetragon2D> renderer;

	/**
	 * 
	 */
	public VLTetragon2D(VLVertexSkid v1, VLVertexSkid v2, VLVertexSkid v3, VLVertexSkid v4, Consumer<VLTetragon2D> renderer) {

		this.edge1 = new VLEdge2D(v1, v2, Color.WHITE, null);
		this.edge2 = new VLEdge2D(v2, v3, Color.WHITE, null);

		this.diagonal = new VLEdge2D(v3, v1, Color.WHITE, null);

		this.edge3 = new VLEdge2D(v1, v4, Color.WHITE, null);
		this.edge4 = new VLEdge2D(v4, v3, Color.WHITE, null);

		this.facet1 = new VLFacet2D(edge1, edge2, diagonal, null);
		this.facet2 = new VLFacet2D(diagonal, edge3, edge4, null);

		this.renderer = renderer;
	}

	VLTetragon2D(VLEdge2D edge1, VLEdge2D edge2, VLEdge2D edge3, VLEdge2D edge4, VLEdge2D diagonal, VLFacet2D f1, VLFacet2D f2,
			Consumer<VLTetragon2D> renderer) {
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		this.diagonal = diagonal;
		this.facet1 = f1;
		this.facet2 = f2;
		this.renderer = renderer;
	}

	@Override
	public void render() {

		if (renderer != null) {
			renderer.accept(this);
		}

		facet1.render();
		facet2.render();
	}

	@Override
	public List<VLEdge2D> getEdges() {
		return Arrays.asList(edge1, edge2, edge3, edge4);
	}

	@Override
	public VLTetragon2D dcopy() {
		return new VLTetragon2D(edge1.dcopy(), edge2.dcopy(), edge3.dcopy(), edge4.dcopy(), diagonal.dcopy(), facet1.dcopy(),
				facet2.dcopy(), renderer);
	}

	public VLTetragon2D setColor(Color color) {

		facet1.setColor(color);
		facet2.setColor(color);

		return this;
	}

	public VLFacet2D getFacet1() {
		return facet1;
	}

	public VLFacet2D getFacet2() {
		return facet2;
	}

}
