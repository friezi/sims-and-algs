/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.verlet.IVLPolygon2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.g2d.verlet.VLVertexSkid;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2DPlain;
import de.zintel.utils.IterableIterator;

/**
 * alert! set contextEdgesProvider!
 * 
 * @author friedemann.zintel
 *
 */
public class PolygonInterpolatingRenderer<T extends IVLPolygon2D> implements Consumer<T> {

	private final IGraphicsSubsystem graphicsSubsystem;

	private final Function<VLEdge2D, Color> colorModifier;

	/**
	 * provides the context-edges on which basis the color-interpolation takes
	 * place.
	 */
	private Function<T, List<VLEdge2D>> contextEdgesProvider;

	public PolygonInterpolatingRenderer(IGraphicsSubsystem graphicsSubsystem, Function<VLEdge2D, Color> colorModifier) {
		this.graphicsSubsystem = graphicsSubsystem;
		this.colorModifier = colorModifier;
	}

	@Override
	public void accept(T polygon) {

		IterableIterator<VLVertexSkid> vertexIterator = new IterableIterator<>(polygon.getVertices().iterator());
		if (!vertexIterator.hasNext()) {
			// zero vertices
			return;
		}

		Point pivot = vertexIterator.next().getVertex().getCurrent().toPoint();
		if (!vertexIterator.hasNext()) {
			// only one vertex in total
			return;
		}

		VLVertex2D start = vertexIterator.next().getVertex();

		while (vertexIterator.hasNext()) {

			VLVertex2D end = vertexIterator.next().getVertex();

			final LinearPointInterpolater2D baselineInterpolater = new LinearPointInterpolater2D(start.getCurrent().toPoint(),
					end.getCurrent().toPoint(), true);
			for (IterationUnit2D baseunit : baselineInterpolater) {

				final LinearPointInterpolater2D lineInterpolater = new LinearPointInterpolater2D(pivot, baseunit.getPoint(), true);
				for (IterationUnit2D unit : lineInterpolater) {

					Point point = unit.getPoint();
					graphicsSubsystem.drawPoint(point.x, point.y, calculateColor(new Vector2DPlain(point), polygon));
				}
			}

			start = end;

		}
	}

	private Color calculateColor(final Vector2DPlain point, final T polygon) {

		final Collection<EdgeValue> values = new ArrayList<>(3);
		for (VLEdge2D edge : (contextEdgesProvider != null ? contextEdgesProvider.apply(polygon) : Collections.<VLEdge2D> emptyList())) {
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

	private double distance(final Vector2DPlain point, final VLEdge2D edge) {

		final Vector2DPlain start = edge.getFirst().getVertex().getCurrent();
		final Vector2DPlain end = edge.getSecond().getVertex().getCurrent();
		final Vector2DPlain edgeV = Vector2DPlain.substract(end, start);
		final double lambda = (Vector2DPlain.mult(point, edgeV) - Vector2DPlain.mult(start, edgeV)) / Vector2DPlain.mult(edgeV, edgeV);
		// check for exceeding the line
		final Vector2DPlain referencePoint = lambda > 1 ? end : lambda < 0 ? start : Vector2DPlain.mult(lambda, edgeV).add(start);
		return Vector2DPlain.substract(referencePoint, point).length();

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

	public Function<T, List<VLEdge2D>> getContextEdgesProvider() {
		return contextEdgesProvider;
	}

	public PolygonInterpolatingRenderer<T> setContextEdgesProvider(Function<T, List<VLEdge2D>> contextEdgesProvider) {
		this.contextEdgesProvider = contextEdgesProvider;
		return this;
	}

}
