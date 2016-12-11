/**
 * 
 */
package de.zintel.gfx.g2d;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author friedemann.zintel
 *
 */
public class EdgeContainer2D {

	private final Collection<Edge2D> edges = new LinkedList<>();

	public void addEdge(final Edge2D edge) {
		edges.add(edge);
	}

	public Collection<Edge2D> getEdges() {
		return edges;
	}

}
