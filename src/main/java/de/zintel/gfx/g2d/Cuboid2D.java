/**
 * 
 */
package de.zintel.gfx.g2d;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author friedemann.zintel
 *
 */
public class Cuboid2D implements IEdgeContainer2D {

	private IRenderer<Cuboid2D> renderer;

	private List<Edge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public Cuboid2D(Vertex2D v1, Vertex2D v2, Vertex2D v3, Vertex2D v4, IRenderer<Cuboid2D> renderer, IRenderer<Edge2D> edgeRenderer) {

		this.renderer = renderer;

		edges.add(new Edge2D(v1, v2, edgeRenderer));
		edges.add(new Edge2D(v2, v3, edgeRenderer));
		edges.add(new Edge2D(v3, v4, edgeRenderer));
		edges.add(new Edge2D(v4, v1, edgeRenderer));
		edges.add(new Edge2D(v1, v3, edgeRenderer));
		edges.add(new Edge2D(v2, v4, edgeRenderer));
	}

	private Cuboid2D(IRenderer<Cuboid2D> renderer, List<Edge2D> edges) {
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
		return new Cuboid2D(renderer, edges.stream().map(edge -> edge.dcopy()).collect(Collectors.toList()));
	}

}
