/**
 * 
 */
package de.zintel.gfx.g2d;

/**
 * @author friedemann.zintel
 *
 */
public class Cuboid2D extends EdgeContainer2D {

	/**
	 * 
	 */
	public Cuboid2D(Vertex2D v1, Vertex2D v2, Vertex2D v3, Vertex2D v4) {
		addEdge(new Edge2D(v1, v2));
		addEdge(new Edge2D(v2, v3));
		addEdge(new Edge2D(v3, v4));
		addEdge(new Edge2D(v4, v1));
		addEdge(new Edge2D(v1, v3));
		addEdge(new Edge2D(v2, v4));
	}

}
