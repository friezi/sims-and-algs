/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.verlet.IVLEdgeContainer2D;
import de.zintel.gfx.g2d.verlet.IVLPolygon2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;
import de.zintel.utils.IterableIterator;

/**
 * @author friedemann.zintel
 *
 */
public class PolygonInterpolatingRenderer<T extends IVLPolygon2D> implements Consumer<T> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final Function<VLEdge2D, Color> colorModifier;

	public PolygonInterpolatingRenderer(IGraphicsSubsystem graphicsSubsystem, Function<VLEdge2D, Color> colorModifier) {
		this.graphicsSubsystem = graphicsSubsystem;
		this.colorModifier = colorModifier;
	}

	@Override
	public void accept(T polygon) {

		IterableIterator<VLVertex2D> vertexIterator = new IterableIterator<>(polygon.getVertices().iterator());
		if (!vertexIterator.hasNext()) {
			// zero vertices
			return;
		}

		Point pivot = vertexIterator.next().getCurrent().toPoint();
		if (!vertexIterator.hasNext()) {
			// only one vertex in total
			return;
		}

		VLVertex2D start = vertexIterator.next();

		while (vertexIterator.hasNext()) {

			VLVertex2D end = vertexIterator.next();

			final LinearPointInterpolater2D baselineInterpolater = new LinearPointInterpolater2D(start.getCurrent().toPoint(),
					end.getCurrent().toPoint(), true);
			for (IterationUnit2D baseunit : baselineInterpolater) {

				final LinearPointInterpolater2D lineInterpolater = new LinearPointInterpolater2D(pivot, baseunit.getPoint(), true);
				for (IterationUnit2D unit : lineInterpolater) {

					Point point = unit.getPoint();
					graphicsSubsystem.drawPoint(point.x, point.y, calculateColor(new Vector2D(point), polygon));
				}
			}

			start = end;

		}
	}

	private Color calculateColor(final Vector2D point, final IVLEdgeContainer2D polygon) {

		final Collection<EdgeValue> values = new ArrayList<>(3);
		for (VLEdge2D edge : polygon.getEdges()) {
			values.add(new EdgeValue(1 / (distance(point, edge) + 1.0), edge));
		}

		final Double sum = values.stream().collect(Collectors.summingDouble(EdgeValue::getValue));

		double red = 0, green = 0, blue = 0, alpha = 0;
		for (EdgeValue ev : values) {

			final Color color = colorModifier.apply(ev.getEdge());
			final double coeff = ev.getValue() / sum;

			red += coeff * color.getRed();
			green += coeff * color.getGreen();
			blue += coeff * color.getBlue();
			alpha += coeff * color.getAlpha();

		}

		return new Color((int) red, (int) green, (int) blue, (int) alpha);

	}

	private double distance(final Vector2D point, final VLEdge2D edge) {

		final Vector2D edgeV = edge.currentToVector2D();
		final Vector2D edgeS = edge.getFirst().getCurrent();
		return Vector2D.mult((Vector2D.mult(point, edgeV) - Vector2D.mult(edgeS, edgeV)) / Vector2D.mult(edgeV, edgeV), edgeV).add(edgeS)
				.substract(point).length();

	}

	private static class EdgeValue {

		private final Double value;

		private final VLEdge2D edge;

		public EdgeValue(Double value, VLEdge2D edge) {
			this.value = value;
			this.edge = edge;
		}

		public Double getValue() {
			return value;
		}

		public VLEdge2D getEdge() {
			return edge;
		}

	}

}
