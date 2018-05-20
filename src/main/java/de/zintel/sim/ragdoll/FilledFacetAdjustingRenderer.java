/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.awt.Point;
import java.util.function.Consumer;

import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;

/**
 * @author friedemann.zintel
 *
 */
public class FilledFacetAdjustingRenderer implements Consumer<VLFacet2D> {

	private final IGraphicsSubsystem graphicsSubsystem;

	public FilledFacetAdjustingRenderer(IGraphicsSubsystem graphicsSubsystem) {
		this.graphicsSubsystem = graphicsSubsystem;
	}

	@Override
	public void accept(VLFacet2D facet) {

		Point pivot = facet.getVertex1().getCurrent().toPoint();

		final LinearPointInterpolater2D baselineInterpolater = new LinearPointInterpolater2D(facet.getVertex2().getCurrent().toPoint(),
				facet.getVertex3().getCurrent().toPoint(), false);
		for (IterationUnit2D baseunit : baselineInterpolater) {

			final LinearPointInterpolater2D lineInterpolater = new LinearPointInterpolater2D(pivot, baseunit.getPoint(), true);
			for (IterationUnit2D unit : lineInterpolater) {

				Point point = unit.getPoint();
				graphicsSubsystem.drawPoint(point.x, point.y, Color.BLUE);
			}
		}
	}
//	
//	private Color calculateColor(final Point point,final VLFacet2D facet) {
//		
//		
//		
//	}
//	
//	private double distance(final Point point,final VLEdge2D edge) {
//		
//	}

}
