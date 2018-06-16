package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.g2d.verlet.VLTetragon2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;

public class TetragonFullInterpolatingFacetRenderer implements Consumer<VLTetragon2D> {

	final PolygonInterpolatingRenderer<VLFacet2D> facetRenderer;

	final TetragonEdgesProvider edgesProvider = new TetragonEdgesProvider();

	public TetragonFullInterpolatingFacetRenderer(IGraphicsSubsystem graphicsSubsystem, Function<VLEdge2D, Color> colorProvider) {
		facetRenderer = new PolygonInterpolatingRenderer<VLFacet2D>(graphicsSubsystem, colorProvider)
				.setContextEdgesProvider(edgesProvider);
	}

	private static class TetragonEdgesProvider implements Function<VLFacet2D, List<VLEdge2D>> {

		private VLTetragon2D tetragon;

		@Override
		public List<VLEdge2D> apply(VLFacet2D t) {
			return getTetragon() != null ? tetragon.getEdges() : Collections.emptyList();
		}

		public VLTetragon2D getTetragon() {
			return tetragon;
		}

		public void setTetragon(VLTetragon2D tetragon) {
			this.tetragon = tetragon;
		}

	}

	@Override
	public void accept(VLTetragon2D tetragon) {

		edgesProvider.setTetragon(tetragon);
		tetragon.getFacet1().setRenderer(facetRenderer);
		tetragon.getFacet2().setRenderer(facetRenderer);

		tetragon.getFacet1().render();
		tetragon.getFacet2().render();

	}

}
