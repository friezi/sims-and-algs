/**
 * 
 */
package de.zintel.gfx.graphicsubsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.zintel.gfx.color.EColorMixture;
import de.zintel.math.Vector2DPlain;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class SwingGraphicsSubsystem implements IGraphicsSubsystem {

	private class GfxPanel extends JPanel {

		@Override
		protected void paintComponent(Graphics graphics) {

			super.paintComponent(graphics);
			SwingGraphicsSubsystem.this.graphics = graphics;

			for (IRendererListener renderer : rendererListeners) {
				renderer.render(SwingGraphicsSubsystem.this);
			}

		}

	}

	private JFrame mainFrame;

	private JPanel gfxPanel;

	private String title;

	private Dimension dimension;

	private Graphics graphics;

	private final Collection<IRendererListener> rendererListeners = new ArrayList<>();

	public SwingGraphicsSubsystem(String title, int width, int height) {
		this.title = title;
		this.dimension = new Dimension(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#init()
	 */
	@Override
	public void init(boolean doecord, String filename) {

		mainFrame = new JFrame(title);
		mainFrame.setSize(dimension.width, dimension.height);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setIgnoreRepaint(true);

		gfxPanel = new GfxPanel();
		gfxPanel.setLayout(new BoxLayout(gfxPanel, BoxLayout.Y_AXIS));
		gfxPanel.setOpaque(true);
		mainFrame.setContentPane(gfxPanel);

		dimension = new Dimension(mainFrame.getWidth(), mainFrame.getHeight());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#drawFilledCircle(int, int, int,
	 * java.awt.Color)
	 */
	@Override
	public void drawFilledCircle(int x, int y, int radius, Supplier<Color> colorGenerator) {
		graphics.setColor(colorGenerator.get());
		graphics.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	@Override
	public void drawFilledEllipse(int x, int y, int radius, double ratioYX, double angle, Supplier<Color> colorGenerator) {
		drawFilledCircle(x, y, radius, colorGenerator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#drawLine(int, int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2, Color color, Color colorEnd) {
		graphics.setColor(color);
		graphics.drawLine(x1, y1, x2, y2);

	}

	@Override
	public void drawString(String str, int x, int y, Color color) {
		graphics.setColor(color);
		graphics.drawString(str, x, y);
	}

	@Override
	public void setFont(Font font) {
		graphics.setFont(font);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#display()
	 */
	@Override
	public void display() {
		mainFrame.setVisible(true);
		dimension = new Dimension(mainFrame.getWidth(), mainFrame.getHeight());
	}

	@Override
	public void repaint() {
		if (mainFrame != null) {
			mainFrame.repaint();
		}
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public void setFullScreen() {
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.setUndecorated(true);
	}

	@Override
	public void addRendererListener(IRendererListener listener) {
		rendererListeners.add(listener);
	}

	@Override
	public void shutdown() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem#setBackground(java.awt.
	 * Color)
	 */
	@Override
	public void setBackground(Color color) {
		gfxPanel.setBackground(color);
	}

	@Override
	public synchronized void addKeyListener(KeyListener listener) {
		mainFrame.addKeyListener(listener);
	}

	@Override
	public synchronized void addMouseWheelListener(MouseWheelListener listener) {
		mainFrame.addMouseWheelListener(listener);
	}

	@Override
	public synchronized void addMouseListener(MouseListener listener) {
		mainFrame.addMouseListener(listener);
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		mainFrame.addMouseMotionListener(listener);

	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		mainFrame.removeMouseWheelListener(listener);

	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener listener) {
		mainFrame.removeMouseMotionListener(listener);

	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		mainFrame.removeMouseListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		mainFrame.removeKeyListener(listener);
	}

	@Override
	public boolean supportsColorChange() {
		return false;
	}

	@Override
	public void synchronize(boolean value) {
		// not supported
	}

	@Override
	public void drawFilledTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {

		Collection<Vector2DPlain> points = new ArrayList<>(3);
		points.add(new Vector2DPlain(x1, y1));
		points.add(new Vector2DPlain(x2, y2));
		points.add(new Vector2DPlain(x3, y3));

		drawFilledPolygon(points, color);

	}

	@Override
	public void drawFilledPolygon(Collection<Vector2DPlain> points, Supplier<Color> colorGenerator) {
		graphics.setColor(colorGenerator.get());

		final int x[] = new int[points.size()];
		final int y[] = new int[points.size()];
		int i = 0;
		for (Vector2DPlain point : points) {
			x[i] = (int) point.x;
			y[i] = (int) point.y;
			i++;
		}

		graphics.drawPolygon(x, y, points.size());
	}

	@Override
	public void setColorMixture(EColorMixture colorMixture) {
		// not supported
	}

}
