/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.List;

import de.zintel.gfx.IRenderable;

/**
 * @author friedemann.zintel
 *
 */
public interface IVLEdgeContainer2D extends IRenderable {

	List<VLEdge2D> getEdges();

	IVLEdgeContainer2D dcopy();

}
