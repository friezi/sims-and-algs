/**
 * 
 */
package de.zintel.sim.whirl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
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
import de.zintel.math.AVectorND;
import de.zintel.math.ICamera3D;
import de.zintel.math.MathUtils;
import de.zintel.math.PlaneCamera3D;
import de.zintel.math.SphereCamera3D;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.sim.SimulationScreen;

/**
 * @author friedemann.zintel
 *
 */
public class WhirlSim extends SimulationScreen {

	private static enum GridType {
		SIMPLE, COMPLEX
	}

	private static class Particle {

		public final double velocity;

		public double angle = 0;

		public final Vector3D position;

		public final Vector3D initialPosition;

		public final Color color;

		private final double radius;

		public Particle(final double x, final double y, final double z, Color color, double velocity, double radius) {

			this.position = new Vector3D(x, y, z);
			this.initialPosition = new Vector3D(x, y, z);
			this.color = color;
			this.velocity = velocity;
			this.radius = radius;

		}

	}

	private static final Color gridcolor = new Color(200, 0, 0);

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 20);

	private static final double VP_STEP = 10;

	private Set<Particle> particles = new LinkedHashSet<>();

	private ICamera3D camera;

	private Vector3D rotcenter = new Vector3D(0.0, 540.0, 200.0);

	private int frequency = 1;

	private final double finalBubbleRadius = 3D;

	private final double finalCircleRadius = 1.0;

	private double deltaxmin = -200;

	private double deltaxmax = 200;

	private double rotationTransitionLeft = -3;

	private double rotationTransitionRight = 4;

	private double particlesminy = 1;

	private double particlesmaxy = 0;

	private boolean showgrid = true;

	private EColorMixture colorMixture = EColorMixture.ADDITIVE;

	private GridType gridType = GridType.SIMPLE;

	private int minParticleRadius = 3;

	private int maxParticleRadius = 9;

	private int rotationspeed = 0;

	private boolean dynamicColoring = false;

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

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		graphicsSubsystem.setColorMixture(colorMixture);
		initKeyActions();

		camera = new PlaneCamera3D(new Vector3D((graphicsSubsystem.getDimension().getWidth() - 1) / 2,
				(graphicsSubsystem.getDimension().getHeight() - 1) / 2, -1000.0), new CoordinateTransformation3D(), 0,
				graphicsSubsystem.getDimension());
		// camera = new SphereCamera3D(new Vector3D(950.0, 140.0, -1000.0), new
		// CoordinateTransformation3D(), 5000000,
		// graphicsSubsystem.getDimension());

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

		final Dimension dimension = graphicsSubsystem.getDimension();

		if (showgrid) {
			if (gridType == GridType.SIMPLE) {
				drawSimpleGrid(graphicsSubsystem);
			} else {
				drawComplexGrid(graphicsSubsystem);
			}
		}

		for (Particle particle : particles) {

			final Vector3D point = particle.position;

			final Vector3D ppoint = project(point);
			if (ppoint == null) {
				continue;
			}

			double px = ppoint.x();
			double py = ppoint.y();

			/*
			 * while traversing forward around the rotation-axis, the
			 * bubble-size should decrease to a fixed minimum
			 * (finalBubbleRadius), independent from the initial size. After
			 * having calculated the new dynamic radius, we determine a
			 * rim-point of the bubble, to be capable of determining the correct
			 * projected radius. For this we choose a vector of the length of
			 * the radius in direction to the x-axis on the camera plane and
			 * retransform it into the global coordinate system. Now we can add
			 * the vector to the bubble to get the rim-point. By projecting it
			 * to the plane we can determine the effective (shown) radius.
			 */
			final double dynamicRadius = calculateDynamicRadius(particle, dimension);
			int pradius = (int) projectRadius(point, ppoint, dynamicRadius);
			if (pradius == 0) {
				continue;
			}

			final Function<Double, Double> colortrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -7, 1.4, point.x()));
			final Function<Double, Double> alphatrans = v -> MathUtils
					.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -18, 1.4, point.x()));

			if (camera.inRange(ppoint)) {
				graphicsSubsystem.drawFilledCircle((int) px, (int) py, pradius,
						() -> CUtils.transparent(adjustColor(CUtils.morphColor(particle.color, Color.YELLOW, colortrans, point.x()), point),
								(int) MathUtils.morph(v -> (double) particle.color.getAlpha(), v -> 0D, alphatrans, point.x())));
			}
		}
	}

	private void drawSimpleGrid(IGraphicsSubsystem graphicsSubsystem) {

		final Dimension dimension = graphicsSubsystem.getDimension();
		final double gridz = 1500;

		final double gridy = dimension.getHeight();
		for (int i = 0 + (int) deltaxmin; i < dimension.getWidth() + deltaxmax; i += 50) {

			final Vector3D topStart = new Vector3D(i, 0, 0);
			final Vector3D p_topStart = project(topStart);
			final Vector3D topEnd = new Vector3D(i, 0, gridz);
			final Vector3D p_topEnd = project(topEnd);
			if (p_topStart != null && p_topEnd != null) {
				graphicsSubsystem.drawLine((int) p_topStart.x(), (int) p_topStart.y(), (int) p_topEnd.x(), (int) p_topEnd.y(),
						adjustColor(gridcolor, topStart), adjustColor(gridcolor, topEnd));
			}

			final Vector3D bottomStart = new Vector3D(i, gridy, 0);
			final Vector3D p_bottomStart = project(bottomStart);
			final Vector3D bottomEnd = new Vector3D(i, gridy, gridz);
			final Vector3D p_bottomEnd = project(bottomEnd);
			if (p_bottomStart != null && p_bottomEnd != null) {
				graphicsSubsystem.drawLine((int) p_bottomStart.x(), (int) p_bottomStart.y(), (int) p_bottomEnd.x(), (int) p_bottomEnd.y(),
						adjustColor(gridcolor, bottomStart), adjustColor(gridcolor, bottomEnd));
			}

			final Vector3D backStart = new Vector3D(i, 0, gridz);
			final Vector3D p_backStart = project(backStart);
			final Vector3D backEnd = new Vector3D(i, gridy, gridz);
			final Vector3D p_backEnd = project(backEnd);
			if (p_backStart != null && p_backEnd != null) {
				graphicsSubsystem.drawLine((int) p_backStart.x(), (int) p_backStart.y(), (int) p_backEnd.x(), (int) p_backEnd.y(),
						adjustColor(gridcolor, backStart), adjustColor(gridcolor, backEnd));
			}
		}

	}

	private void drawComplexGrid(IGraphicsSubsystem graphicsSubsystem) {

		final Dimension dimension = graphicsSubsystem.getDimension();

		final int step = 100;
		for (int z = 1500; z >= 0; z -= step) {
			for (int y = 0; y < dimension.getHeight(); y += step) {
				for (int x = 0; x < dimension.getWidth(); x += step) {

					final Vector3D point = new Vector3D(x, y, z);
					final Vector3D znpoint = new Vector3D(point.x(), point.y(), point.z() - step);
					final Vector3D xnpoint = new Vector3D(point.x() + step, point.y(), point.z());
					final Vector3D ynpoint = new Vector3D(point.x(), point.y() + step, point.z());
					final Vector3D ppoint = project(point);
					final Vector3D znppoint = project(znpoint);
					final Vector3D xnppoint = project(xnpoint);
					final Vector3D ynppoint = project(ynpoint);
					if (ppoint == null) {
						continue;
					}

					final int radius = (int) projectRadius(point, ppoint, finalBubbleRadius * 3);
					if (radius == 0) {
						continue;
					}

					if (ppoint != null) {

						if (camera.inRange(ppoint)) {
							graphicsSubsystem.drawFilledCircle((int) ppoint.x(), (int) ppoint.y(), radius,
									() -> adjustColor(Color.GREEN, point));
						}

						if (xnppoint != null) {
							if (x < dimension.getWidth()) {
								graphicsSubsystem.drawLine((int) ppoint.x(), (int) ppoint.y(), (int) xnppoint.x(), (int) xnppoint.y(),
										adjustColor(gridcolor, ppoint), adjustColor(gridcolor, xnppoint));
							}
						}

						if (ynppoint != null) {
							if (y < dimension.getHeight()) {
								graphicsSubsystem.drawLine((int) ppoint.x(), (int) ppoint.y(), (int) ynppoint.x(), (int) ynppoint.y(),
										adjustColor(gridcolor, ppoint), adjustColor(gridcolor, ynppoint));
							}
						}

						if (znppoint != null) {
							if (z > 0) {
								graphicsSubsystem.drawLine((int) ppoint.x(), (int) ppoint.y(), (int) znppoint.x(), (int) znppoint.y(),
										adjustColor(gridcolor, ppoint), adjustColor(gridcolor, znppoint));
							}
						}
					}
				}
			}
		}
	}

	private double projectRadius(final Vector3D point, final Vector3D ppoint, final double radius) {

		final Vector3D pRimPoint = project(
				AVectorND.add(point, camera.getTransformationToScreen().inverseTransformVector(new Vector3D(radius, 0, 0))));

		if (pRimPoint == null) {
			return 0;
		}

		return Vector3D.distance(ppoint, pRimPoint);

	}

	private double calculateDynamicRadius(Particle particle, final Dimension dimension) {

		final Vector3D point = particle.position;

		final Vector3D prc = project(new Vector3D(point.x(), point.y(), rotcenter.z()));
		if (prc == null) {
			return 0;
		}
		return MathUtils.morph(v -> particle.radius, v -> finalBubbleRadius,
				v -> MathUtils.sigmoid(MathUtils.morphRange(deltaxmin, dimension.getWidth() + deltaxmax, -3, 4, point.x())), prc.x());

	}

	/**
	 * @param point
	 * @return
	 */
	private Vector3D project(final Vector3D point) {
		return camera.project(point);
	}

	private Color adjustColor(final Color color, final Vector3D point) {

		final Vector3D t_point = dynamicColoring ? camera.getTransformationToScreen().transformPoint(point) : point;
		if (t_point.z() > rotcenter.z()) {

			float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			final float brightness = (float) (hsb[2] * rotcenter.z() / (2 * t_point.z()));
			return Color.getHSBColor(hsb[0], hsb[1], brightness);

		} else {
			return color;
		}
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
			final Vector3D point = particle.position;

			final Function<Double, Double> rottrans = x -> MathUtils
					.sigmoid(MathUtils.morphRange(0, width, rotationTransitionLeft, rotationTransitionRight, point.x()));

			point.setX(point.x() + MathUtils.morph(v -> particle.velocity, v -> particle.velocity + 10, rottrans, point.x()));

			if (point.x() > width) {

				iterator.remove();
				continue;

			}

			final double cradius = MathUtils.morph(x -> Math.abs(rotcenter.y() - particle.initialPosition.y()), x -> finalCircleRadius,
					rottrans, point.x());

			particle.angle += MathUtils.morph(x -> 0.000005, x -> 40D, rottrans, point.x());

			point.setZ(Math.sin(theta(particle.angle)) * cradius + rotcenter.z());
			point.setY(rotcenter.y() + (particle.initialPosition.y() < rotcenter.y() ? -1 : 1) * Math.cos(theta(particle.angle)) * cradius);

		}

		if (validByFrequency(frequency)) {
			newparticles.add(new Particle(deltaxmin, MathUtils.makeRandom((int) particlesminy, (int) particlesmaxy), rotcenter.z(),
					CUtils.transparent(CUtils.makeRandomColor(), 200), MathUtils.makeRandom(2, 7),
					MathUtils.makeRandom(minParticleRadius, maxParticleRadius)));
		}

		particles = newparticles;

		if (rotationspeed != 0) {
			camera.rotate(0, rotationspeed * 2 * Math.PI / (60 * 60), 0);
		}

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
				camera.getViewpoint().setX(camera.getViewpoint().x() + VP_STEP);
			}

			@Override
			public void minus() {
				camera.getViewpoint().setX(camera.getViewpoint().x() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(camera.getViewpoint().x());
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
				camera.getViewpoint().setY(camera.getViewpoint().y() + VP_STEP);
			}

			@Override
			public void minus() {
				camera.getViewpoint().setY(camera.getViewpoint().y() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(camera.getViewpoint().y());
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
				if (camera.getViewpoint().z() + VP_STEP < 0) {
					camera.getViewpoint().setZ(camera.getViewpoint().z() + VP_STEP);
				}
			}

			@Override
			public void minus() {
				camera.getViewpoint().setZ(camera.getViewpoint().z() - VP_STEP);
			}

			@Override
			public String getValue() {
				return String.valueOf(camera.getViewpoint().z());
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
				colorMixture = colorMixture == EColorMixture.ADDITIVE ? EColorMixture.MEAN : EColorMixture.ADDITIVE;
				getGraphicsSubsystem().setColorMixture(colorMixture);
			}

			@Override
			public void minus() {
				colorMixture = colorMixture == EColorMixture.ADDITIVE ? EColorMixture.MEAN : EColorMixture.ADDITIVE;
				getGraphicsSubsystem().setColorMixture(colorMixture);
			}

			@Override
			public String getValue() {
				return String.valueOf(colorMixture.name());
			}
		});
		addKeyAction(KeyEvent.VK_S, new IKeyAction() {

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
				return "MPR";
			}

			@Override
			public String text() {
				return "max particle radius";
			}

			@Override
			public void plus() {
				maxParticleRadius++;
			}

			@Override
			public void minus() {
				if (maxParticleRadius > minParticleRadius) {
					maxParticleRadius--;
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(maxParticleRadius);
			}
		});
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
				return "ROTSPEED";
			}

			@Override
			public String text() {
				return "rotation speed";
			}

			@Override
			public void plus() {
				rotationspeed++;
			}

			@Override
			public void minus() {
				rotationspeed--;
			}

			@Override
			public String getValue() {
				return String.valueOf(rotationspeed);
			}
		});
		addKeyAction(KeyEvent.VK_D, new IKeyAction() {

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
				return "DYNCOL";
			}

			@Override
			public String text() {
				return "dynamic coloring";
			}

			@Override
			public void plus() {
				dynamicColoring = true;
			}

			@Override
			public void minus() {
				dynamicColoring = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(dynamicColoring);
			}
		});
		addKeyAction(KeyEvent.VK_A, new IKeyAction() {

			private final int rstep = 20;

			private final double cstep = 0.1;

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
				return "RAD";
			}

			@Override
			public String text() {
				return (camera instanceof SphereCamera3D ? "radius" : "curvature");
			}

			@Override
			public void plus() {
				if (camera instanceof SphereCamera3D) {
					final SphereCamera3D scamera = (SphereCamera3D) camera;
					scamera.setRadius(scamera.getRadius() + rstep);
				} else {
					final PlaneCamera3D pcamera = (PlaneCamera3D) camera;
					pcamera.setCurvature(pcamera.getCurvature() + cstep);
				}
			}

			@Override
			public void minus() {
				if (camera instanceof SphereCamera3D) {
					final SphereCamera3D scamera = (SphereCamera3D) camera;
					if (((getGraphicsSubsystem().getDimension().getWidth() - 1) / (2 * (scamera.getRadius() - rstep))) < Math.PI / 2) {
						scamera.setRadius(scamera.getRadius() - rstep);
					}
				} else {

					final PlaneCamera3D pcamera = (PlaneCamera3D) camera;
					if (pcamera.getCurvature() - cstep >= 0) {
						pcamera.setCurvature(pcamera.getCurvature() - cstep);
					} else {
						pcamera.setCurvature(0);
					}
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(
						camera instanceof SphereCamera3D ? ((SphereCamera3D) camera).getRadius() : ((PlaneCamera3D) camera).getCurvature());
			}
		});
		addKeyAction(KeyEvent.VK_E, new IKeyAction() {

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
				return "GRIDTYPE";
			}

			@Override
			public String text() {
				return "gridtype";
			}

			@Override
			public void plus() {
				switchGT();
			}

			@Override
			public void minus() {
				switchGT();
			}

			@Override
			public String getValue() {
				return String.valueOf(gridType.name());
			}

			private void switchGT() {
				gridType = gridType == GridType.SIMPLE ? GridType.COMPLEX : GridType.SIMPLE;
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
}
