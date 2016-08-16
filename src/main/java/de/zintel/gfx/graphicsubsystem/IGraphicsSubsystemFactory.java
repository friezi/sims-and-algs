/**
 * 
 */
package de.zintel.gfx.graphicsubsystem;

/**
 * @author friedo
 *
 */
public interface IGraphicsSubsystemFactory {

	IGraphicsSubsystem newGraphicsSubsystem(String title, int width, int height);

}
