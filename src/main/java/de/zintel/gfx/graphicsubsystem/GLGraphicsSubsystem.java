/**
 * 
 */
package de.zintel.gfx.graphicsubsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import javax.swing.JFrame;

import org.jcodec.api.awt.SequenceEncoder;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.awt.TextRenderer;

import de.zintel.gfx.gl.GLUtils;
import de.zintel.gfx.gl.GLUtils.CircleDrawer;
import de.zintel.math.Vector2D;

/**
 * @author Friedemann
 *
 */
public class GLGraphicsSubsystem implements IGraphicsSubsystem, GLEventListener, WindowListener {

	private static final boolean DOUBLE_BUFFERING = true;

	private class DisplayTask implements Runnable {

		public volatile boolean stopped = false;

		@Override
		public void run() {

			while (!stopped) {

				synchronized (renderThread) {
					try {

						renderThread.wait();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				canvas.display();
			}
		}
	}

	private String title;

	private Dimension dimension;

	private JFrame mainFrame;

	private GLCanvas canvas;

	private GLAutoDrawable autodrawable;

	private GL2 gl;

	private final Collection<IRendererListener> rendererListeners = new ArrayList<>();

	private Thread renderThread;

	private DisplayTask displayTask;

	private Color backgroundColor = Color.WHITE;

	private CircleDrawer circleDrawer = new CircleDrawer();

	private SequenceEncoder enc;

	private String recordFilename;

	private TextRenderer textRenderer = null;

	private boolean synchronizzed = false;

	public GLGraphicsSubsystem(String title, int width, int height) {
		this.title = title;
		this.dimension = new Dimension(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#init()
	 */
	@Override
	public void init(boolean doRecord, String filename) {

		recordSession(doRecord, filename);

		final GLCapabilities glCapabilities = new GLCapabilities(GLProfile.getDefault());

		if (DOUBLE_BUFFERING) {
			glCapabilities.setDoubleBuffered(true);
		}
		canvas = new GLCanvas(glCapabilities);

		mainFrame = new JFrame(title);
		mainFrame.setSize(dimension.width, dimension.height);
		mainFrame.setIgnoreRepaint(true);
		mainFrame.add(canvas);

		dimension.width = mainFrame.getWidth();
		dimension.height = mainFrame.getHeight();

		canvas.addGLEventListener(GLGraphicsSubsystem.this);

		if (doRecord) {

			try {
				enc = new SequenceEncoder(new File(recordFilename));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (!synchronizzed) {

			displayTask = new DisplayTask();
			renderThread = new Thread(displayTask);
			renderThread.start();

		}

		mainFrame.addWindowListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#drawFilledCircle(int, int,
	 * int, de.zintel.gfx.color.CUtils.ColorGenerator)
	 */
	@Override
	public void drawFilledCircle(int x, int y, int radius, Supplier<Color> colorGenerator) {
		circleDrawer.drawFilledEllipse(x, y, radius, colorGenerator, dimension, 1, 0, gl);
	}

	@Override
	public void drawFilledEllipse(int x, int y, int radius, double ratioYX, double angle, Supplier<Color> colorGenerator) {
		circleDrawer.drawFilledEllipse(x, y, radius, colorGenerator, dimension, ratioYX, angle, gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#drawLine(int, int, int,
	 * int, java.awt.Color)
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2, Color colorStart, Color colorEnd) {
		GLUtils.drawLine(x1, y1, x2, y2, colorStart, colorEnd, dimension, gl);

	}

	@Override
	public void drawFilledTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		GLUtils.drawFilledTriangle(x1, y1, x2, y2, x3, y3, color, dimension, gl);
	}

	@Override
	public void drawFilledPolygon(Collection<Vector2D> points, Supplier<Color> colorGenerator) {
		GLUtils.drawFilledPolygon(points, colorGenerator, dimension, gl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#repaint()
	 */
	@Override
	public void repaint() {

		if (synchronizzed) {

			canvas.display();

		} else {

			synchronized (renderThread) {
				renderThread.notify();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.nbodies.IGraphicsSubsystem#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color color) {
		backgroundColor = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#setFullScreen()
	 */
	@Override
	public void setFullScreen() {
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainFrame.setUndecorated(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.nbodies.IGraphicsSubsystem#addMouseWheelListener(java.awt.
	 * event.MouseWheelListener)
	 */
	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		canvas.addMouseWheelListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.nbodies.IGraphicsSubsystem#addMouseListener(java.awt.event.
	 * MouseListener)
	 */
	@Override
	public void addMouseListener(MouseListener listener) {
		canvas.addMouseListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.nbodies.IGraphicsSubsystem#addKeyListener(java.awt.event.
	 * KeyListener)
	 */
	@Override
	public void addKeyListener(KeyListener listener) {
		canvas.addKeyListener(listener);
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		canvas.addMouseMotionListener(listener);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		canvas.removeMouseWheelListener(listener);
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener listener) {
		canvas.removeMouseMotionListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		canvas.removeMouseListener(listener);
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		canvas.removeKeyListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.nbodies.IGraphicsSubsystem#addRendererListener(de.zintel.
	 * sim.nbodies.IRendererListener)
	 */
	@Override
	public void addRendererListener(IRendererListener listener) {
		rendererListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.nbodies.IGraphicsSubsystem#display()
	 */
	@Override
	public void display() {
		mainFrame.setVisible(true);
		mainFrame.requestFocus();
		canvas.requestFocus();
		dimension.width = mainFrame.getWidth();
		dimension.height = mainFrame.getHeight();
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		autodrawable = drawable;
		gl = drawable.getGL().getGL2();

		gl.glClearColor(GLUtils.projectColorValue2GL(backgroundColor.getRed()), GLUtils.projectColorValue2GL(backgroundColor.getGreen()),
				GLUtils.projectColorValue2GL(backgroundColor.getBlue()), GLUtils.projectColorValue2GL(backgroundColor.getAlpha()));

		// clear background
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// enable transparency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		for (IRendererListener renderer : rendererListeners) {
			renderer.render(this);
		}

		if (enc != null) {
			record();
		}

		if (textRenderer != null) {

			textRenderer.endRendering();
			textRenderer = null;

		}

		if (DOUBLE_BUFFERING) {

			gl.glFlush();
			// drawable.swapBuffers(); // not necessary with GLCanvas since
			// autoSwapBuffers==true

		}

	}

	private void record() {

		final AWTGLReadBufferUtil awtglReadBufferUtil = new AWTGLReadBufferUtil(gl.getGLProfile(), false);
		final BufferedImage bufferedImage = awtglReadBufferUtil.readPixelsToBufferedImage(gl, true);

		try {
			enc.encodeImage(bufferedImage);
		} catch (IOException e) {
			e.printStackTrace();
			enc = null;
		}

	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void init(GLAutoDrawable arg0) {
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

	}

	@Override
	public void shutdown() {

		if (displayTask != null) {
			displayTask.stopped = true;
		}

		if (enc != null) {
			try {
				enc.finish();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		shutdown();

		System.exit(0);

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

	}

	private void recordSession(boolean doRecord, String filename) {

		this.recordFilename = filename;
		if (doRecord) {
			this.synchronizzed = true;
		}

	}

	@Override
	public void drawString(String str, int x, int y, Color color) {

		textRenderer.setColor(color);
		textRenderer.draw(str, x, dimension.height - y);

	}

	@Override
	public void setFont(Font font) {

		if (textRenderer != null) {
			return;
		}

		textRenderer = new TextRenderer(font);
		textRenderer.beginRendering(autodrawable.getSurfaceWidth(), autodrawable.getSurfaceHeight());

	}

	@Override
	public boolean supportsColorChange() {
		return true;
	}

	@Override
	public void synchronize(boolean value) {
		this.synchronizzed = value;
	}

}
