/**
 * 
 */
package de.zintel.sim.whirl;

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
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.MathUtils;
import de.zintel.sim.SimulationScreen;

/**
 * @author friedemann.zintel
 *
 */
public class WhirlSim extends SimulationScreen {

	private static class Particle {

		public final double velocity;

		public double angle = 0;

		public final Vector3D position;

		public final Vector3D initialPosition;

		public final Color color;

		private final double radius;

		public Particle(final double x, final double y, final double z, Color color) {

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

	private Set<Particle> particles = new LinkedHashSet<>();

	private Vector3D viewpoint = new Vector3D(950.0, 140.0, -1000.0);

	private Vector3D rotcenter = new Vector3D(0.0, 540.0, 200.0);

	private int frequency = 1;

	private final double finalBubbleRadius = 3D;

	private final double finalCircleRadius = 5.0;

	private double deltaxmin = -200;

	private double deltaxmax = 200;

	private double rotationTransitionLeft = -3;

	private double rotationTransitionRight = 4;

	private double particlesminy = 1;

	private double particlesmaxy = 0;

	private boolean showgrid = true;

	private EColorMixture colorMixture = EColorMixture.ADDITIVE;

	/**
	 * @param title
	 * @param gfxSsystem
	 * @param screenParameters
	 * @param doRecord
	 * @param recordFilename
	 * @param recordingRate
	 */
	public WhirlSim(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
		super(title, gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
		particlesmaxy = getGraphicsSubsystem().getDimension().getHeight();
		getGraphicsSubsystem().setColorMixture(colorMixture);
	}

	public static void main(String args[]) throws Exception {
		new WhirlSim("Testing", GFX_SSYSTEM, SCREENPARAMETERS, false, "", 0).start();
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

		if (showgrid) {
			// grid
			final double gridy = dimension.getHeight();
			for (int i = 0 + (int) deltaxmin; i < dimension.getWidth() + deltaxmax; i += 50) {
				graphicsSubsystem.drawLine((int) projectX(i, 0, viewpoint), (int) projectY(0, 0, viewpoint),
						(int) projectX(i, gridz, viewpoint), (int) projectY(0, gridz, viewpoint), adjustColor(gridcolor, 0),
						adjustColor(gridcolor, gridz));
				graphicsSubsystem.drawLine((int) projectX(i, 0, viewpoint), (int) projectY(gridy, 0, viewpoint),
						(int) projectX(i, gridz, viewpoint), (int) projectY(gridy, gridz, viewpoint), adjustColor(gridcolor, 0),
						adjustColor(gridcolor, gridz));
				graphicsSubsystem.drawLine((int) projectX(i, gridz, viewpoint), (int) projectY(0, gridz, viewpoint),
						(int) projectX(i, gridz, viewpoint), (int) projectY(gridy, gridz, viewpoint), adjustColor(gridcolor, gridz),
						adjustColor(gridcolor, gridz));
			}
		}

		for (Particle particle : particles) {

			final Vector3D point = particle.position;

			double x = projectX(point.x(), point.z(), viewpoint);
			double y = projectY(point.y(), point.z(), viewpoint);

			double rcx = projectX(point.x(), rotcenter.z(), viewpoint);

			double bubbleRadius = Math.abs(x - projectX(
					point.x() + MathUtils.morph(v -> particle.radius, v -> finalBubbleRadius,
							v -> MathUtils.sigmoid(
									MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -3, 4, particle.position.x())),
							rcx),
					point.z(), viewpoint));

			final Function<Double, Double> colortrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -7, 1.4, particle.position.x()));
			final Function<Double, Double> alphatrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -18, 1.4, particle.position.x()));
			graphicsSubsystem.drawFilledCircle((int) x, (int) y, (int) bubbleRadius,
					() -> CUtils.transparent(adjustColor(enlighten(particle.color, colortrans, particle.position.x()), point.z()),
							(int) MathUtils.morph(v -> (double) particle.color.getAlpha(), v -> 0D, alphatrans, particle.position.x())));
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

		Set<Particle> newparticles = new LinkedHashSet<>(particles);

		final Iterator<Particle> iterator = newparticles.iterator();
		while (iterator.hasNext()) {

			final Particle particle = iterator.next();
			final Function<Double, Double> rottrans = x -> MathUtils
					.sigmoid(MathUtils.morphRange(0, width, rotationTransitionLeft, rotationTransitionRight, particle.position.x()));

			particle.position.setX(particle.position.x()
					+ MathUtils.morph(v -> particle.velocity, v -> particle.velocity + 10, rottrans, particle.position.x()));

			if (particle.position.x() > width) {

				iterator.remove();
				continue;

			}

			final double cradius = MathUtils.morph(x -> Math.abs(rotcenter.y() - particle.initialPosition.y()), x -> finalCircleRadius,
					rottrans, particle.position.x());

			particle.angle += MathUtils.morph(x -> 0.000005, x -> 40D, rottrans, particle.position.x());

			particle.position.setZ(Math.sin(theta(particle.angle)) * cradius + rotcenter.z());
			particle.position.setY(
					rotcenter.y() + (particle.initialPosition.y() < rotcenter.y() ? -1 : 1) * Math.cos(theta(particle.angle)) * cradius);

		}

