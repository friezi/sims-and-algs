/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.zintel.gfx.IRenderer;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class VLChain2D implements IVLEdgeContainer2D {

	private IRenderer<VLChain2D> renderer;

	private List<VLEdge2D> edges = new LinkedList<>();

	/**
	 * 
	 */
	public VLChain2D(Collection<VLVertex2D> vertices, IRenderer<VLChain2D> renderer, IRenderer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		VLVertex2D previous = null;
		for (VLVertex2D vertex : vertices) {
			if (previous != null) {
				edges.add(new VLEdge2D(previous, vertex, edgeRenderer));
			}
			previous = vertex;
		}
	}

	public VLChain2D(VLVertex2D first, VLVertex2D last, int elements, IRenderer<VLChain2D> renderer, IRenderer<VLEdge2D> edgeRenderer) {

		this.renderer = renderer;

		VLVertex2D previous = first;
		for (int i = 2; i < elements; i++) {

			final VLVertex2D current = new VLVertex2D(
					new Vector2D(MathUtils.interpolateLinearReal(first.getCurrent().x, last.getCurrent().x, i, elements),
							MathUtils.interpolateLinearReal(first.getCurrent().y, last.getCurrent().y, i, elements)));
			edges.add(new VLEdge2D(previous, current, edgeRenderer));
			previous = current;

		}

		edges.add(new VLEdge2D(previous, last, edgeRenderer));

	}

	private VLChain2D(List<VLEdge2D> edges, IRenderer<VLChain2D> renderer) {
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
		return new VLChain2D(edges.stream().map(edge -> edge.dcopy()).collect(Collectors.toList()), renderer);
	}

}
