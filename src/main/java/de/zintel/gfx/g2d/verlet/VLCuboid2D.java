/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.zintel.gfx.IRenderer;

/**
 * @author friedemann.zintel
 *
 */
public class VLCuboid2D implements IVLEdgeContainer2D {

	private IRenderer<VLCuboid2D> renderer;

	private List<VLEdge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public VLCuboid2D(VLVertex2D v1, VLVertex2D v2, VLVertex2D v3, VLVertex2D v4, IRenderer<VLCuboid2D> renderer, IRenderer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		edges.add(new VLEdge2D(v1, v2, edgeRenderer));
		edges.add(new VLEdge2D(v2, v3, edgeRenderer));
		edges.add(new VLEdge2D(v3, v4, edgeRenderer));
		edges.add(new VLEdge2D(v4, v1, edgeRenderer));
		edges.add(new VLEdge2D(v1, v3, edgeRenderer));
		edges.add(new VLEdge2D(v2, v4, edgeRenderer));
	}

	private VLCuboid2D(IRenderer<VLCuboid2D> renderer, List<VLEdge2D> edges) {
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
	public List<VLEdge2D> getEdges() {
		return edges;
	}

	@Override
	public IVLEdgeContainer2D dcopy() {
		return new VLCuboid2D(renderer, edges.stream().map(edge -> edge.dcopy()).collect(Collectors.toList()));
	}

}
