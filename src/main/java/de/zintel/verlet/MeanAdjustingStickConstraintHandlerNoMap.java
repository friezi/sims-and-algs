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
 * Positionen der Kantenenden werden gemittelt.
 * 
 * @author friedemann.zintel
 *
 */
public class MeanAdjustingStickConstraintHandlerNoMap implements BiConsumer<Collection<VLEdge2D>, Integer> {

	/**
	 * 
	 */
	public MeanAdjustingStickConstraintHandlerNoMap() {
	}

	@Override
	public void accept(Collection<VLEdge2D> edges, Integer iteration) {
		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge, iteration);
		}
		for (final VLEdge2D edge : edges) {

			adjustToDeltas(edge.getFirst().getVertex());
			adjustToDeltas(edge.getSecond().getVertex());

		}

	}

	private void adjustToDeltas(final VLVertex2D vertex) {

		int size = vertex.getDeltas().size();
		if (size == 0) {

			return;

		} else {

			Vector2DPlain dv = new Vector2DPlain();
			for (Vector2DPlain delta : vertex.getDeltas()) {
				double fac = 1.0 / size;
				if (!Double.isFinite(fac)) {
					System.out.println("dfac is not finite!");
				}
				dv.add(Vector2DPlain.mult(fac, delta));
			}
			vertex.getCurrent().add(dv);

			vertex.clearDeltas();
		}

	}

	private void add(final VLVertex2D vertex, final Vector2DPlain vector) {
		vertex.addDelta(vector);
	}

	private void handleStickConstraints(final VLEdge2D edge, final int iteration) {

		VLVertex2D vFirst = edge.getFirst().getVertex();
		VLVertex2D vSecond = edge.getSecond().getVertex();
		final Vector2DPlain cFirst = vFirst.getCurrent();
		final Vector2DPlain cSecond = vSecond.getCurrent();
		Vector2DPlain vDistance = Vector2DPlain.substract(cFirst, cSecond);
		if (vDistance.isNullVector()) {
			vDistance = Vector2DPlain.substract(vFirst.getPrevious(), vSecond.getPrevious());
			// Problem!!! no line anymore
			// System.out.println("WARNING: Nullvector! edge: " + edge);
			// // do no adjustment to prevent NaN
			// return;
			// vDistance =
			// Vector2D.max(Vector2D.substract(edge.getFirst().getPrevious(),
			// edge.getSecond().getCurrent()),
			// Vector2D.substract(edge.getSecond().getPrevious(),
			// edge.getFirst().getCurrent()));
			// vDistance.mult(0.001);
		}

		double distance = vDistance.length();
		if (Double.isInfinite(distance)) {
			distance = Double.MAX_VALUE / 2 - 1;
		} else if (distance == 0) {
			System.out.println("distance is zero!");
			distance = 0.000001;
		}

		if (distance != edge.getPreferredLength()) {

			double diff = distance - edge.getPreferredLength();
			double fac = (diff / distance) / 2;
			if (!Double.isFinite(fac)) {
				System.out.println(
						"fac is not finite! diff: " + diff + " length: " + distance + " preferredLength: " + edge.getPreferredLength());
			}
			Vector2DPlain vSlack = Vector2DPlain.mult(fac, vDistance);

			if (!edge.getFirst().isSticky()) {
				add(vFirst, edge.getSecond().isSticky() ? Vector2DPlain.mult(-2, vSlack) : Vector2DPlain.mult(-1, vSlack));
			}
			if (!edge.getSecond().isSticky()) {
				add(vSecond, edge.getFirst().isSticky() ? Vector2DPlain.mult(2, vSlack) : vSlack);
			}
		}
	}

}
