/**
 * 
 */
package de.zintel.verlet;

import java.util.Collection;
import java.util.function.BiConsumer;

import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.math.Vector2DPlain;

/**
 * @author friedemann.zintel
 *
 */
public class DfltStickConstraintHandler implements BiConsumer<Collection<VLEdge2D>, Integer> {

	/**
	 * 
	 */
	public DfltStickConstraintHandler() {
	}

	@Override
	public void accept(Collection<VLEdge2D> edges, Integer iteration) {

		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge);
		}

	}

	private void handleStickConstraints(final VLEdge2D edge) {

		VLVertex2D vFirst = edge.getFirst().getVertex();
		VLVertex2D vSecond = edge.getSecond().getVertex();
		final Vector2DPlain cFirst = vFirst.getCurrent();
		final Vector2DPlain cSecond = vSecond.getCurrent();
		Vector2DPlain dV = Vector2DPlain.substract(cFirst, cSecond);
		if (dV.isNullVector()) {
			dV = Vector2DPlain.substract(vFirst.getPrevious(), vSecond.getPrevious());
			// Problem!!! no line anymore
			// System.out.println("WARNING: Nullvector! edge: " + edge);
			// // do no adjustment to prevent NaN
			// return;
			// dV =
			// Vector2D.max(Vector2D.substract(edge.getFirst().getPrevious(),
			// edge.getSecond().getCurrent()),
			// Vector2D.substract(edge.getSecond().getPrevious(),
			// edge.getFirst().getCurrent()));
			// dV.mult(0.001);
		}

		double length = dV.length();
		if (Double.isInfinite(length)) {
			length = Double.MAX_VALUE / 2 - 1;
		}
		
		if (length != edge.getPreferredLength()) {

			double diff = length - edge.getPreferredLength();
			Vector2DPlain slackV = Vector2DPlain.mult((diff / length) / 2, dV);

			if (!edge.getFirst().isSticky()) {

				if (edge.getSecond().isSticky()) {
					cFirst.substract(Vector2DPlain.mult(2, slackV));
				} else {
					cFirst.substract(slackV);
				}
			}
			if (!edge.getSecond().isSticky()) {

				if (edge.getFirst().isSticky()) {
					cSecond.add(Vector2DPlain.mult(2, slackV));
				} else {
					cSecond.add(slackV);
				}
			}
		}
	}

}
