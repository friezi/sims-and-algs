/**
 * 
 */
package de.zintel.verlet;

import java.awt.Dimension;
import java.util.function.Function;

import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.g2d.verlet.VLVertexSkid;
import de.zintel.math.Vector2D;
import de.zintel.utils.Pair;

/**
 * @author friedemann.zintel
 *
 */
public class VertexBorderConstraintHandler implements Function<VLVertexSkid, Pair<Vector2D, Vector2D>> {

	private Dimension dimension;

	private double decay;

	private static final TriFunction<Double, Double, Double, Double> df = (x, limit, decay) -> -(decay + 1) * (x - limit);

	public VertexBorderConstraintHandler(Dimension dimension, double decay) {
		this.dimension = dimension;
		this.decay = decay;
	}

	@Override
	public Pair<Vector2D, Vector2D> apply(VLVertexSkid skid) {

		VLVertex2D vertex = skid.getVertex();

		final Vector2D current = vertex.getCurrent();
		final Vector2D previous = vertex.getPrevious();

		final double xmax = dimension.getWidth() - 1;
		final double xmin = 0;
		final double ymax = dimension.getHeight() - 1;
		final double ymin = 0;

		final Vector2D dcurrent = new Vector2D();
		final Vector2D dprevious = new Vector2D();

		// bounce
		if (current.x > xmax) {
			dcurrent.x = df.apply(current.x, xmax, decay);
			dprevious.x = df.apply(previous.x, xmax, 1.0);
		} else if (current.x < xmin) {
			dcurrent.x = df.apply(current.x, xmin, decay);
			dprevious.x = df.apply(previous.x, xmin, 1.0);
		}

		if (current.y > ymax) {
			dcurrent.y = df.apply(current.y, ymax, decay);
			dprevious.y = df.apply(previous.y, ymax, 1.0);
		} else if (current.y < ymin) {
			dcurrent.y = df.apply(current.y, ymin, decay);
			dprevious.y = df.apply(previous.y, ymin, 1.0);
		}

		return new Pair<>(dcurrent, dprevious);
	}

	public Dimension getDimension() {
		return dimension;
	}

	public VertexBorderConstraintHandler setDimension(Dimension dimension) {
		this.dimension = dimension;
		return this;
	}

	public double getDecay() {
		return decay;
	}

	public VertexBorderConstraintHandler setDecay(double decay) {
		this.decay = decay;
		return this;
	}

	private static interface TriFunction<A, B, C, D> {
		D apply(A a, B b, C c);
	}

}
