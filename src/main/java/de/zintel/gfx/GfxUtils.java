/**
 * 
 */
package de.zintel.gfx;

import java.util.HashMap;
import java.util.Map;

import de.zintel.gfx.graphicsubsystem.GLGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.SwingGraphicsSubsystem;

/**
 * @author friedo
 *
 */
public final class GfxUtils {

	public static enum EGraphicsSubsystem {
		SWING, GL
	}

	@SuppressWarnings("serial")
	public static final Map<EGraphicsSubsystem, IGraphicsSubsystemFactory> graphicsSubsystemFactories = new HashMap<EGraphicsSubsystem, IGraphicsSubsystemFactory>() {
		{
			put(EGraphicsSubsystem.SWING, new IGraphicsSubsystemFactory() {

				@Override
				public IGraphicsSubsystem newGraphicsSubsystem(String title, int width, int height) {
					return new SwingGraphicsSubsystem(title, width, height);
				}
			});
			put(EGraphicsSubsystem.GL, new IGraphicsSubsystemFactory() {

				@Override
				public IGraphicsSubsystem newGraphicsSubsystem(String title, int width, int height) {
					return new GLGraphicsSubsystem(title, width, height);
				}
			});
		}
	};

	/**
	 * 
	 */
	private GfxUtils() {
	}

}
