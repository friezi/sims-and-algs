/**
 * 
 */
package de.zintel.gfx.g3d.renderer;

import de.zintel.camera.ICamera3D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;

/**
 * @author friedemann.zintel
 *
 */
public interface IRenderer {

	/**
	 * for rendering on a certain camera
	 * 
	 * @param graphicsSubsystem
	 * @param camera
	 */
	void render(IGraphicsSubsystem graphicsSubsystem, ICamera3D camera);

}
