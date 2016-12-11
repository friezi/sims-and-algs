/**
 * 
 */
package de.zintel.gfx.g2d;

import java.util.Collection;

import de.zintel.math.Utils;

/**
 * @author friedemann.zintel
 *
 */
public class Chain2D extends EdgeContainer2D {

	/**
	 * 
	 */
	public Chain2D(Collection<Vertex2D> vertices) {

		Vertex2D previous = null;
		for (Vertex2D vertex : vertices) {
			if (previous != null) {
				addEdge(new Edge2D(previous, vertex));
			}
			previous = vertex;
		}
	}

	public Chain2D(Vertex2D first, Vertex2D last, int elements) {

		Vertex2D previous = first;
		for (int i = 2; i < elements; i++) {

			final Vertex2D current = new Vertex2D(
					new Vector2D(Utils.interpolateLinearReal(first.getCurrent().x, last.getCurrent().x, i, elements),
							Utils.interpolateLinearReal(first.getCurrent().y, last.getCurrent().y, i, elements)));
			addEdge(new Edge2D(previous, current));
			previous = current;

		}

		addEdge(new Edge2D(previous, last));

	}

}
