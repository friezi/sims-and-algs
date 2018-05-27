/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

/**
 * serves as a skid resp. frame for a vertex. It holds additional properties to
 * a vertex.
 * 
 * @author friedemann.zintel
 *
 */
public class VLVertexSkid {

	private final VLVertex2D vertex;

	private boolean sticky = false;

	private boolean dependent = false;

	public VLVertexSkid(VLVertex2D vertex) {
		this.vertex = vertex;
	}

	public VLVertex2D getVertex() {
		return vertex;
	}

	public boolean isSticky() {
		return sticky;
	}

	public VLVertexSkid setSticky(final boolean sticky) {
		this.sticky = sticky;
		return this;
	}

	public boolean isDependent() {
		return dependent;
	}

	public VLVertexSkid setDependent(boolean dependent) {
		this.dependent = dependent;
		return this;
	}

	public VLVertexSkid dcopy() {
		return new VLVertexSkid(vertex.dcopy()).setSticky(sticky);
	}

	@Override
	public String toString() {
		return "VLVertexSkid [vertex=" + vertex + ", sticky=" + sticky + ", dependent=" + dependent + "]";
	}
}
