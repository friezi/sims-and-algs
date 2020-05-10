/**
 * 
 */
package de.zintel.sim.whirl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.github.strikerx3.jxinput.enums.XInputButton;

import de.zintel.animation.IAnimator;
import de.zintel.animation.MultiAnimator;
import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.AVectorND;
import de.zintel.math.Axis3D;
import de.zintel.math.ICamera3D;
import de.zintel.math.MathUtils;
import de.zintel.math.PlaneCamera3D;
import de.zintel.math.SphereCamera3D;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.sim.SimulationScreen;
import de.zintel.xinput.XInputHandle;

/**
 * @author friedemann.zintel
 *
 */
public class WhirlSim extends SimulationScreen {

	private static enum GridType {
		SIMPLE, COMPLEX
	}

	private static class WhirlParticleAttributes {

		public final Color color;

		private final double radius;

		public WhirlParticleAttributes(Color color, double radius) {
			this.color = color;
			this.radius = radius;
		}

	}

	private static final Color gridcolor = new Color(200, 0, 0);

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 20);

	private static final double VP_STEP = 10;

	private static final double CURVATURE_MAX = 20;

	private ICamera3D camera;

	private WhirlParticleSystem<WhirlParticleAttributes> whirlParticleSystem;

	private final double finalBubbleRadius = 3D;

	private final double finalCircleRadius = 1.0;

	private Vector3D rotcenter = new Vector3D(0.0, 540.0, 200.0);

	private double deltaxmin = -200;

	private double deltaxmax = 200;

	private boolean showgrid = true;

	private EColorMixture colorMixture = EColorMixture.ADDITIVE;

	private GridType gridType = GridType.SIMPLE;

	private int minParticleRadius = 3;

	private int maxParticleRadius = 9;

	private double hrotationspeed = 0;

	private final double maxspeed = 50;

	private double vrotationspeed = 0;

	private double zrotationspeed = 0;

	private double sidespeed = 0;

	private double frontspeed = 0;

	private boolean dynamicColoring = false;

	private IAnimator multiAnimator;

	private Collection<IAnimator> animators = new ArrayList<>();

	private boolean doAnimation = true;

	private boolean buttonRB = false;

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
		initWhirlParticleSystem(graphicsSubsystem);
		initCamera(graphicsSubsystem);
		initXInput();
		initAnimators();

	}

	private void initWhirlParticleSystem(IGraphicsSubsystem graphicsSubsystem) {

		whirlParticleSystem = new WhirlParticleSystem<>(
				particle -> new WhirlParticleAttributes(CUtils.transparent(CUtils.makeRandomColor(), 200),
						MathUtils.makeRandom(minParticleRadius, maxParticleRadius)),
				Collections.emptySet(), rotcenter, deltaxmin, deltaxmax, 1, getGraphicsSubsystem().getDimension().getHeight(), finalCircleRadius);

	}

	/**
	 * @param graphicsSubsystem
	 */
	private void initCamera(IGraphicsSubsystem graphicsSubsystem) {
		// camera = new SphereCamera3D(new Vector3D(950.0, 140.0, -1000.0), new
		// CoordinateTransformation3D(), 5000000,
		// graphicsSubsystem.getDimension());
		camera = new PlaneCamera3D(
				new Vector3D((graphicsSubsystem.getDimension().getWidth() - 1) / 2, (graphicsSubsystem.getDimension().getHeight() - 1) / 2, -1000.0),
				new CoordinateTransformation3D(), 0, graphicsSubsystem.getDimension());
	}

	/**
	 * 
	 */
	private void initXInput() {
		addXInputHandle(new XInputHandle(0).setXInputCombinedHandler(this));
	}

	private void initAnimators() {

		animators.clear();

		animators.add(createCenterAnimator());
		animators.add(creatTopAnimator());
		animators.add(creatRotationTransitionLeftAnimator());

		multiAnimator = new MultiAnimator(Collections.emptyList());

	}

	private IAnimator createCenterAnimator() {
		return new IAnimator() {

			private int start = 0;

			private int end = 0;

			private double step = 0;

			private double deltastep = 0;

			@Override
			public void step() {
				if (!finished()) {
					step += deltastep;
					whirlParticleSystem.getRotcenter().setY(MathUtils.morphRange(0, maxSteps(), start, end, step));
				}

			}

			@Override
			public void reinit() {
				end = MathUtils.RANDOM.nextInt((int) getGraphicsSubsystem().getDimension().getHeight());
				start = (int) whirlParticleSystem.getRotcenter().y();
				step = 0;
				deltastep = (1D / (MathUtils.RANDOM.nextInt(3) + 1)) / (1D / (MathUtils.RANDOM.nextInt(3) + 1));

			}

			@Override
			public boolean finished() {
				return step >= maxSteps();
			}

			private int maxSteps() {
				return Math.abs(end - start);
			}
		};
	}

	private IAnimator creatTopAnimator() {
		return new IAnimator() {

			private int start = 0;

			private int end = 0;

			private double step = 0;

			private double deltastep = 0;

			@Override
			public void step() {

				if (!finished()) {
					step += deltastep;
					whirlParticleSystem.setParticlesminy(((int) MathUtils.morphRange(0, maxSteps(), start, end, step)));
				}

			}

			@Override
			public void reinit() {
				end = MathUtils.RANDOM.nextInt((int) whirlParticleSystem.getParticlesmaxy() + 1);
				start = (int) whirlParticleSystem.getParticlesminy();
				step = 0;
				deltastep = 1;

			}

			@Override
			public boolean finished() {
				return step >= maxSteps();
			}

			private int maxSteps() {
				return Math.abs(end - start);
			}
		};
	}

	private IAnimator creatRotationTransitionLeftAnimator() {
		return new IAnimator() {

			private int start = 0;

			private int end = 0;

			private double step = 0;

			private double deltastep = 0;

			@Override
			public void step() {

				if (!finished()) {
					step += deltastep;
					whirlParticleSystem.setRotationTransitionLeft(((int) MathUtils.morphRange(0, maxSteps(), start, end, step)));
				}

			}

			@Override
			public void reinit() {
				end = MathUtils.RANDOM.nextInt(7) - 7;
				start = (int) whirlParticleSystem.getRotationTransitionLeft();
				step = 0;
				deltastep = 1D / (MathUtils.RANDOM.nextInt(5) + 5);

			}

			@Override
			public boolean finished() {
				return step >= maxSteps();
			}

			private int maxSteps() {
				return Math.abs(end - start);
			}
		};
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

		for (WhirlParticle<WhirlParticleAttributes> particle : whirlParticleSystem.getParticles()) {

			final Vector3D point = particle.getPosition();

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
						() -> CUtils.transparent(
								adjustColor(CUtils.morphColor(particle.getAttributes().color, Color.YELLOW, colortrans, point.x()), point),
								(int) MathUtils.morph(v -> (double) particle.getAttributes().color.getAlpha(), v -> 0D, alphatrans, point.x())));
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

					final int radius = (int) projectRadius(point, ppoint, finalBubbleRadius * 2.5);
					if (radius == 0) {
						continue;
					}

					if (ppoint != null) {

						if (camera.inRange(ppoint)) {
							graphicsSubsystem.drawFilledCircle((int) ppoint.x(), (int) ppoint.y(), radius, () -> adjustColor(Color.GREEN, point));
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

		final Vector3D pRimPoint = project(AVectorND.add(point, new Vector3D(radius, 0, 0)));

		if (pRimPoint == null) {
			return 0;
		}

		return Math.max(Vector3D.distance(ppoint, pRimPoint), 1);

	}

	private double calculateDynamicRadius(WhirlParticle<WhirlParticleAttributes> particle, final Dimension dimension) {

		final Vector3D point = particle.getPosition();

		final Vector3D prc = project(new Vector3D(point.x(), point.y(), whirlParticleSystem.getRotcenter().z()));
		if (prc == null) {
			return 0;
		}
		return MathUtils.morph(v -> particle.getAttributes().radius, v -> finalBubbleRadius,
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

		final Vector3D t_point = dynamicColoring ? camera.getTransformationToCamera().transformPoint(point) : point;
		if (t_point.z() > whirlParticleSystem.getRotcenter().z()) {

			float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			final float brightness = (float) (hsb[2] * whirlParticleSystem.getRotcenter().z() / (1.1 * t_point.z()));
			return Color.getHSBColor(hsb[0], hsb[1], brightness);

		} else {
			return color;
		}
	}

	private void doAnimators() {

		if (multiAnimator.finished()) {

			multiAnimator = newMultiAnimator();
			multiAnimator.reinit();

		}

		multiAnimator.step();

	}

	private IAnimator newMultiAnimator() {

		List<IAnimator> selectiveAnimators = new ArrayList<>(animators);
		Collection<IAnimator> activeAnimators = new LinkedList<>();

		final int cnt = MathUtils.RANDOM.nextInt(selectiveAnimators.size());
		for (int i = 0; i <= cnt; i++) {

			final int nmb = MathUtils.RANDOM.nextInt(selectiveAnimators.size());
			activeAnimators.add(selectiveAnimators.remove(nmb));

		}

		return new MultiAnimator(activeAnimators);

	}

	private double speedAngle(final double speed) {
		return speed * 2 * Math.PI / (60 * 60);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#calculate(java.awt.Dimension)
	 */
	@Override
	protected void calculate(Dimension dimension) throws Exception {

		if (doAnimation) {
			doAnimators();
		}

		if (hrotationspeed != 0) {

			final double angle = speedAngle(hrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x(), viewpoint.y() - 1, viewpoint.z()));
			camera.rotate(axis, angle);

		}

		if (vrotationspeed != 0) {

			final double angle = speedAngle(vrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x() + 1, viewpoint.y(), viewpoint.z()));
			camera.rotate(axis, angle);
		}

		if (zrotationspeed != 0) {

			final double angle = speedAngle(zrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x(), viewpoint.y(), viewpoint.z() + 1));
			camera.rotate(axis, angle);
		}

		if (frontspeed != 0) {
			final Vector3D translationVector = Vector3D.mult(frontspeed, new Vector3D(0, 0, 1));
			camera.translate(translationVector);
		}

		if (sidespeed != 0) {
			// s. a.
			final Vector3D tv = Vector3D.mult(sidespeed, new Vector3D(1, 0, 0));
			camera.translate(tv);
		}

		whirlParticleSystem.calculate(dimension);

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
				whirlParticleSystem.getRotcenter().setY(whirlParticleSystem.getRotcenter().y() + delta);
			}

			@Override
			public void minus() {
				whirlParticleSystem.getRotcenter().setY(whirlParticleSystem.getRotcenter().y() - delta);
			}

			@Override
			public String getValue() {
				return String.valueOf(whirlParticleSystem.getRotcenter().y());
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
				whirlParticleSystem.setRotationTransitionLeft(whirlParticleSystem.getRotationTransitionLeft() + delta);
			}

			@Override
			public void minus() {
				whirlParticleSystem.setRotationTransitionLeft(whirlParticleSystem.getRotationTransitionLeft() - delta);
			}

			@Override
			public String getValue() {
				return String.valueOf(whirlParticleSystem.getRotationTransitionLeft());
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
				whirlParticleSystem.setRotationTransitionRight(whirlParticleSystem.getRotationTransitionRight() + delta);
			}

			@Override
			public void minus() {
				whirlParticleSystem.setRotationTransitionRight(whirlParticleSystem.getRotationTransitionRight() - delta);
			}

			@Override
			public String getValue() {
				return String.valueOf(whirlParticleSystem.getRotationTransitionRight());
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
				if (whirlParticleSystem.getParticlesminy() + delta <= whirlParticleSystem.getParticlesmaxy()) {
					whirlParticleSystem.setParticlesminy(whirlParticleSystem.getParticlesminy() + delta);
				}
			}

			@Override
			public void minus() {
				whirlParticleSystem.setParticlesminy(whirlParticleSystem.getParticlesminy() - delta);
			}

			@Override
			public String getValue() {
				return String.valueOf(whirlParticleSystem.getParticlesminy());
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
				whirlParticleSystem.setParticlesmaxy(whirlParticleSystem.getParticlesmaxy() + delta);
			}

			@Override
			public void minus() {
				if (whirlParticleSystem.getParticlesmaxy() - delta >= whirlParticleSystem.getParticlesminy()) {
					whirlParticleSystem.setParticlesmaxy(whirlParticleSystem.getParticlesmaxy() - delta);
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(whirlParticleSystem.getParticlesmaxy());
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
				whirlParticleSystem.setFrequency(whirlParticleSystem.getFrequency() + 1);
			}

			@Override
			public void minus() {
				whirlParticleSystem.setFrequency(whirlParticleSystem.getFrequency() - 1);
			}

			@Override
			public String getValue() {
				final int frequency = whirlParticleSystem.getFrequency();
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
				hrotationspeed++;
			}

			@Override
			public void minus() {
				hrotationspeed--;
			}

			@Override
			public String getValue() {
				return String.valueOf(hrotationspeed);
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
				return String
						.valueOf(camera instanceof SphereCamera3D ? ((SphereCamera3D) camera).getRadius() : ((PlaneCamera3D) camera).getCurvature());
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
		addKeyAction(KeyEvent.VK_H, new IKeyAction() {

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
				return "DOANIMATION";
			}

			@Override
			public String text() {
				return "do animation";
			}

			@Override
			public void plus() {
				doAnimation = true;
			}

			@Override
			public void minus() {
				doAnimation = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(doAnimation);
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

	}

	@Override
	public void handleXInputLeftStick(float x, float y) {

		super.handleXInputLeftStick(x, y);

		sidespeed = 200 * x;
		frontspeed = 200 * y;
	}

	@Override
	public void handleXInputRightStick(float x, float y) {

		super.handleXInputRightStick(x, y);

		if (buttonRB) {
			hrotationspeed = 0;
			vrotationspeed = 0;
			zrotationspeed = maxspeed * x;
		} else {

			hrotationspeed = -maxspeed * x;
			vrotationspeed = maxspeed * y;
			zrotationspeed = 0;

		}
	}

	@Override
	public void handleXInputRT(float value) {

		super.handleXInputRT(value);

		if (camera instanceof PlaneCamera3D) {
			final PlaneCamera3D pcamera = (PlaneCamera3D) camera;
			pcamera.setCurvature(Math.max(pcamera.getCurvature(), CURVATURE_MAX * value));
		}

	}

	@Override
	public void handleXInputLT(float value) {

		super.handleXInputRT(value);

		if (camera instanceof PlaneCamera3D && value != 0) {
			final PlaneCamera3D pcamera = (PlaneCamera3D) camera;
			pcamera.setCurvature(Math.min(pcamera.getCurvature(), CURVATURE_MAX * (1 - value)));
		}

	}

	@Override
	public void buttonChanged(XInputButton button, boolean pressed) {
		super.buttonChanged(button, pressed);

		if (button == XInputButton.X && pressed) {
			initCamera(getGraphicsSubsystem());
		} else if (button == XInputButton.RIGHT_SHOULDER) {
			buttonRB = pressed;
		}

	}
}
