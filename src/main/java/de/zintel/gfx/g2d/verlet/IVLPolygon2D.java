/**
 * 
 */
package de.zintel.gfx.g2d.verlet;

import java.util.Collection;

/**
 * @author friedemann.zintel
 *
 */
public interface IVLPolygon2D extends IVLEdgeContainer2D {

	Collection<VLVertexSkid> getVertices();

}
