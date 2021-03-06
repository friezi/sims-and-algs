/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.zintel.ci.Boid;
import de.zintel.ci.BoidMotioner;
import de.zintel.ci.BoidType;
import de.zintel.ci.FishSwarm;
import de.zintel.ci.Swarm;
import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.g2d.BezierPointInterpolater;
import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.ContinuousInterpolatingValueProvider;
import de.zintel.math.MathUtils;
import de.zintel.math.Polar2D;
import de.zintel.math.Vector2DPlain;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class CollectiveIntelligence extends SimulationScreen {

	private static ScreenParameters screenParameters = new ScreenParameters();

	private static final Random RANDOM = new Random();

	private static final boolean USE_DYNAMIC_PROVIDERS = false;

	private static final int NMB_BOIDS = 500;

	private static final int NMB_LEADERS = 4;

	private static final int NMB_PREDATORS = 3;

	private static final int BOID_SIZE = 6;

	private static final int DELAY = 10;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final Color[] COLOR_BOID = new Color[] { Color.BLUE, new Color(0, 0, 180), new Color(0, 0, 130), new Color(0, 0, 90),
			new Color(0, 0, 70), new Color(0, 0, 90), new Color(0, 0, 130), new Color(0, 0, 180) };

	private static final Color[] COLOR_LEADER = COLOR_BOID;

	private static final Color[] COLOR_PREDATOR = new Color[] { Color.RED, new Color(200, 0, 0) };

	private static final Color COLOR_CLUSTERING = Color.GREEN.darker().darker().darker().darker().darker().darker();

	private static final Color SHINE = Color.WHITE;

	private static final int LEADER_SPEED = 3;

	private static final int PREDATOR_SPEED = 5;

	private static final boolean SHIVERING = (GFX_SSYSTEM == GfxUtils.EGraphicsSubsystem.GL);

	private static final String ID_BOID_SPEED = "b_s";

	private static final String ID_SEPARATION_INFLUENCE = "s_i";

	private static final String ID_ALIGNMENT_INFLUENCE = "a_i";

	private static final String ID_COHESION = "coh";

	private static final String ID_SEPARATION = "sep";

	private static final String ID_ALIGNMENT = "al";

	private static final String ID_PUBLIC_DISTANCE = "pud";

	private static final String ID_PERSONAL_DISTANCE = "ped";

	private static final String ID_PREDATOR_DISTANCE = "prd";

	private static final String ID_LEADER_ATTRACTION = "pan";

	private static final String ID_PANIC = "l_a";

	private static final String ID_CLUSTERING = "clustering";

	private int cIdx = 0;

	private boolean firstRun = true;

	private static class BezierMotioner implements BoidMotioner {

		private static final double threshold = 0.75;

		private Point previousPosition;

		private final int speed;

		private final Collection<BezierPointInterpolater> interpolaters = new LinkedList<>();

		public BezierMotioner(Boid boid, int speed) {
			super();
			this.previousPosition = new Point((int) boid.getPosition().x, (int) boid.getPosition().y);
			this.speed = speed;
		}

		public Vector2DPlain nextMotionVector() {

			Point currentPosition = previousPosition;

			{
				final Iterator<BezierPointInterpolater> iterator = interpolaters.iterator();
				while (iterator.hasNext()) {
					if (!iterator.next().hasNext()) {
						iterator.remove();
					}
				}
			}

			if (interpolaters.isEmpty()) {

				interpolaters.clear();
				interpolaters.add(newBezierPointInterpolater(previousPosition));

			}

			for (int i = 0; i < speed; i++) {

				final Iterator<BezierPointInterpolater> iterator = interpolaters.iterator();
				if (iterator.hasNext()) {

					final BezierPointInterpolater interpolater = iterator.next();
					if (interpolater.hasNext()) {

						final IterationUnit2D currentUnit = interpolater.next();
						currentPosition = currentUnit.getPoint();
						final double ratio = ((double) currentUnit.getIteration()) / currentUnit.getMaxIterations();
						if (ratio > threshold) {

							BezierPointInterpolater nextInterpolater;

							if (!iterator.hasNext()) {

								nextInterpolater = newBezierPointInterpolater(currentPosition);
								interpolaters.add(nextInterpolater);

							} else {

								nextInterpolater = iterator.next();
								if (!nextInterpolater.hasNext()) {

									interpolaters.remove(nextInterpolater);
									nextInterpolater = newBezierPointInterpolater(currentPosition);
									interpolaters.add(nextInterpolater);

								}

							}

							final IterationUnit2D nextUnit = nextInterpolater.next();
							final Point nextPosition = nextUnit.getPoint();
							final int thresholdStart = (int) (currentUnit.getMaxIterations() * threshold);
							final int max = currentUnit.getMaxIterations() - thresholdStart;
							final int step = currentUnit.getIteration() - thresholdStart + 1;
							final double sFac = MathUtils.interpolateLinearReal(1, 0, step, max);
							final double tFac = MathUtils.interpolateLinearReal(0, 1, step, max);
							// smooth transition of both interpolaters
							currentPosition = new Point((int) (sFac * currentPosition.x + tFac * nextPosition.x),
									(int) (sFac * currentPosition.y + tFac * nextPosition.y));

						}

					} else {
						iterator.remove();
					}
				}
			}

			final Vector2DPlain motionVector = new Vector2DPlain(currentPosition.x - previousPosition.x,
					currentPosition.y - previousPosition.y);
			previousPosition = currentPosition;

			return motionVector;
		}

		private BezierPointInterpolater newBezierPointInterpolater(Point start) {

			BezierPointInterpolater interpolater = new BezierPointInterpolater(start,
					makeRandomPoint(screenParameters.WIDTH, screenParameters.HEIGHT), false, false);
			int nmbControlPoints = RANDOM.nextInt(10);
			for (int i = 0; i < nmbControlPoints; i++) {
				interpolater.addControlPoint(makeRandomPoint(screenParameters.WIDTH, screenParameters.HEIGHT));
			}

			return interpolater;

		}
	}

	private static class MouseBasedMotioner implements BoidMotioner, MouseMotionListener {

		private Vector2DPlain previousPosition;

		private Vector2DPlain currentPosition;

		public MouseBasedMotioner(Boid boid) {
			this.previousPosition = boid.getPosition();
			this.currentPosition = this.previousPosition;
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			currentPosition = new Vector2DPlain(e.getX(), e.getY());
		}

		@Override
		public Vector2DPlain nextMotionVector() {
			Vector2DPlain motionVector = Vector2DPlain.substract(currentPosition, previousPosition);
			previousPosition = currentPosition;
			return motionVector;
		}

	}

	private IGraphicsSubsystem graphicsSubsystem;

	private Swarm swarm;

	private ContinuousInterpolatingValueProvider leaderAttractionProvider = new ContinuousInterpolatingValueProvider(1, 800000);
	private ContinuousInterpolatingValueProvider speedProvider = new ContinuousInterpolatingValueProvider(1, 100);

	private boolean clustering = false;

	private final Collection<Boid> leaders = new ArrayList<Boid>() {
		{
			for (int i = 0; i < NMB_LEADERS; i++) {
				Boid leader = makeRandomBoid("leader_" + i).setType(BoidType.LEADER);
				leader.setMotioner(new BezierMotioner(leader, LEADER_SPEED));
				add(leader);
			}
		}
	};
	private final Collection<Boid> predators = new ArrayList<Boid>() {
		{
			for (int i = 0; i < NMB_PREDATORS; i++) {
				Boid predator = makeRandomBoid("predator_" + i).setType(BoidType.PREDATOR);
				predator.setMotioner(new BezierMotioner(predator, PREDATOR_SPEED));
				add(predator);
			}
		}
	};

	private final Boid mouseBoid = makeRandomBoid("mouseBoid").setType(BoidType.LEADER).setConvergeAttractor(false);

	private boolean mouseIsLeader = true;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new CollectiveIntelligence(GFX_SSYSTEM, screenParameters, false, "", 1).start();
	}

	public CollectiveIntelligence(EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("Collective Intelligence", gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		this.graphicsSubsystem = graphicsSubsystem;
		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		graphicsSubsystem.setColorMixture(EColorMixture.ADDITIVE);

		swarm = new FishSwarm(
				new Vector2DPlain(graphicsSubsystem.getDimension().getWidth() / 2, graphicsSubsystem.getDimension().getHeight() / 2))
						.setUseLeader(true).setUsePredator(true)
						.setPublicDistance(MathUtils.distance(new Point(), new Point(screenParameters.WIDTH, screenParameters.HEIGHT)) / 4)
						.setLeaderAttraction(40000);

		initKeyActions();
		initBoids();

		setCalculationRate(DELAY);

	}

	@Override
	protected void calculate(Dimension dimension) throws Exception {

		if (firstRun) {
			firstRun = false;
		} else {

			if (cIdx == Integer.MAX_VALUE - NMB_BOIDS) {
				cIdx = 0;
			} else {
				cIdx++;
			}

			if (USE_DYNAMIC_PROVIDERS) {
				swarm.setLeaderAttraction(leaderAttractionProvider.nextValue());
				swarm.setBoidSpeed(speedProvider.nextValue());
			}
		}

		swarm.swarm();

	}

	@Override
	protected void renderSim(IGraphicsSubsystem graphicsSubsystem) {

		final Collection<Boid> boids = new ArrayList<Boid>(swarm.getBoids());
		if (clustering) {

			// detect clusters
			final BiFunction<Boid, Boid, Double> distanceOp = (b1, b2) -> Vector2DPlain.distance(b1.getPosition(), b2.getPosition());
			final Collection<Boid> memberBoids = boids.stream().filter(boid -> boid.getType() == BoidType.MEMBER)
					.collect(Collectors.toList());

			final Set<Collection<Boid>> boidClusters = MathUtils.getClusters(memberBoids, 2.5, distanceOp);

			// Visualize clusters
			for (Collection<Boid> cluster : boidClusters) {

				final Queue<Boid> queue = new LinkedList<>(cluster);
				Boid boid = null;
				while ((boid = queue.poll()) != null) {
					for (Boid neighbour : queue) {
						graphicsSubsystem.drawLine((int) boid.getPosition().x, (int) boid.getPosition().y, (int) neighbour.getPosition().x,
								(int) neighbour.getPosition().y, COLOR_CLUSTERING, COLOR_CLUSTERING);
					}
				}
			}

		}

		int i = 0;
		Color color = null;
		for (final Boid boid : boids) {

			i++;

			if (boid.getType() == BoidType.LEADER || boid.getType() == BoidType.PREDATOR) {

				if (boid.getType() == BoidType.LEADER) {
					color = calculateMovementDependentDeltaColor(SHINE, COLOR_LEADER[cIdx % COLOR_LEADER.length], boid);

				} else if (boid.getType() == BoidType.PREDATOR) {
					color = COLOR_PREDATOR[cIdx % COLOR_PREDATOR.length];
				}

			} else {
				color = calculateMovementDependentDeltaColor(SHINE, COLOR_BOID[(cIdx + i) % COLOR_BOID.length], boid);
			}

			final Color effectiveColor = color;

			Supplier<Color> colorGenerator = new Supplier<Color>() {

				boolean center = true;

				@Override
				public Color get() {

					if (center == true) {

						center = false;
						return effectiveColor;

					} else {
						return new Color(effectiveColor.getRed(), effectiveColor.getGreen(), effectiveColor.getBlue(), 50);
					}
				}
			};

			graphicsSubsystem.drawFilledEllipse((int) boid.getPosition().x, (int) boid.getPosition().y,
					graphicsSubsystem.supportsColorChange() ? BOID_SIZE : BOID_SIZE / 2, 2,
					Math.PI / 2 - boid.getDirectionPolar().getAngle(), colorGenerator);

			if (SHIVERING && !graphicsSubsystem.supportsColorChange()) {
				graphicsSubsystem.drawFilledEllipse((int) boid.getPosition().x, (int) boid.getPosition().y, BOID_SIZE, 2,
						Math.PI / 2 - boid.getDirectionPolar().getAngle(),
						() -> new Color(effectiveColor.getRed(), effectiveColor.getGreen(), effectiveColor.getBlue(), 100));
			}
		}
	}

	@Override
	protected void shutdown() throws Exception {

	}

	private void initKeyActions() {

		addKeyAction(KeyEvent.VK_L, new IKeyAction() {

			final int delta = 1000;

			@Override
			public String textID() {
				return ID_LEADER_ATTRACTION;
			}

			@Override
			public String text() {
				return "leader attraction";
			}

			@Override
			public void plus() {
				swarm.setLeaderAttraction(Math.max(swarm.getLeaderAttraction() + delta, 1));
			}

			@Override
			public void minus() {
				swarm.setLeaderAttraction(Math.max(swarm.getLeaderAttraction() - delta, 1));
			}

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getLeaderAttraction());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_S, new IKeyAction() {

			@Override
			public String textID() {
				return ID_BOID_SPEED;
			}

			@Override
			public String text() {
				return "boid speed";
			}

			@Override
			public void plus() {
				swarm.setBoidSpeed(Math.max(swarm.getBoidSpeed() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setBoidSpeed(Math.max(swarm.getBoidSpeed() - 1, 1));
			}

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getBoidSpeed());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_I, new IKeyAction() {

			@Override
			public String textID() {
				return ID_SEPARATION_INFLUENCE;
			}

			@Override
			public String text() {
				return "influence of separation";
			}

			@Override
			public void plus() {
				swarm.setInfluenceOfSeparation(Math.max(swarm.getInfluenceOfSeparation() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setInfluenceOfSeparation(Math.max(swarm.getInfluenceOfSeparation() - 1, 1));
			}

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getInfluenceOfSeparation());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_J, new IKeyAction() {

			@Override
			public String textID() {
				return ID_ALIGNMENT_INFLUENCE;
			}

			@Override
			public String text() {
				return "influence of alignment";
			}

			@Override
			public void plus() {
				swarm.setInfluenceOfAlignment(Math.max(swarm.getInfluenceOfAlignment() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setInfluenceOfAlignment(Math.max(swarm.getInfluenceOfAlignment() - 1, 1));
			}

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getInfluenceOfAlignment());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_C, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_COHESION;
			}

			@Override
			public String text() {
				return "cohesion";
			}

			@Override
			public void plus() {
				swarm.setUseCohesion(true);
			}

			@Override
			public void minus() {
				swarm.setUseCohesion(false);
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.isUseCohesion());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_P, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_SEPARATION;
			}

			@Override
			public String text() {
				return "separation";
			}

			@Override
			public void plus() {
				swarm.setUseSeparation(true);
			}

			@Override
			public void minus() {
				swarm.setUseSeparation(false);
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.isUseSeparation());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_N, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_PANIC;
			}

			@Override
			public String text() {
				return "panic";
			}

			@Override
			public void plus() {
				swarm.setUsePanic(true);
			}

			@Override
			public void minus() {
				swarm.setUsePanic(false);
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.isUsePanic());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_A, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_ALIGNMENT;
			}

			@Override
			public String text() {
				return "alignment";
			}

			@Override
			public void plus() {
				swarm.setUseAlignment(true);
			}

			@Override
			public void minus() {
				swarm.setUseAlignment(false);
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.isUseAlignment());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_U, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_PUBLIC_DISTANCE;
			}

			@Override
			public String text() {
				return "public distance";
			}

			@Override
			public void plus() {
				swarm.setPublicDistance(Math.max(swarm.getPublicDistance() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setPublicDistance(Math.max(swarm.getPublicDistance() - 1, 1));
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getPublicDistance());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_E, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_PERSONAL_DISTANCE;
			}

			@Override
			public String text() {
				return "personal distance";
			}

			@Override
			public void plus() {
				swarm.setPersonalDistance(Math.max(swarm.getPersonalDistance() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setPersonalDistance(Math.max(swarm.getPersonalDistance() - 1, 1));
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getPersonalDistance());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_R, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_PREDATOR_DISTANCE;
			}

			@Override
			public String text() {
				return "predator distance";
			}

			@Override
			public void plus() {
				swarm.setPredatorDistance(Math.max(swarm.getPredatorDistance() + 1, 1));
			}

			@Override
			public void minus() {
				swarm.setPredatorDistance(Math.max(swarm.getPredatorDistance() - 1, 1));
			}

			@Override
			public String getValue() {
				return String.valueOf(swarm.getPredatorDistance());
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});
		addKeyAction(KeyEvent.VK_U, new IKeyAction() {

			@Override
			public boolean withAction() {
				return true;
			}

			@Override
			public String textID() {
				return ID_CLUSTERING;
			}

			@Override
			public String text() {
				return "clustering";
			}

			@Override
			public void plus() {
				clustering = true;
			}

			@Override
			public void minus() {
				clustering = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(clustering);
			}

			@Override
			public boolean toggleComponent() {
				return false;
			}
		});

	}

	private void initBoids() {

		if (swarm.isUseLeader()) {
			for (Boid leader : leaders) {
				swarm.addBoid(leader);
			}
		}

		if (swarm.isUsePredator()) {
			for (Boid predator : predators) {
				swarm.addBoid(predator);
			}
		}

		for (int i = 0; i < NMB_BOIDS; i++) {
			swarm.addBoid(makeRandomBoid(String.valueOf(i)));
		}

	}

	private Boid makeRandomBoid(String id) {
		return new Boid(makeRandomVector2D(screenParameters.WIDTH, screenParameters.HEIGHT), id);
	}

	private static Vector2DPlain makeRandomVector2D(int width, int height) {
		return new Vector2DPlain(makeRandomPoint(width, height));
	}

	private static Point makeRandomPoint(int width, int height) {
		return new Point(RANDOM.nextInt(width - 1) + 1, RANDOM.nextInt(height - 1) + 1);
	}

	/**
	 * calculates color-change dependent on the angle-change of movement.
	 * 
	 * @param shine
	 * @param color
	 * @param boid
	 * @return
	 */
	private Color calculateMovementDependentDeltaColor(final Color shine, Color color, final Boid boid) {

		Polar2D polarDirection = boid.getDirectionPolar();
		Polar2D polarPreviousDirection = boid.getPreviousDirectionPolar();

		double deltaAngle = (Double.isNaN(polarDirection.getAngle()) || Double.isNaN(polarPreviousDirection.getAngle())) ? 0
				: Math.abs(polarDirection.getAngle() - polarPreviousDirection.getAngle());
		if (deltaAngle > Math.PI) {
			deltaAngle = 2 * Math.PI - deltaAngle;
		}

		int deltaRed = calculateDeltaColorValue(shine.getRed(), color.getRed(), deltaAngle);
		int deltaGreen = calculateDeltaColorValue(shine.getGreen(), color.getGreen(), deltaAngle);
		int deltaBlue = calculateDeltaColorValue(shine.getBlue(), color.getBlue(), deltaAngle);

		return new Color(color.getRed() + deltaRed, color.getGreen() + deltaGreen, color.getBlue() + deltaBlue);
	}

	private int calculateDeltaColorValue(int targetColorValue, int currentColorValue, double angle) {
		return (int) (angle * (targetColorValue - currentColorValue) / Math.PI);
	}

	@Override
	public void mouseClicked(MouseEvent event) {

		super.mouseClicked(event);

		if (event.getButton() == MouseEvent.BUTTON1) {

			graphicsSubsystem.removeMouseMotionListener((MouseMotionListener) mouseBoid.getMotioner());

			mouseIsLeader ^= true;

			mouseBoid.setType(mouseIsLeader ? BoidType.LEADER : BoidType.PREDATOR);
			mouseBoid.setPosition(new Vector2DPlain(event.getX(), event.getY()));

			MouseBasedMotioner motioner = new MouseBasedMotioner(mouseBoid);
			mouseBoid.setMotioner(motioner);

			graphicsSubsystem.addMouseMotionListener(motioner);
			swarm.removeBoid(mouseBoid);
			swarm.addBoid(mouseBoid);

		} else if (event.getButton() == MouseEvent.BUTTON3) {

			mouseIsLeader ^= true;
			swarm.removeBoid(mouseBoid);
			graphicsSubsystem.removeMouseMotionListener((MouseMotionListener) mouseBoid.getMotioner());

		}
	}
}
