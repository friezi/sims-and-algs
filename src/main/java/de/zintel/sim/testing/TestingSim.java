/**
 * 
 */
package de.zintel.sim.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.MathUtils;
import de.zintel.sim.SimulationScreen;

/**
 * @author friedemann.zintel
 *
 */
public class TestingSim extends SimulationScreen {

	private static class Bubble {

		public final double velocity;

		public double angle = 0;

		public final Vector3D position;

		public final Vector3D initialPosition;

		public final Color color;

		private final double radius;

		public Bubble(final double x, final double y, final double z, Color color) {
			this.position = new Vector3D(x, y, z);
			this.initialPosition = new Vector3D(x, y, z);
			this.color = color;
			this.velocity = MathUtils.makeRandom(2, 7);
			this.radius = MathUtils.makeRandom(3, 9);

		}

	}

	private static final Color gridcolor = new Color(200, 0, 0);

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 20);

	private static final double VP_STEP = 10;

	private Set<Bubble> bubbles = new LinkedHashSet<>();

	private Vector3D viewpoint = new Vector3D(950.0, 140.0, -1000.0);

	private Vector3D rotcenter = new Vector3D(0.0, 540.0, 200.0);

	private final int frequency = 1;

	private final double finalBubbleRadius = 3D;

	private final double finalCircleRadius = 5.0;

	private final double deltaxmin = -200;

	private final double deltaxmax = 200;

	/**
	 * @param title
	 * @param gfxSsystem
	 * @param screenParameters
	 * @param doRecord
	 * @param recordFilename
	 * @param recordingRate
	 */
	public TestingSim(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
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

		final double gridz = 1500;
		final Dimension dimension = graphicsSubsystem.getDimension();

		// grid
		final double gridy = dimension.getHeight();
		for (int i = 0 + (int) deltaxmin; i < dimension.getWidth() + deltaxmax; i += 50) {
			graphicsSubsystem.drawLine((int) projectX(i, 0, viewpoint), (int) projectY(0, 0, viewpoint), (int) projectX(i, gridz, viewpoint),
					(int) projectY(0, gridz, viewpoint), adjustColor(gridcolor, 0), adjustColor(gridcolor, gridz));
			graphicsSubsystem.drawLine((int) projectX(i, 0, viewpoint), (int) projectY(gridy, 0, viewpoint), (int) projectX(i, gridz, viewpoint),
					(int) projectY(gridy, gridz, viewpoint), adjustColor(gridcolor, 0), adjustColor(gridcolor, gridz));
			graphicsSubsystem.drawLine((int) projectX(i, gridz, viewpoint), (int) projectY(0, gridz, viewpoint), (int) projectX(i, gridz, viewpoint),
					(int) projectY(gridy, gridz, viewpoint), adjustColor(gridcolor, gridz), adjustColor(gridcolor, gridz));
		}

		for (Bubble bubble : bubbles) {

			final Vector3D point = bubble.position;

			double x = projectX(point.x(), point.z(), viewpoint);
			double y = projectY(point.y(), point.z(), viewpoint);

			double rcx = projectX(point.x(), rotcenter.z(), viewpoint);

			double bubbleRadius = Math.abs(x - projectX(point.x() + MathUtils.morph(v -> bubble.radius, v -> finalBubbleRadius,
					v -> MathUtils.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -3, 4, bubble.position.x())), rcx),
					point.z(), viewpoint));

			final Function<Double, Double> colortrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -7, 1.4, bubble.position.x()));
			final Function<Double, Double> alphatrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -18, 1.4, bubble.position.x()));
			graphicsSubsystem.drawFilledCircle((int) x, (int) y, (int) bubbleRadius,
					() -> CUtils.transparent(adjustColor(enlighten(bubble.color, colortrans, bubble.position.x()), point.z()),
							(int) MathUtils.morph(v -> (double) bubble.color.getAlpha(), v -> 0D, alphatrans, bubble.position.x())));
		}
	}

	private Color enlighten(final Color color, final Function<Double, Double> ftrans, final double value) {
		return new Color((int) MathUtils.morph(x -> (double) color.getRed(), x -> 255D, ftrans, value),
				(int) MathUtils.morph(x -> (double) color.getGreen(), x -> 255D, ftrans, value),
				(int) MathUtils.morph(x -> (double) color.getBlue(), x -> 255D, ftrans, value), color.getAlpha());
	}

	private Color adjustColor(final Color color, final double z) {

		if (z > rotcenter.z()) {

			float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			final float brightness = (float) (hsb[2] * rotcenter.z() / (2 * z));
			return Color.getHSBColor(hsb[0], hsb[1], brightness);

		} else {
			return color;
		}
	}

	private double projectX(final double x, final double z, final Vector3D vp) {
		return projectXY(x, z, vp, Vector3D::x);
	}

	private double projectY(final double y, final double z, final Vector3D vp) {
		return projectXY(y, z, vp, Vector3D::y);
	}

	private double projectXY(final double xy, final double z, final Vector3D vp, Function<Vector3D, Double> vpxy) {

		double diffZ = vp.z() - z;
		if (diffZ == 0) {
			return 0;
		}
		return (xy * vp.z() - z * vpxy.apply(vp)) / diffZ;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#calculate(java.awt.Dimension)
	 */
	@Override
	protected void calculate(Dimension dimension) throws Exception {

		final double width = dimension.getWidth() + deltaxmax;
		final double height = dimension.getHeight();

		Set<Bubble> newbubbles = new LinkedHashSet<>(bubbles);
		final Iterator<Bubble> iterator = newbubbles.iterator();
		while (iterator.hasNext()) {

			final Bubble bubble = iterator.next();
			final Function<Double, Double> rottrans = x -> MathUtils.sigmoid(MathUtils.morphRange(0, width, -3, 4, bubble.position.x()));

			bubble.position
					.setX(bubble.position.x() + MathUtils.morph(v -> bubble.velocity, v -> bubble.velocity + 10, rottrans, bubble.position.x()));

			if (bubble.position.x() > width) {

				iterator.remove();
				continue;

			}

			final double cradius = MathUtils.morph(x -> Math.abs(rotcenter.y() - bubble.initialPosition.y()), x -> finalCircleRadius, rottrans,
					bubble.position.x());

			bubble.angle += MathUtils.morph(x -> 0.000005, x -> 40D, rottrans, bubble.position.x());

			bubble.position.setZ(Math.sin(theta(bubble.angle)) * cradius + rotcenter.z());
			bubble.position.setY(rotcenter.y() + (bubble.initialPosition.y() < rotcenter.y() ? -1 : 1) * Math.cos(theta(bubble.angle)) * cradius);

		}

		if (MathUtils.RANDOM.nextInt(frequency) == frequency - 1) {
			newbubbles.add(
					new Bubble(deltaxmin, MathUtils.makeRandom(1, (int) height), rotcenter.z(), CUtils.transparent(CUtils.makeRandomColor(), 200)));
		}

		bubbles = newbubbles;
	}

	private double theta(final double degree) {
		return MathUtils.morphRange(0, 360, 0, 2 * Math.PI, ((int) degree) % 360);
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
		addKeyAction(KeyEvent.VK_X, new IKeyAction() {

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
				return "VPX";
			}

			@Override
			public String text() {
				return "Viewpoint x";
			}

			@Override
			public void plus() {
				viewpoint.setX(viewpoint.x() + VP_STEP);
			}

			@Override
			public void minus() {
				viewpoint.setX(viewpoint.x() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(viewpoint.x());
			}
		});
		addKeyAction(KeyEvent.VK_Y, new IKeyAction() {

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
				return "VPY";
			}

			@Override
			public String text() {
				return "Viewpoint y";
			}

			@Override
			public void plus() {
				viewpoint.setY(viewpoint.y() + VP_STEP);
			}

			@Override
			public void minus() {
				viewpoint.setY(viewpoint.y() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(viewpoint.y());
			}
		});
		addKeyAction(KeyEvent.VK_Z, new IKeyAction() {

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
				return "VPZ";
			}

			@Override
			public String text() {
				return "Viewpoint z";
			}

			@Override
			public void plus() {
				if (viewpoint.z() + VP_STEP < 0) {
					viewpoint.setZ(viewpoint.z() + VP_STEP);
				}
			}

			@Override
			public void minus() {
				viewpoint.setZ(viewpoint.z() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(viewpoint.z());
			}
		});
	}
}
