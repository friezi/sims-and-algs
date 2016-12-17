/**
 * 
 */
package de.zintel.gfx.graphicsubsystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.zintel.gfx.color.CUtils.ColorGenerator;

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
				renderer.render();
			}

		}

	}

	private JFrame mainFrame;

	private JPanel gfxPanel;

	private String title;

	private int width;

	private int height;

	private Graphics graphics;

	private final Collection<IRendererListener> rendererListeners = new ArrayList<>();

	public SwingGraphicsSubsystem(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#init()
	 */
	@Override
	public void init() {

		mainFrame = new JFrame(title);
		mainFrame.setSize(width, height);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setIgnoreRepaint(true);

		gfxPanel = new GfxPanel();
		gfxPanel.setLayout(new BoxLayout(gfxPanel, BoxLayout.Y_AXIS));
		gfxPanel.setOpaque(true);
		mainFrame.setContentPane(gfxPanel);

		width = mainFrame.getWidth();
		height = mainFrame.getHeight();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#drawFilledCircle(int, int, int,
	 * java.awt.Color)
	 */
	@Override
	public void drawFilledCircle(int x, int y, int radius, ColorGenerator colorGenerator) {
		graphics.setColor(colorGenerator.generateColor());
		graphics.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphics#drawLine(int, int, int, int, int)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2, Color color) {
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
		width = mainFrame.getWidth();
		height = mainFrame.getHeight();
	}

	@Override
	public void repaint() {
		if (mainFrame != null) {
			mainFrame.repaint();
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
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

	@Override
	public void recordSession(boolean doecord, String filename) {

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
	public void drawFilledEllipse(int x, int y, int radius, double ratioYX, float angle, ColorGenerator colorGenerator) {
		// not supported
	}

}
