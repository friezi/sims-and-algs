/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author friedemann.zintel
 *
 */
public class VLCuboid2D implements IVLEdgeContainer2D {

	private Consumer<VLCuboid2D> renderer;

	private List<VLEdge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public VLCuboid2D(VLVertex2D v1, VLVertex2D v2, VLVertex2D v3, VLVertex2D v4, Consumer<VLCuboid2D> renderer,
			Consumer<VLEdge2D> edgeRenderer) {
		this(new VLVertexSkid(v1), new VLVertexSkid(v2), new VLVertexSkid(v3), new VLVertexSkid(v4), renderer, edgeRenderer);
	}

	/**
	 * 
	 */
	public VLCuboid2D(VLVertexSkid v1, VLVertexSkid v2, VLVertexSkid v3, VLVertexSkid v4, Consumer<VLCuboid2D> renderer,
			Consumer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		edges.add(new VLEdge2D(v1, v2, Color.WHITE, edgeRenderer));
		edges.add(new VLEdge2D(v2, v3, Color.WHITE, edgeRenderer));
		edges.add(new VLEdge2D(v3, v4, Color.WHITE, edgeRenderer));
		edges.add(new VLEdge2D(v4, v1, Color.WHITE, edgeRenderer));
		edges.add(new VLEdge2D(v1, v3, Color.WHITE, edgeRenderer));
		edges.add(new VLEdge2D(v2, v4, Color.WHITE, edgeRenderer));
	}

	private VLCuboid2D(Consumer<VLCuboid2D> renderer, List<VLEdge2D> edges) {
		this.renderer = renderer;
		this.edges = edges;
	}

	@Override
	public void render() {
		if (renderer != null) {
			renderer.accept(this);
		}
	}

	@Override
	public List<VLEdge2D> getEdges() {
		return edges;
	}

	@Override
	public IVLEdgeContainer2D dcopy() {
		return new VLCuboid2D(renderer, edges.stream().map(edge -> edge.dcopy()).collect(Collectors.toList()));
	}

}
