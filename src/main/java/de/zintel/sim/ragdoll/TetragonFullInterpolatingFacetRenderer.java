package de.zintel.sim.ragdoll;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.g2d.verlet.VLTetragon2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;

public class TetragonFullInterpolatingFacetRenderer implements Consumer<VLTetragon2D> {

	private final Consumer<VLFacet2D> facetRenderer;

	public TetragonFullInterpolatingFacetRenderer(Consumer<VLFacet2D> facetRenderer) {
		this.facetRenderer = facetRenderer;
	}

	@Override
	public void accept(VLTetragon2D tetragon) {

		new TetragonEdgesFacetWrapper(tetragon, tetragon.getFacet1(), facetRenderer).render();
		new TetragonEdgesFacetWrapper(tetragon, tetragon.getFacet2(), facetRenderer).render();

	}

	private static class TetragonEdgesFacetWrapper extends VLFacet2D {

		private final VLTetragon2D tetragon;

		private final VLFacet2D facet;

		private final Consumer<VLFacet2D> facetRenderer;

		public TetragonEdgesFacetWrapper(VLTetragon2D tetragon, VLFacet2D facet, Consumer<VLFacet2D> facetRenderer) {
			super(facet.getVertex1(), facet.getVertex2(), facet.getVertex3(), null);
			this.tetragon = tetragon;
			this.facet = facet;
			this.facetRenderer = facetRenderer;
		}

		@Override
		public void render() {
			if (facetRenderer != null) {
				facetRenderer.accept(this);
			}
		}

		@Override
		public List<VLEdge2D> getEdges() {
			return tetragon.getEdges();
		}

		@Override
		public Collection<VLVertex2D> getVertices() {
			return facet.getVertices();
		}

	}

}
