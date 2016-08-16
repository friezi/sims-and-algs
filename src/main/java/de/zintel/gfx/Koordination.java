/**
 * 
 */
package de.zintel.gfx;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * @author Friedemann
 *
 */
public final class Koordination {

	public final int WIDTH /* = 1920 */;
	public final int HEIGHT /* = 1080 */;
	public final int XNULL = 30;
	public final int YNULL = 100;
	public final int ZNULL = 5;
	public final int RENDER_STARTY = 200;
	public final int RENDER_MAX_RAND_X;
	public final int RENDER_MAX_RAND_Y;

	public Koordination() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.WIDTH = gd.getDisplayMode().getWidth();
		this.HEIGHT = gd.getDisplayMode().getHeight();
		this.RENDER_MAX_RAND_X = WIDTH - 20;
		this.RENDER_MAX_RAND_Y = HEIGHT - RENDER_STARTY - 20;
	}

}
