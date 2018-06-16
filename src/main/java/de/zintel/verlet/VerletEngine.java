/**
 * 
 */
package de.zintel.verlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.g2d.verlet.VLVertexSkid;
import de.zintel.math.Vector2D;
import de.zintel.utils.Pair;

/**
 * @author friedemann.zintel
 *
 */
public class VerletEngine {

	private final Set<Runnable> progressors = new LinkedHashSet<>();

	private final Set<BiFunction<Vector2D, Vector2D, Vector2D>> influenceVectorProviders = new LinkedHashSet<>();

	private final Set<VerletEngine> engines = new LinkedHashSet<>();

	private Function<VLVertexSkid, Pair<Vector2D, Vector2D>> vertexConstraintHandler = null;

	private final Collection<VLVertexSkid> vertexSkids;

	private final Collection<VLEdge2D> edges;

	private final Object syncObject;

	private int iterations;

	public VerletEngine(Collection<VLVertexSkid> vertexSkids, Collection<VLEdge2D> edges, Object syncObject, int iterations) {
		this.vertexSkids = vertexSkids;
		this.edges = edges;
		this.syncObject = syncObject;
		this.iterations = iterations;
	}

	public void progress() {

		for (Runnable progressor : progressors) {
			progressor.run();
		}

		//
		// if (useWind) {
		// windSimulator.progressWindflaw();
		// }
		//
		// final double width = dimension.getWidth();
		// final double height = dimension.getHeight();
		//
		// if (useWind && useWindparticles) {
		//
		// if (windParticleCnt == 0) {
		// windParticles.clear();
		// }
		//
		// final VectorField2D airstreamField =
		// windSimulator.getAirstreamField();
		//
		// if (windParticleCnt % windParticleFrequence == 0) {
		//
		// final List<Integer> airstreamdimensions =
		// airstreamField.getDimensions();
		// final int windWidth = airstreamdimensions.get(0);
		// final int windHeight = airstreamdimensions.get(1);
		//
		// windParticles.add(new VLVertex2D(
		// new Vector2D(rnd.nextInt(windWidth) * width / windWidth,
		// rnd.nextInt(windHeight) * height / windHeight)));
		// } else {
		//
		// final Iterator<VLVertex2D> iterator = windParticles.iterator();
		// while (iterator.hasNext()) {
		//
		// final VLVertex2D windParticle = iterator.next();
		// final Vector2D newPosition = calculateNewPosition(windParticle, 1);
		// final Vector2D wind =
		// windSimulator.calculateWind(windParticle.getCurrent());
		// newPosition.add(wind.mult(1 / wind.length()));
		//
		// if (newPosition.x >= width || newPosition.x < 0 || newPosition.y >=
		// height || newPosition.y < 0) {
		// iterator.remove();
		// } else {
		// repositionVertex(windParticle, newPosition);
		// }
		// }
		// }
		//
		// if (windParticleCnt == Long.MAX_VALUE) {
		// windParticleCnt = 0;
		// } else {
		// windParticleCnt++;
		// }
		// }

		synchronized (syncObject) {

			vertexSkids.parallelStream().forEach(new Consumer<VLVertexSkid>() {

				@Override
				public void accept(VLVertexSkid vertexSkid) {

					VLVertex2D vertex = vertexSkid.getVertex();

					if (vertexSkid.isSticky()) {
						if (!vertexSkid.isDependent()) {
							vertex.setPrevious(vertex.getCurrent());
						}
						return;
					}

					final Vector2D newCurrent = calculateNewPosition(vertex);

					for (BiFunction<Vector2D, Vector2D, Vector2D> provider : influenceVectorProviders) {
						newCurrent.add(provider.apply(vertex.getCurrent(), newCurrent));
					}

					repositionVertex(vertex, newCurrent);

				}
			});

			for (int i = 0; i < iterations; i++) {
				handleConstraints(i + 1);
			}

			for (VerletEngine engine : engines) {
				engine.progress();
			}

		}
	}

	/**
	 * calculates the new position. Uses verlet integration.
	 * 
	 * @param vertex
	 * @param friction
	 * @return
	 */
	public Vector2D calculateNewPosition(VLVertex2D vertex) {
		return Vector2D.add(vertex.getCurrent(), Vector2D.substract(vertex.getCurrent(), vertex.getPrevious()));
	}

