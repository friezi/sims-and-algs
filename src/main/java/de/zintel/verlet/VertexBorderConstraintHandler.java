/**
 * 
 */
package de.zintel.verlet;

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

	private static final Vector2D GRAV_DOWN = new Vector2D(0, 0.8);

	private double xmin, ymin, xmax, ymax;

	private double decay;

	private static final TriFunction<Double, Double, Double, Double> dX = (x, limit, decay) -> -(decay + 1) * (x - limit);

	public VertexBorderConstraintHandler(double xmin, double ymin, double xmax, double ymax, double decay) {
		this.decay = decay;
	}

	@Override
	public Pair<Vector2D, Vector2D> apply(VLVertexSkid skid) {

		VLVertex2D vertex = skid.getVertex();

		final Vector2D current = vertex.getCurrent();
		final Vector2D previous = vertex.getPrevious();

		final Vector2D dcurrent = new Vector2D();
		final Vector2D dprevious = new Vector2D();

		// bounce
		if (current.x > xmax) {
			dcurrent.x = dX.apply(current.x, xmax, decay);
			dprevious.x = dX.apply(previous.x, xmax, 1.0);
		} else if (current.x < xmin) {
			dcurrent.x = dX.apply(current.x, xmin, decay);
			dprevious.x = dX.apply(previous.x, xmin, 1.0);
		}

		if (current.y > ymax) {
			dcurrent.y = dX.apply(current.y/*-2*GRAV_DOWN.y*/, ymax, decay);
			dprevious.y = dX.apply(previous.y, ymax, 1.0);
		} else if (current.y < ymin) {
			dcurrent.y = dX.apply(current.y, ymin, decay);
			dprevious.y = dX.apply(previous.y, ymin, 1.0);
		}

		return new Pair<>(dcurrent, dprevious);
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

	public double getXmin() {
		return xmin;
	}

	public VertexBorderConstraintHandler setXmin(double xmin) {
		this.xmin = xmin;
		return this;
	}

	public double getYmin() {
		return ymin;
	}

	public VertexBorderConstraintHandler setYmin(double ymin) {
		this.ymin = ymin;
		return this;
	}

	public double getXmax() {
		return xmax;
	}

	public VertexBorderConstraintHandler setXmax(double xmax) {
		this.xmax = xmax;
		return this;
	}

	public double getYmax() {
		return ymax;
	}

	public VertexBorderConstraintHandler setYmax(double ymax) {
		this.ymax = ymax;
		return this;
	}

}
