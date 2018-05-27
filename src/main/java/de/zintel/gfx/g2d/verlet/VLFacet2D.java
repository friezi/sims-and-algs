/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import de.zintel.math.Vector2D;

/**
 * @author friedemann.zintel
 *
 */
public class VLFacet2D implements IVLPolygon2D {

	private final static Comparator<VLVertexSkid> vertexComparator = new Comparator<VLVertexSkid>() {

		@Override
		public int compare(VLVertexSkid o1, VLVertexSkid o2) {
			Vector2D left = o1.getVertex().getCurrent();
			Vector2D right = o2.getVertex().getCurrent();
			return (left.x == right.x && left.y == right.y) ? 0 : -1;
		}
	};

	private final VLEdge2D edge1;

	private final VLEdge2D edge2;

	private final VLEdge2D edge3;

	private final VLVertexSkid vertex1;

	private final VLVertexSkid vertex2;

	private final VLVertexSkid vertex3;

	private Consumer<VLFacet2D> renderer;

	/**
	 * 
	 */
	public VLFacet2D(final VLVertexSkid v1, final VLVertexSkid v2, final VLVertexSkid v3, Consumer<VLFacet2D> renderer) {
		this(new VLEdge2D(v1, v2, Color.WHITE, null), new VLEdge2D(v2, v3, Color.WHITE, null), new VLEdge2D(v3, v1, Color.WHITE, null),
				renderer);
	}

	/**
	 * 
	 */
	VLFacet2D(final VLEdge2D e1, final VLEdge2D e2, final VLEdge2D e3, Consumer<VLFacet2D> renderer) {

		this.edge1 = e1;
		this.edge2 = e2;
		this.edge3 = e3;
//		Set<VLVertexSkid> vertices = new TreeSet<>(vertexComparator);
//		vertices.add(e1.getFirst());
//		vertices.add(e1.getSecond());
//		vertices.add(e2.getFirst());
//		vertices.add(e2.getSecond());
//		vertices.add(e3.getFirst());
//		vertices.add(e3.getSecond());
//
//		if (vertices.size() != 3) {
//			throw new RuntimeException("not 3 vertices! : "+vertices.size());
//		}
//
//		Iterator<VLVertexSkid> iterator = vertices.iterator();

//		this.vertex1 = iterator.next();
//		this.vertex2 = iterator.next();
//		this.vertex3 = iterator.next();

		this.vertex1 = e1.getFirst();
		this.vertex2 = e2.getFirst();
		this.vertex3 = e3.getFirst();
		this.renderer = renderer;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IRenderable#render()
	 */
	@Override
	public void render() {

		if (renderer != null) {
			renderer.accept(this);
		}

	}

	public VLFacet2D setColor(Color color) {
		for (VLEdge2D edge : getEdges()) {
			edge.setColor(color);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IEdgeContainer2D#getEdges()
	 */
	@Override
	public List<VLEdge2D> getEdges() {
		return Arrays.asList(edge1, edge2, edge3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.g2d.IEdgeContainer2D#dcopy()
	 */
	@Override
	public VLFacet2D dcopy() {
		return new VLFacet2D(edge1.dcopy(), edge2.dcopy(), edge3.dcopy(), renderer).setColor(edge1.getColor());
	}

	public VLEdge2D getEdge1() {
		return edge1;
	}

	public VLEdge2D getEdge2() {
		return edge2;
	}

	public VLEdge2D getEdge3() {
		return edge3;
	}

	public VLVertexSkid getVertex1() {
		return vertex1;
	}

	public VLVertexSkid getVertex2() {
		return vertex2;
	}

	public VLVertexSkid getVertex3() {
		return vertex3;
	}

	public Consumer<VLFacet2D> getRenderer() {
		return renderer;
	}

	public void setRenderer(Consumer<VLFacet2D> renderer) {
		this.renderer = renderer;
	}

	@Override
	public Collection<VLVertexSkid> getVertices() {
		return Arrays.asList(vertex1, vertex2, vertex3);
	}

}