	/**
	 * sets the vertex to the new position
	 * 
	 * @param vertex
	 * @param newCurrent
	 */
	public void repositionVertex(VLVertex2D vertex, final Vector2D newCurrent) {
		vertex.setPrevious(vertex.getCurrent());
		vertex.setCurrent(newCurrent);
	}

	private void handleConstraints(final int iteration) {

		if (vertexConstraintHandler != null) {
			vertexSkids.parallelStream().forEach(vertex -> adjust(vertex.getVertex(), vertexConstraintHandler.apply(vertex)));
		}

		Map<VLVertex2D, Collection<Vector2D>> modMap = new HashMap<>();
		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge,modMap, iteration);
		}
//		for (Map.Entry<VLVertex2D, Collection<Vector2D>> entry:modMap.entrySet()) {
//			
//			Vector2D dv=new Vector2D();
//			for (Vector2D v:entry.getValue()) {
//				dv.add(v);
//			}
//			dv.mult(1.0/entry.getValue().size());
//			entry.getKey().getCurrent().add(dv);
//		}

	}

	private void add(final VLVertex2D vertex, final Vector2D vector, final Map<VLVertex2D, Collection<Vector2D>> map) {

		Collection<Vector2D> collection = map.get(vertex);
		if (collection == null) {

			collection = new ArrayList<>();
			map.put(vertex, collection);

		}

		collection.add(vector);

	}

	private void adjust(final VLVertex2D vertex, Pair<Vector2D, Vector2D> delta) {

		vertex.getCurrent().add(delta.getFirst());
		vertex.getPrevious().add(delta.getSecond());

	}

	private void handleStickConstraints(final VLEdge2D edge,Map<VLVertex2D, Collection<Vector2D>> modMap, final int iteration) {

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
					cFirst.substract(Vector2D.mult(2, slackV));
//					dVector=Vector2D.mult(-2, slackV);
				} else {
					cFirst.substract(slackV);
//					dVector=Vector2D.mult(-1, slackV);
				}
//				add(vFirst,dVector,modMap);
			}
			if (!edge.getSecond().isSticky()) {

				Vector2D dVector;
				if (edge.getFirst().isSticky()) {
					cSecond.add(Vector2D.mult(2, slackV));
//					dVector=Vector2D.mult(2, slackV);
				} else {
					cSecond.add(slackV);
//					dVector=slackV;
				}
//				add(vSecond,dVector,modMap);
			}
		}

	}

	public VerletEngine addInfluenceVectorProvider(final BiFunction<Vector2D, Vector2D, Vector2D> provider) {
		influenceVectorProviders.add(provider);
		return this;
	}

	public VerletEngine removeInfluenceVectorProvider(final BiFunction<Vector2D, Vector2D, Vector2D> provider) {
		influenceVectorProviders.remove(provider);
		return this;
	}

	public VerletEngine addProgressor(final Runnable progressor) {
		progressors.add(progressor);
		return this;
	}

	public VerletEngine removeProgressor(final Runnable progressor) {
		progressors.remove(progressor);
		return this;
	}

	public VerletEngine addEngine(final VerletEngine engine) {
		engines.add(engine);
		return this;
	}

	public VerletEngine addEngines(final Collection<VerletEngine> engines) {
		for (VerletEngine engine : engines) {
			addEngine(engine);
		}
		return this;
	}

	public VerletEngine removeEngine(final VerletEngine engine) {
		engines.remove(engine);
		return this;
	}

	public int getIterations() {
		return iterations;
	}

	public VerletEngine setIterations(int iterations) {
		this.iterations = iterations;
		return this;
	}

	public Function<VLVertexSkid, Pair<Vector2D, Vector2D>> getVertexConstraintHandler() {
		return vertexConstraintHandler;
	}

	/**
	 * @param vertexConstraintHandler
	 * @return <delta_current,delta_previous>
	 */
	public VerletEngine setVertexConstraintHandler(Function<VLVertexSkid, Pair<Vector2D, Vector2D>> vertexConstraintHandler) {
		this.vertexConstraintHandler = vertexConstraintHandler;
		return this;
	}

}
