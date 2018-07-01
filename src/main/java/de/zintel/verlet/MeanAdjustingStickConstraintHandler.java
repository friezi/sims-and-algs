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
import de.zintel.math.Vector2D;

/**
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

		Map<VLVertex2D, Collection<Vector2D>> modMap = new HashMap<>();
		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge, modMap, iteration);
		}
		
		for (Map.Entry<VLVertex2D, Collection<Vector2D>> entry : modMap.entrySet()) {

			Vector2D dv = new Vector2D();
			for (Vector2D v : entry.getValue()) {
				dv.add(v);
			}
			dv.mult(1.0 / entry.getValue().size());
			entry.getKey().getCurrent().add(dv);
		}

	}

	private void add(final VLVertex2D vertex, final Vector2D vector, final Map<VLVertex2D, Collection<Vector2D>> map) {

		Collection<Vector2D> collection = map.get(vertex);
		if (collection == null) {

			collection = new ArrayList<>();
			map.put(vertex, collection);

		}

		collection.add(vector);

	}

	private void handleStickConstraints(final VLEdge2D edge, Map<VLVertex2D, Collection<Vector2D>> modMap, final int iteration) {

		VLVertex2D vFirst = edge.getFirst().getVertex();
		VLVertex2D vSecond = edge.getSecond().getVertex();
		final Vector2D cFirst = vFirst.getCurrent();
		final Vector2D cSecond = vSecond.getCurrent();
		Vector2D dV = Vector2D.substract(cFirst, cSecond);
		if (dV.isNullVector()) {
			dV = Vector2D.substract(vFirst.getPrevious(), vSecond.getPrevious());
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
			Vector2D slackV = Vector2D.mult((diff / length) / 2, dV);

			if (!edge.getFirst().isSticky()) {

				Vector2D dVector;
				if (edge.getSecond().isSticky()) {
					dVector = Vector2D.mult(-2, slackV);
				} else {
					dVector = Vector2D.mult(-1, slackV);
				}
				add(vFirst, dVector, modMap);
			}
			if (!edge.getSecond().isSticky()) {

				Vector2D dVector;
				if (edge.getFirst().isSticky()) {
					dVector = Vector2D.mult(2, slackV);
				} else {
					dVector = slackV;
				}
				add(vSecond, dVector, modMap);
			}
		}

	}

}
