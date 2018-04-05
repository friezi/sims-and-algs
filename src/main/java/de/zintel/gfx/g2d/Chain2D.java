/**
 * 
 */
package de.zintel.gfx.g2d;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class Chain2D implements IEdgeContainer2D {

	private IRenderer<Chain2D> renderer;

	private List<Edge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public Chain2D(Collection<Vertex2D> vertices, IRenderer<Chain2D> renderer, IRenderer<Edge2D> edgeRenderer) {

		this.renderer = renderer;

		Vertex2D previous = null;
		for (Vertex2D vertex : vertices) {
			if (previous != null) {
				edges.add(new Edge2D(previous, vertex, edgeRenderer));
			}
			previous = vertex;
		}
	}

	public Chain2D(Vertex2D first, Vertex2D last, int elements, IRenderer<Chain2D> renderer, IRenderer<Edge2D> edgeRenderer) {

		this.renderer = renderer;

		Vertex2D previous = first;
		for (int i = 2; i < elements; i++) {

			final Vertex2D current = new Vertex2D(
					new Vector2D(MathUtils.interpolateLinearReal(first.getCurrent().x, last.getCurrent().x, i, elements),
							MathUtils.interpolateLinearReal(first.getCurrent().y, last.getCurrent().y, i, elements)));
			edges.add(new Edge2D(previous, current, edgeRenderer));
			previous = current;

		}

		edges.add(new Edge2D(previous, last, edgeRenderer));

	}

	private Chain2D(List<Edge2D> edges, IRenderer<Chain2D> renderer) {
		this.renderer = renderer;
		this.edges = edges;
	}

	@Override
	public void render() {
		if (renderer != null) {
			renderer.render(this);
		}
	}

	@Override
	public List<Edge2D> getEdges() {
		return edges;
	}

	@Override
	public IEdgeContainer2D dcopy() {
		return new Chain2D(edges.stream().map(edge -> edge.dcopy()).collect(Collectors.toList()), renderer);
	}

}
