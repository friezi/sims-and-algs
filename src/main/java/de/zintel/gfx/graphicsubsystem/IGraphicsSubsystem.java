/**
 * 
 */
package de.zintel.gfx.graphicsubsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Collection;

import de.zintel.gfx.color.CUtils.ColorGenerator;
import de.zintel.gfx.g2d.Vector2D;

/**
 * @author Friedemann
 *
 */
public interface IGraphicsSubsystem {

	void init(boolean doRecord, String filename);

	boolean supportsColorChange();

	void drawFilledCircle(final int x, final int y, final int radius, final ColorGenerator colorGenerator);

	void drawFilledEllipse(final int x, final int y, final int radius, double ratioYX, double angle, final ColorGenerator colorGenerator);

	void drawLine(final int x1, final int y1, final int x2, final int y2, final Color color);

	void drawFilledTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3, final Color color);

	void drawFilledPolygon(final Collection<Vector2D> points, final Color color);

	void drawString(String str, final int x, final int y, final Color color);

	void setFont(Font font);

	void repaint();

	void setBackground(Color color);

	void setFullScreen();

	void addMouseWheelListener(MouseWheelListener listener);

	void addMouseMotionListener(MouseMotionListener listener);

	void addMouseListener(MouseListener listener);

	void addKeyListener(KeyListener listener);

	void removeMouseWheelListener(MouseWheelListener listener);

	void removeMouseMotionListener(MouseMotionListener listener);

	void removeMouseListener(MouseListener listener);

	void removeKeyListener(KeyListener listener);

	void addRendererListener(IRendererListener listener);

	void display();

	Dimension getDimension();

	void shutdown();

	void synchronize(boolean value);

}
