/**
 * 
 */
package de.zintel.gfx.g2d;

import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public interface IEdgeContainer2D extends IRenderable {

	List<Edge2D> getEdges();

	IEdgeContainer2D dcopy();

}
