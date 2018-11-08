/**
 * 
 */
package de.zintel.sim.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.sim.SimulationScreen;

/**
 * @author friedemann.zintel
 *
 */
public class TestingSim extends SimulationScreen {

	private static final Color bubblecolor = new Color(200, 0, 0);

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final double VP_STEP = 10;

	private double cnt = 0;

	private Vector3D point = new Vector3D(Arrays.asList(0.0, 540.0, 0.0));

	private Vector3D vp = new Vector3D(Arrays.asList(950.0, 140.0, -1000.0));

	private Vector3D rc = new Vector3D(Arrays.asList(0.0, 540.0, 200.0));

	private double radius = 20;

	/**
	 * @param title
	 * @param gfxSsystem
	 * @param screenParameters
	 * @param doRecord
	 * @param recordFilename
	 * @param recordingRate
	 */
	public TestingSim(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord,
			String recordFilename, int recordingRate) {
		super(title, gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws Exception {
		new TestingSim("Testing", GFX_SSYSTEM, SCREENPARAMETERS, false, "", 0).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
	 * MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
	 * MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#init(de.zintel.gfx.graphicsubsystem.
	 * IGraphicsSubsystem)
	 */
	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		System.out.println("initialising ...");

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		initKeyActions();

		System.out.println("initialised");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.sim.SimulationScreen#renderSim(de.zintel.gfx.graphicsubsystem.
	 * IGraphicsSubsystem)
	 */
	@Override
	protected void renderSim(IGraphicsSubsystem graphicsSubsystem) {

		final double meshz = 1500;
		final double meshy = graphicsSubsystem.getDimension().getHeight();
		for (int i = 0; i < graphicsSubsystem.getDimension().getWidth(); i += 50) {
			graphicsSubsystem.drawLine((int) projectX(i, 0, vp), (int) projectY(meshy, 0, vp), (int) projectX(i, meshz, vp),
					(int) projectY(meshy, meshz, vp), bubblecolor, adjustColor(bubblecolor, meshz));
		}

		double x = projectX(point.x(), point.z(), vp);
		double y = projectY(point.y(), point.z(), vp);

		double rcx = projectX(point.x(), rc.z(), vp);
		double rcy = projectY(rc.y(), rc.z(), vp);

		graphicsSubsystem.drawLine((int) x, (int) y, (int) rcx, (int) rcy, adjustColor(Color.GREEN, point.z()), Color.GREEN);

		double rad = Math.abs(x - projectX(point.x() + radius, point.z(), vp));

		if (rc.z() > point.z()) {
			graphicsSubsystem.drawLine((int) projectX(0, rc.z(), vp), (int) rcy,
					(int) projectX(graphicsSubsystem.getDimension().getWidth(), rc.z(), vp), (int) rcy, Color.YELLOW, Color.YELLOW);
			graphicsSubsystem.drawFilledCircle((int) x, (int) y, (int) rad, () -> adjustColor(bubblecolor, point.z()));
		} else {
			graphicsSubsystem.drawFilledCircle((int) x, (int) y, (int) rad, () -> adjustColor(bubblecolor, point.z()));
			graphicsSubsystem.drawLine((int) projectX(0, rc.z(), vp), (int) rcy,
					(int) projectX(graphicsSubsystem.getDimension().getWidth(), rc.z(), vp), (int) rcy, Color.YELLOW, Color.YELLOW);

		}
	}

	private Color adjustColor(final Color color, final double z) {

		if (z > rc.z()) {

			float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			final float h = (float) (hsb[2] * (1 - Math.tanh(z - rc.z()) / 2));
			return Color.getHSBColor(hsb[0], hsb[1], h);

		} else {
			return color;
		}
	}

	private double projectX(final double x, final double z, final Vector3D vp) {

		double diffZ = vp.z() - z;
		if (diffZ == 0) {
			return 0;
		}
		return (x * vp.z() - z * vp.x()) / diffZ;

	}

	private double projectY(final double y, final double z, final Vector3D vp) {

		double diffZ = vp.z() - z;
		if (diffZ == 0) {
			return 0;
		}
		return (y * vp.z() - z * vp.y()) / diffZ;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#calculate(java.awt.Dimension)
	 */
	@Override
	protected void calculate(Dimension dimension) throws Exception {

		cnt += 1;

		point.setX(cnt % dimension.getWidth());

		final double cradius = 100;
		point.setZ(Math.sin(theta(cnt)) * cradius + rc.z());
		point.setY(rc.y() - Math.cos(theta(cnt)) * cradius);
	}

	private double theta(final double x) {

		final int steps = 45;

		return 2 * Math.PI * ((double) (((int) x) % steps)) / steps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#shutdown()
	 */
	@Override
	protected void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

	private void initKeyActions() {
		addKeyAction(KeyEvent.VK_V, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}

			@Override
			public String textID() {
				return "VP";
			}

			@Override
			public String text() {
				return "Viewpoint";
			}

			@Override
			public void plus() {
				if (vp.z() + VP_STEP < 0) {
					vp.setZ(vp.z() + VP_STEP);
				}
			}

			@Override
			public void minus() {
				vp.setZ(vp.z() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(vp.z());
			}
		});
	}
}
