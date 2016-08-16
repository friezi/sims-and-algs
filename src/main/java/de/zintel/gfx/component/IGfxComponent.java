package de.zintel.gfx.component;

import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;

/**
 * 
 */

/**
 * @author Friedemann
 *
 */
public interface IGfxComponent {

	void draw(IGraphicsSubsystem graphicsSubsystem);

	void stop();

	GfxState getState();

}
