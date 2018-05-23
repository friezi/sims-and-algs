/**
 * 
 */
package de.zintel.verlet;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.g2d.verlet.VLVertexSkid;
import de.zintel.math.Vector2D;
import de.zintel.math.VectorField2D;

/**
 * @author friedemann.zintel
 *
 */
public class VerletProcessor {

	private final Set<Runnable> progressors = new LinkedHashSet<>();
	
	private final Set<BiFunction<Vector2D,Vector2D, Vector2D>> influenceVectorProviders = new LinkedHashSet<>();
	
	private final Set<VerletProcessor> processors=new LinkedHashSet<>();
	
	private final Collection<VLVertexSkid> vertexSkids;

	private final Collection<VLEdge2D> edges;
	
	private final Object syncObject;
	
	private int iterations;
	
	private final double decay;
	
	public VerletProcessor(Collection<VLVertexSkid> vertexSkids, Collection<VLEdge2D> edges, Object syncObject, int iterations,
			double decay) {
		this.vertexSkids = vertexSkids;
		this.edges = edges;
		this.syncObject = syncObject;
		this.iterations = iterations;
		this.decay = decay;
	}

	public void progress(Dimension dimension) {
		
		for (Runnable progressor :progressors) {
			progressor.run();
		}
		
//
//		if (useWind) {
//			windSimulator.progressWindflaw();
//		}
//
//		final double width = dimension.getWidth();
//		final double height = dimension.getHeight();
//
//		if (useWind && useWindparticles) {
//
//			if (windParticleCnt == 0) {
//				windParticles.clear();
//			}
//
//			final VectorField2D airstreamField = windSimulator.getAirstreamField();
//
//			if (windParticleCnt % windParticleFrequence == 0) {
//
//				final List<Integer> airstreamdimensions = airstreamField.getDimensions();
//				final int windWidth = airstreamdimensions.get(0);
//				final int windHeight = airstreamdimensions.get(1);
//
//				windParticles.add(new VLVertex2D(
//						new Vector2D(rnd.nextInt(windWidth) * width / windWidth, rnd.nextInt(windHeight) * height / windHeight)));
//			} else {
//
//				final Iterator<VLVertex2D> iterator = windParticles.iterator();
//				while (iterator.hasNext()) {
//
//					final VLVertex2D windParticle = iterator.next();
//					final Vector2D newPosition = calculateNewPosition(windParticle, 1);
//					final Vector2D wind = windSimulator.calculateWind(windParticle.getCurrent());
//					newPosition.add(wind.mult(1 / wind.length()));
//
//					if (newPosition.x >= width || newPosition.x < 0 || newPosition.y >= height || newPosition.y < 0) {
//						iterator.remove();
//					} else {
//						repositionVertex(windParticle, newPosition);
//					}
//				}
//			}
//
//			if (windParticleCnt == Long.MAX_VALUE) {
//				windParticleCnt = 0;
//			} else {
//				windParticleCnt++;
//			}
//		}

		synchronized (syncObject) {

			vertexSkids.parallelStream().forEach(new Consumer<VLVertexSkid>() {

				@Override
				public void accept(VLVertexSkid vertexSkid) {

					VLVertex2D vertex = vertexSkid.getVertex();

					if (vertexSkid.isSticky()) {
						vertex.setPrevious(vertex.getCurrent());
						return;
					}
//
//			vertices.parallelStream().forEach(new Consumer<VLVertex2D>() {
//
//				@Override
//				public void accept(VLVertex2D vertex) {
//
//					if (vertex.isPinned()) {
//						vertex.setPrevious(vertex.getCurrent());
//						return;
//					}
					
//
//					double frictionFac = 1;
//					// friction
//					if (vertex.getCurrent().y == height - 1) {
//						frictionFac = friction;
//					}

					final Vector2D newCurrent = calculateNewPosition(vertex);
					
					for (BiFunction<Vector2D,Vector2D,Vector2D> provider:influenceVectorProviders) {
						newCurrent.add(provider.apply(vertex.getCurrent(),newCurrent));
					}
//
//					if (useGravity) {
//						newCurrent.add(gravity);
//					}
//
//					if (useWind) {
//						newCurrent.add(windSimulator.calculateWind(vertex.getCurrent()));
//					}

					repositionVertex(vertex, newCurrent);

				}
			});

			for (int i = 0; i < iterations; i++) {
				handleConstraints(dimension);
			}
			
			for (VerletProcessor processor:processors) {
				processor.progress(dimension);
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
//		return Vector2D.add(vertex.getCurrent(), Vector2D.mult(friction, Vector2D.substract(vertex.getCurrent(), vertex.getPrevious())));
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

	private void handleConstraints(Dimension dimension) {

		vertexSkids.parallelStream().map(skid->skid.getVertex()).forEach(vertex -> handleBorderConstraints(vertex, dimension));

		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge);
		}

	}

	private void handleStickConstraints(final VLEdge2D edge) {

		final Vector2D cFirst = edge.getFirst().getVertex().getCurrent();
		final Vector2D cSecond = edge.getSecond().getVertex().getCurrent();
		Vector2D dV = Vector2D.substract(cFirst, cSecond);
		if (dV.isNullVector()) {
			// Problem!!! no line anymore
			// System.out.println("Nullvector! edge: " + edge);
			// do no adjustment to prevent NaN
			return;
			// dV =
			// Vector2D.max(Vector2D.substract(edge.getFirst().getPrevious(),
			// edge.getSecond().getCurrent()),
			// Vector2D.substract(edge.getSecond().getPrevious(),
			// edge.getFirst().getCurrent()));
			// dV.mult(0.001);
		}

		if (dV.length() != edge.getPreferredLength()) {

			double diff = dV.length() - edge.getPreferredLength();
			Vector2D slackV = Vector2D.mult(diff / dV.length() / 2, dV);

			if (!edge.getFirst().isSticky()) {
				if (edge.getSecond().isSticky()) {
					cFirst.substract(Vector2D.mult(2, slackV));
				} else {
					cFirst.substract(slackV);
				}
			}
			if (!edge.getSecond().isSticky()) {
				if (edge.getFirst().isSticky()) {
					cSecond.add(Vector2D.mult(2, slackV));
				} else {
					cSecond.add(slackV);
				}
			}
		}

	}

	private void handleBorderConstraints(final VLVertex2D vertex, Dimension dimension) {

		final Vector2D current = vertex.getCurrent();
		final Vector2D previous = vertex.getPrevious();

		// bounce
		if (current.x > dimension.getWidth() - 1) {
			current.x = dimension.getWidth() - 1 - ((current.x - (dimension.getWidth() - 1)) * decay);
			previous.x = dimension.getWidth() - 1 - (previous.x - (dimension.getWidth() - 1));
		} else if (current.x < 0) {
			current.x = -current.x * decay;
			previous.x = -previous.x;
		}

		if (current.y > dimension.getHeight() - 1) {
			current.y = dimension.getHeight() - 1 - ((current.y - (dimension.getHeight() - 1)) * decay);
			previous.y = dimension.getHeight() - 1 - (previous.y - (dimension.getHeight() - 1));
		} else if (current.y < 0) {
			current.y = -current.y * decay;
			previous.y = -previous.y;
		}

	}

	public VerletProcessor addInfluenceVectorProvider(final BiFunction<Vector2D, Vector2D, Vector2D> provider) {
		influenceVectorProviders.add(provider);
		return this;
	}

	public VerletProcessor removeInfluenceVectorProvider(final BiFunction<Vector2D, Vector2D, Vector2D> provider) {
		influenceVectorProviders.remove(provider);
		return this;
	}

	public VerletProcessor addProgressor(final Runnable progressor) {
		progressors.add(progressor);
		return this;
	}

	public VerletProcessor removeProgressor(final Runnable progressor) {
		progressors.remove(progressor);
		return this;
	}

	public VerletProcessor addProcressor(final VerletProcessor processor) {
		processors.add(processor);
		return this;
	}

	public VerletProcessor removeProcessor(final VerletProcessor processor) {
		processors.remove(processor);
		return this;
	}

	public int getIterations() {
		return iterations;
	}

	public VerletProcessor setIterations(int iterations) {
		this.iterations = iterations;
		return this;
	}

}
