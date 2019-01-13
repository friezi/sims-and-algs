/**
 * 
 */
package de.zintel.verlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
public class MeanAdjustingStickConstraintHandler implements BiConsumer<Collection<VLEdge2D>, Integer> {

	/**
	 * 
	 */
	public MeanAdjustingStickConstraintHandler() {
	}

	@Override
	public void accept(Collection<VLEdge2D> edges, Integer iteration) {

		Map<VLVertex2D, Collection<Vector2DPlain>> modMap = new HashMap<>();
		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge, modMap, iteration);
		}

		for (Map.Entry<VLVertex2D, Collection<Vector2DPlain>> entry : modMap.entrySet()) {

			Vector2DPlain dv = new Vector2DPlain();
			int size = entry.getValue().size();
			for (Vector2DPlain v : entry.getValue()) {
				dv.add(Vector2DPlain.mult(1.0 / size, v));
			}
			entry.getKey().getCurrent().add(dv);
		}

	}

	private void add(final VLVertex2D vertex, final Vector2DPlain vector, final Map<VLVertex2D, Collection<Vector2DPlain>> map) {

		Collection<Vector2DPlain> collection = map.get(vertex);
		if (collection == null) {

			collection = new ArrayList<>();
			map.put(vertex, collection);

		}

		collection.add(vector);

	}

	private void handleStickConstraints(final VLEdge2D edge, Map<VLVertex2D, Collection<Vector2DPlain>> modMap, final int iteration) {

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

				Vector2DPlain dVector;
				if (edge.getSecond().isSticky()) {
					dVector = Vector2DPlain.mult(-2, slackV);
				} else {
					dVector = Vector2DPlain.mult(-1, slackV);
				}
				add(vFirst, dVector, modMap);
			}
			if (!edge.getSecond().isSticky()) {

				Vector2DPlain dVector;
				if (edge.getFirst().isSticky()) {
					dVector = Vector2DPlain.mult(2, slackV);
				} else {
					dVector = slackV;
				}
				add(vSecond, dVector, modMap);
			}
		}
	}

}