		if (validByFrequency(frequency)) {
			newparticles.add(new Particle(deltaxmin, MathUtils.makeRandom((int) particlesminy, (int) particlesmaxy), rotcenter.z(),
					CUtils.transparent(CUtils.makeRandomColor(), 200)));
		}

		particles = newparticles;
	}

	private boolean validByFrequency(final int frequency) {

		if (frequency > 0) {
			return MathUtils.RANDOM.nextInt(frequency) == frequency - 1;
		} else if (frequency < 0) {
			return MathUtils.RANDOM.nextInt(-frequency) != -frequency - 1;
		} else {
			return false;
		}

	}

	private double theta(final double degree) {
		return MathUtils.morphRange(0, 360, 0, 2 * Math.PI, ((int) degree) % 360);
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
				return "viewpoint x";
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
				return "viewpoint y";
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
				return "viewpoint z";
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
		addKeyAction(KeyEvent.VK_C, new IKeyAction() {

			private final int delta = 10;

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
				return "RC";
			}

			@Override
			public String text() {
				return "rotation center";
			}

			@Override
			public void plus() {
				rotcenter.setY(rotcenter.y() + delta);
			}

			@Override
			public void minus() {
				rotcenter.setY(rotcenter.y() - delta);
			}

			@Override
			public String getValue() {
				return String.valueOf(rotcenter.y());
			}
		});
		addKeyAction(KeyEvent.VK_L, new IKeyAction() {

			private final double delta = 0.1;

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
				return "RTL";
			}

			@Override
			public String text() {
				return "rotation transition left";
			}

			@Override
			public void plus() {
				rotationTransitionLeft += delta;
			}

			@Override
			public void minus() {
				rotationTransitionLeft -= delta;
			}

			@Override
			public String getValue() {
				return String.valueOf(rotationTransitionLeft);
			}
		});
		addKeyAction(KeyEvent.VK_R, new IKeyAction() {

			private final double delta = 0.1;

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
				return "RTR";
			}

			@Override
			public String text() {
				return "rotation transition right";
			}

			@Override
			public void plus() {
				rotationTransitionRight += delta;
			}

			@Override
			public void minus() {
				rotationTransitionRight -= delta;
			}

			@Override
			public String getValue() {
				return String.valueOf(rotationTransitionRight);
			}
		});
		addKeyAction(KeyEvent.VK_T, new IKeyAction() {

			private final int delta = 2;

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
				return "PMINY";
			}

			@Override
			public String text() {
				return "particles top";
			}

			@Override
			public void plus() {
				if (particlesminy + delta <= particlesmaxy) {
					particlesminy += delta;
				}
			}

			@Override
			public void minus() {
				particlesminy -= delta;
			}

			@Override
			public String getValue() {
				return String.valueOf(particlesminy);
			}
		});
		addKeyAction(KeyEvent.VK_B, new IKeyAction() {

			private final int delta = 2;

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
				return "PMAXY";
			}

			@Override
			public String text() {
				return "particles bottom";
			}

			@Override
			public void plus() {
				particlesmaxy += delta;
			}

			@Override
			public void minus() {
				if (particlesmaxy - delta >= particlesminy) {
					particlesmaxy -= delta;
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(particlesmaxy);
			}
		});
		addKeyAction(KeyEvent.VK_G, new IKeyAction() {

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
				return "GRID";
			}

			@Override
			public String text() {
				return "show grid";
			}

			@Override
			public void plus() {
				showgrid = true;
			}

			@Override
			public void minus() {
				showgrid = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(showgrid);
			}
		});
		addKeyAction(KeyEvent.VK_F, new IKeyAction() {

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
				return "FREQ";
			}

			@Override
			public String text() {
				return "frequency";
			}

			@Override
			public void plus() {
				frequency += 1;
			}

			@Override
			public void minus() {
				frequency -= 1;
			}

			@Override
			public String getValue() {
				return String.valueOf(frequency >= 0 ? (double) frequency : (1 / (double) -frequency));
			}
		});
		addKeyAction(KeyEvent.VK_M, new IKeyAction() {

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
				return "CMIX";
			}

			@Override
			public String text() {
				return "color mixture";
			}

			@Override
			public void plus() {
				colorMixture = colorMixture == EColorMixture.ADDITIVE ? EColorMixture.TRANSPARENT : EColorMixture.ADDITIVE;
				getGraphicsSubsystem().setColorMixture(colorMixture);
			}

			@Override
			public void minus() {
				colorMixture = colorMixture == EColorMixture.ADDITIVE ? EColorMixture.TRANSPARENT : EColorMixture.ADDITIVE;
				getGraphicsSubsystem().setColorMixture(colorMixture);
			}

			@Override
			public String getValue() {
				return String.valueOf(colorMixture.name());
			}
		});
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
}
