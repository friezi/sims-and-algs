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

import de.zintel.animator.IAnimator;
import de.zintel.animator.MultiAnimator;
import de.zintel.animator.PathCameraAnimator;
import de.zintel.animator.XInputCameraAnimator;
import de.zintel.camera.ICamera3D;
import de.zintel.camera.PlaneCamera3D;
import de.zintel.camera.SphereCamera3D;
import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.g3d.BezierInterpolaterFactory;
import de.zintel.gfx.g3d.LinearPointInterpolater3D;
import de.zintel.gfx.g3d.RandomPointInterpolaterFactory;
import de.zintel.gfx.g3d.renderer.IRenderer;
import de.zintel.gfx.g3d.renderer.PathCameraAnimatorRenderer;
import de.zintel.gfx.g3d.renderer.PlaneCamera3DRenderer;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.AVectorND;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.sim.SimulationScreen;
import de.zintel.utils.CyclingIterator;
import de.zintel.utils.Pair;
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

	private ICamera3D<?> camera;

	private final Collection<ICamera3D<?>> cameras = new LinkedList<>();

	private CyclingIterator<ICamera3D<?>> cameraIterator;

	private Collection<IAnimator> cameraAnimators = new LinkedList<>();

	private final Collection<IRenderer> cameraRenderers = new LinkedList<>();

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

	private boolean dynamicColoring = false;

	private IAnimator multiAnimator;

	private Collection<IAnimator> animators = new ArrayList<>();

	private volatile boolean doAnimation = true;

	private volatile boolean showCameras = false;

	private final double initialCurvature = 100;

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
	 * @see de.zintel.sim.SimulationScreen#init(de.zintelgfx.graphicsubsystem.
	 * IGraphicsSubsystem)
	 */
	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		graphicsSubsystem.setColorMixture(colorMixture);
		initKeyActions();
		initWhirlParticleSystem(graphicsSubsystem);
		initCameras(graphicsSubsystem);
		initAnimators(graphicsSubsystem);

	}

	private void initWhirlParticleSystem(IGraphicsSubsystem graphicsSubsystem) {

		whirlParticleSystem = new WhirlParticleSystem<>(
				particle -> new WhirlParticleAttributes(CUtils.transparent(CUtils.makeRandomColor(), 200),
						MathUtils.makeRandom(minParticleRadius, maxParticleRadius)),
				Collections.emptySet(), rotcenter, deltaxmin, deltaxmax, 1, getGraphicsSubsystem().getDimension().getHeight(),
				finalCircleRadius);

	}

	/**
	 * @param graphicsSubsystem
	 */
	@SuppressWarnings("unchecked")
	private void initCameras(IGraphicsSubsystem graphicsSubsystem) {

		{
			// camera = new SphereCamera3D(new Vector3D(950.0, 140.0, -1000.0),
			// new CoordinateTransformation3D(), 5000000,
			// graphicsSubsystem.getDimension());
			final PlaneCamera3D currCamera = new PlaneCamera3D(
					new Vector3D((graphicsSubsystem.getDimension().getWidth() - 1) / 2,
							(graphicsSubsystem.getDimension().getHeight() - 1) / 2, -1000.0),
					new CoordinateTransformation3D(), 0, graphicsSubsystem.getDimension()).setId("plane: manual");
			currCamera.setCurvature(initialCurvature);
			XInputCameraAnimator animator = new XInputCameraAnimator(currCamera, 50);
			addXInputHandle(new XInputHandle(0, animator));
			cameraAnimators.add(animator);
			cameraRenderers.add(new PlaneCamera3DRenderer(currCamera, Color.YELLOW));

			cameras.add(currCamera);
		}

		{
			final PlaneCamera3D currCamera = new PlaneCamera3D(
					new Vector3D((graphicsSubsystem.getDimension().getWidth() - 1) / 2,
							(graphicsSubsystem.getDimension().getHeight() - 1) / 2, -1000.0),
					new CoordinateTransformation3D(), 0, graphicsSubsystem.getDimension()).setId("plane: auto");
			currCamera.setCurvature(initialCurvature);
			final PathCameraAnimator animator = new PathCameraAnimator(currCamera, rotcenter, graphicsSubsystem.getDimension(),
					new RandomPointInterpolaterFactory(new BezierInterpolaterFactory(graphicsSubsystem.getDimension(), 80),
							(start, end) -> new LinearPointInterpolater3D(start, end, false)));
			cameraAnimators.add(animator);
			cameraRenderers.add(new PathCameraAnimatorRenderer(animator));
			cameraRenderers.add(new PlaneCamera3DRenderer(currCamera, Color.GREEN));

			cameras.add(currCamera);
		}

		cameraIterator = new CyclingIterator<>(cameras);
		camera = cameraIterator.next();

	}

	private void initAnimators(IGraphicsSubsystem graphicsSubsystem) {

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
					whirlParticleSystem.getRotcenter().setY(MathUtils.scalel(0, maxSteps(), start, end, step));
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
					whirlParticleSystem.setParticlesminy(((int) MathUtils.scalel(0, maxSteps(), start, end, step)));
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
					whirlParticleSystem.setRotationTransitionLeft(((int) MathUtils.scalel(0, maxSteps(), start, end, step)));
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
					.sigmoid(MathUtils.scalel(deltaxmin, dimension.getWidth() + deltaxmax, -7, 1.4, point.x()));
			final Function<Double, Double> alphatrans = v -> MathUtils
					.sigmoid(MathUtils.scalel(deltaxmin, dimension.getWidth() + deltaxmax, -18, 1.4, point.x()));

			if (camera.inScreenRange(ppoint)) {
				graphicsSubsystem.drawFilledCircle((int) px, (int) py, pradius, () -> {
					final Color pcolor = particle.getAttribute().color;
					return CUtils.transparent(adjustColor(
							CUtils.morphColor(pcolor, CUtils.transparent(Color.YELLOW, pcolor.getAlpha()), colortrans, point.x()), point),
							(int) MathUtils.morph(v -> (double) pcolor.getAlpha(), v -> 0D, alphatrans, point.x()));
				});
			}
		}

		if (showCameras) {
			cameraRenderers.forEach(renderer -> renderer.render(graphicsSubsystem, camera));
		}
	}

	private void drawSimpleGrid(IGraphicsSubsystem graphicsSubsystem) {

		final Dimension dimension = graphicsSubsystem.getDimension();
		final double gridz = 1500;

		final double gridy = dimension.getHeight();
		for (int i = 0 + (int) deltaxmin; i < dimension.getWidth() + deltaxmax; i += 50) {

			{
				final Vector3D topStart = new Vector3D(i, 0, 0);
				final Vector3D topEnd = new Vector3D(i, 0, gridz);
				final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(topStart, topEnd);
				if (line != null) {
					final Vector3D p_topStart = line.getFirst();
					final Vector3D p_topEnd = line.getSecond();
					graphicsSubsystem.drawLine((int) p_topStart.x(), (int) p_topStart.y(), (int) p_topEnd.x(), (int) p_topEnd.y(),
							adjustColor(gridcolor, topStart), adjustColor(gridcolor, topEnd));
				}
			}
			{
				final Vector3D bottomStart = new Vector3D(i, gridy, 0);
				final Vector3D bottomEnd = new Vector3D(i, gridy, gridz);
				final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(bottomStart, bottomEnd);
				if (line != null) {
					final Vector3D p_bottomStart = line.getFirst();
					final Vector3D p_bottomEnd = line.getSecond();
					graphicsSubsystem.drawLine((int) p_bottomStart.x(), (int) p_bottomStart.y(), (int) p_bottomEnd.x(),
							(int) p_bottomEnd.y(), adjustColor(gridcolor, bottomStart), adjustColor(gridcolor, bottomEnd));
				}
			}
			{
				final Vector3D backStart = new Vector3D(i, 0, gridz);
				final Vector3D backEnd = new Vector3D(i, gridy, gridz);
				final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(backStart, backEnd);
				if (line != null) {
					final Vector3D p_backStart = line.getFirst();
					final Vector3D p_backEnd = line.getSecond();
					graphicsSubsystem.drawLine((int) p_backStart.x(), (int) p_backStart.y(), (int) p_backEnd.x(), (int) p_backEnd.y(),
							adjustColor(gridcolor, backStart), adjustColor(gridcolor, backEnd));
				}
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
					{
						final Vector3D ppoint = project(point);
						if (ppoint != null) {

							final int radius = (int) projectRadius(point, ppoint, finalBubbleRadius * 2.5);
							if (radius == 0) {
								continue;
							}
							if (camera.inScreenRange(ppoint)) {
								graphicsSubsystem.drawFilledCircle((int) ppoint.x(), (int) ppoint.y(), radius,
										() -> adjustColor(Color.GREEN, point));
							}
						}
					}
					{
						final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(point, xnpoint);
						if (line != null) {
							final Vector3D ppoint = line.getFirst();
							final Vector3D xnppoint = line.getSecond();
							if (x < dimension.getWidth()) {
								graphicsSubsystem.drawLine((int) ppoint.x(), (int) ppoint.y(), (int) xnppoint.x(), (int) xnppoint.y(),
										adjustColor(gridcolor, ppoint), adjustColor(gridcolor, xnppoint));
							}
						}
					}
					{
						final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(point, ynpoint);
						if (line != null) {
							final Vector3D ppoint = line.getFirst();
							final Vector3D ynppoint = line.getSecond();
							if (y < dimension.getHeight()) {
								graphicsSubsystem.drawLine((int) ppoint.x(), (int) ppoint.y(), (int) ynppoint.x(), (int) ynppoint.y(),
										adjustColor(gridcolor, ppoint), adjustColor(gridcolor, ynppoint));
							}
						}
					}
					{
						final Pair<Vector3D, Vector3D> line = camera.worldLineToScreen(point, znpoint);
						if (line != null) {
							final Vector3D ppoint = line.getFirst();
							final Vector3D znppoint = line.getSecond();
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
		return MathUtils.morph(v -> particle.getAttribute().radius, v -> finalBubbleRadius,
				v -> MathUtils.sigmoid(MathUtils.scalel(deltaxmin, dimension.getWidth() + deltaxmax, -3, 4, point.x())), prc.x());

	}

	/**
	 * @param point
	 * @return
	 */
	private Vector3D project(final Vector3D point) {
		return camera.projectWorld(point);
	}

	private Color adjustColor(final Color color, final Vector3D point) {

		final Vector3D t_point = dynamicColoring ? camera.getTransformationToCamera().transformPoint(point) : point;
		if (t_point.z() > whirlParticleSystem.getRotcenter().z()) {

			float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			final float brightness = (float) (hsb[2] * (100 + whirlParticleSystem.getRotcenter().z()) / (100 + t_point.z()));
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
		cameraAnimators.forEach(IAnimator::step);

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
		addKeyAction(KeyEvent.VK_K, new IKeyAction() {

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
				return "SWITCHCAMERA";
			}

			@Override
			public String text() {
				return "switch camera";
			}

			@Override
			public void plus() {
				camera = cameraIterator.next();
			}

			@Override
			public void minus() {
			}

			@Override
			public String getValue() {
				return camera.getId();
			}
		});
		addKeyAction(KeyEvent.VK_J, new IKeyAction() {

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
				return "SHOWCAMERAS";
			}

			@Override
			public String text() {
				return "show cameras";
			}

			@Override
			public void plus() {
				showCameras = !showCameras;
			}

			@Override
			public void minus() {
				showCameras = !showCameras;
			}

			@Override
			public String getValue() {
				return String.valueOf(showCameras);
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
	public void buttonChanged(XInputButton button, boolean pressed) {
		super.buttonChanged(button, pressed);

		if (button == XInputButton.DPAD_RIGHT && pressed) {
			camera = cameraIterator.next();
		} else if (button == XInputButton.BACK && pressed) {
			stop();
		} else if (button == XInputButton.START && pressed) {
			setPaused(!isPaused());
		}

	}
}
