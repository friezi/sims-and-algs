/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.util.function.Consumer;

import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.g2d.verlet.VLTetragon2D;

/**
 * @author friedemann.zintel
 *
 */
public class TetragonFacetInterpolatingRenderer implements Consumer<VLTetragon2D> {

	private final Consumer<VLFacet2D> facetRenderer;

	public TetragonFacetInterpolatingRenderer(Consumer<VLFacet2D> facetRenderer) {
		this.facetRenderer = facetRenderer;
	}

	@Override
	public void accept(VLTetragon2D tetragon) {

		VLFacet2D facet1 = tetragon.getFacet1();
		VLFacet2D facet2 = tetragon.getFacet2();

		facet1.setRenderer(facetRenderer);
		facet2.setRenderer(facetRenderer);

		facet1.render();
		facet2.render();

	}

}
