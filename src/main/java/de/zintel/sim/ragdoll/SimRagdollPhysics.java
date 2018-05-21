/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.zintel.control.IKeyAction;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.g2d.verlet.AdjustingColorProvider;
import de.zintel.gfx.g2d.verlet.IVLEdgeContainer2D;
import de.zintel.gfx.g2d.verlet.VLChain2D;
import de.zintel.gfx.g2d.verlet.VLChainNet2D;
import de.zintel.gfx.g2d.verlet.VLCuboid2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.g2d.verlet.VLTetragon2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector2D;
import de.zintel.math.VectorField2D;
import de.zintel.math.VectorND;
import de.zintel.physics.simulators.WindSimulator;
import de.zintel.sim.SimulationScreen;

/**
 * @author friedemann.zintel
 *
 */
public class SimRagdollPhysics extends SimulationScreen {

	private static final boolean doRecord = false;

	private static final String recordFilename = "D:/wind-sim.mpg";

	private static final int recordingRate = 2;

	private static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final int bobbleSize = 3;

	private static final Vector2D GRAV_DOWN = new Vector2D(0, 0.8);

	private static final Vector2D GRAV_UP = new Vector2D(0, -0.8);

	private static final Vector2D GRAV_RIGHT = new Vector2D(0.8, 0);

	private static final Vector2D GRAV_LEFT = new Vector2D(-0.8, 0);

	private final Random rnd = new Random(Instant.now().toEpochMilli());

	private final double decay = 0.2;

	private final double friction = 0.999;

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private final static String TXT_ID_WIND = "wind";

	private final static String TXT_ID_FILLED = "filled";

	private final static String TXT_ID_GRAVITY = "gravity";

	private final static String TXT_ID_SHOWAIRSTREAMVECTORS = "showairstreamvectors";

	private final static String TXT_ID_SHOWAIRSTREAM = "showairstream";

	private final static String TXT_ID_SHOWWIND = "showwind";

	private final static String TXT_ID_AIRSTREAMDEGREE = "airstreamdegree";

	private final static String TXT_ID_AIRSTREAMINTENSITY = "airstreamintensity";

	private final static String TXT_ID_ITERATIONS = "iterations";

	private final static String TXT_ID_RATE_AIRSTREAMCHANGE = "rate_airstreamchange";

	private final static String TXT_ID_WIND_PARTICLES = "windparticles";

	private final static String TXT_SYNC_RENDERING = "synRendering";

	private int iterations = 40;

	private Vector2D gravity = GRAV_DOWN;

	private volatile boolean mousePressed = false;

	private final Collection<VLVertex2D> grabbedVertices = Collections.synchronizedCollection(new ArrayList<>());

	private Vector2D mousePoint = new Vector2D();

	private VLChainNet2D chainNet;

	private final Color[] colors = { Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.GRAY, Color.CYAN, Color.MAGENTA,
			Color.PINK };

	private int colorCycleCnt = 0;

	private WindSimulator windSimulator;

	private boolean useWind = false;

	private boolean filled = false;

	private boolean useGravity = true;

	private boolean showAirstreamVectors = false;

	private boolean showAirstream = false;

	private boolean showWind = false;

	private boolean useWindparticles = false;

	private final int windParticleFrequence = 2;

	private long windParticleCnt = 0;

	private int airstreamdegree = 6;

	private int rateOfAirstreamChange = 1;

	private boolean syncRendering = false;

	private static class PlainEdgeRenderer implements Consumer<VLEdge2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		public PlainEdgeRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void accept(VLEdge2D edge) {
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, edge.getColor(), edge.getColor());
		}

	}

	private static class AdjustingEdgeRenderer implements Consumer<VLEdge2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		private final AdjustingColorProvider colorProvider = new AdjustingColorProvider();

		public AdjustingEdgeRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void accept(VLEdge2D edge) {

			final Color color = colorProvider.apply(edge);
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, color, color);
		}

	}

	private static class WireMeshChainRenderer implements Consumer<VLChain2D> {

		@Override
		public void accept(VLChain2D item) {
			for (VLEdge2D edge : item.getEdges()) {
				edge.render();
			}
		}
	}

	private static class WireMeshCuboidRenderer implements Consumer<VLCuboid2D> {

		@Override
		public void accept(VLCuboid2D item) {
			for (VLEdge2D edge : item.getEdges()) {
				edge.render();
			}
		}
	}

	private static class WireMeshChainNetRenderer implements Consumer<VLChainNet2D> {

		@Override
		public void accept(VLChainNet2D item) {
			for (VLEdge2D edge : item.getEdges()) {
				edge.render();
			}
		}
	}

	private static class WireMeshFacetRenderer implements Consumer<VLFacet2D> {

		@Override
		public void accept(VLFacet2D item) {
			for (VLEdge2D edge : item.getEdges()) {
				edge.render();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		new SimRagdollPhysics(GFX_SSYSTEM, SCREENPARAMETERS, doRecord, recordFilename, recordingRate).start();
	}

	private final Collection<IVLEdgeContainer2D> edgeContainers = new LinkedList<>();

	private Collection<VLEdge2D> edges = new ArrayList<>();

	private Collection<VLVertex2D> vertices = new LinkedHashSet<>();

	private final Collection<VLVertex2D> windParticles = new ConcurrentLinkedQueue<>();

	public SimRagdollPhysics(EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("Ragdoll physics", gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		System.out.println("initialising ...");

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		initScene(graphicsSubsystem);
		windSimulator = new WindSimulator(initAirstreamField(), getScreenParameters()).setRateOfAirstreamChange(rateOfAirstreamChange);
		initKeyActions();

		System.out.println("initialised");

	}

	@Override
	protected void calculate(Dimension dimension) throws Exception {

		calculatePhysics(dimension);

		for (VLVertex2D vertex : vertices) {
			if (Double.isNaN(vertex.getCurrent().x) || Double.isNaN(vertex.getCurrent().y) || Double.isNaN(vertex.getPrevious().x)
					|| Double.isNaN(vertex.getPrevious().y) || Double.isInfinite(vertex.getCurrent().x) || Double.isInfinite(vertex.getCurrent().y)
					|| Double.isInfinite(vertex.getPrevious().x) || Double.isInfinite(vertex.getPrevious().y)) {
				System.out.println(vertex);
			}
		}

	}

	@Override
	protected void shutdown() throws Exception {

	}

	private VectorField2D initAirstreamField() {

		final int fieldWidth = getScreenParameters().WIDTH / 80;
		final int fieldHeight = getScreenParameters().HEIGHT / 80;
		VectorND[][] airstreamfieldarray = new VectorND[fieldWidth][fieldHeight];
		final VectorField2D airstreamField = new VectorField2D(2, airstreamfieldarray);

		final double max1 = 1.2;
		VectorND[][] initfieldarray1 = { { new VectorND(Arrays.asList(0.0, 0.0)), new VectorND(Arrays.asList(0.0, max1)) },
				{ new VectorND(Arrays.asList(max1, 0.0)), new VectorND(Arrays.asList(max1, max1)) } };
		VectorField2D initfield1 = new VectorField2D(2, initfieldarray1);

		final double max2 = 0.2;
		VectorND[][] initfieldarray2 = { { new VectorND(Arrays.asList(max2, 0.0)), new VectorND(Arrays.asList(0.0, 0.0)) },
				{ new VectorND(Arrays.asList(max2 / 2, -max2 / 2)), new VectorND(Arrays.asList(0.0, 0.0)) } };
		VectorField2D initfield2 = new VectorField2D(2, initfieldarray2);

		for (int x = 0; x < fieldWidth; x++) {
			for (int y = 0; y < fieldHeight; y++) {

				final VectorND pos = new VectorND(Arrays.asList(((double) x) / fieldWidth, ((double) y) / fieldHeight));
				final VectorND v1 = initfield1.interpolateLinear(pos);
				final VectorND v2 = initfield2.interpolateLinear(pos);
				airstreamfieldarray[x][y] = VectorND.add(v1, v2);

			}
		}

		return airstreamField;

	}

	private void initScene(IGraphicsSubsystem graphicsSubsystem) {

		final Consumer<VLEdge2D> plainEdgeRenderer = new PlainEdgeRenderer(graphicsSubsystem);
		final Consumer<VLEdge2D> adjustingEdgeRenderer = new AdjustingEdgeRenderer(graphicsSubsystem);
		final Consumer<VLCuboid2D> cuboidRenderer = new WireMeshCuboidRenderer();
		final Consumer<VLChain2D> chainRenderer = new WireMeshChainRenderer();
		final Consumer<VLChainNet2D> chainNetRenderer = new WireMeshChainNetRenderer();
		final Consumer<VLFacet2D> facetRenderer = new FilledConvexPolygonGSRenderer<VLFacet2D>(graphicsSubsystem);
		final Consumer<VLFacet2D> facetInterpolatingRenderer = new FacetInterpolatingRenderer(graphicsSubsystem, new AdjustingColorProvider());

		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2D(100, 100), new Vector2D(99, 100)), new VLVertex2D(new Vector2D(230, 120)),
				plainEdgeRenderer));
		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2D(100, 100), new Vector2D(101, 100)), new VLVertex2D(new Vector2D(230, 120)),
				plainEdgeRenderer));
		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2D(100, 100), new Vector2D(100, 101)), new VLVertex2D(new Vector2D(230, 120)),
				plainEdgeRenderer));

		final VLVertex2D cuboidHook = new VLVertex2D(new Vector2D(400, 100), new Vector2D(380, 95));
		edgeContainers.add(new VLCuboid2D(cuboidHook, new VLVertex2D(new Vector2D(430, 100)), new VLVertex2D(new Vector2D(430, 130)),
				new VLVertex2D(new Vector2D(400, 130)), cuboidRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLCuboid2D(new VLVertex2D(new Vector2D(450, 100), new Vector2D(410, 105)), new VLVertex2D(new Vector2D(490, 100)),
				new VLVertex2D(new Vector2D(490, 140)), new VLVertex2D(new Vector2D(450, 140)), cuboidRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLCuboid2D(new VLVertex2D(new Vector2D(600, 10), new Vector2D(605, 105)), new VLVertex2D(new Vector2D(700, 10)),
				new VLVertex2D(new Vector2D(700, 140)), new VLVertex2D(new Vector2D(600, 140)), cuboidRenderer, plainEdgeRenderer));

		edgeContainers.add(new VLChain2D(new VLVertex2D(new Vector2D(500, 15)).setPinned(true), new VLVertex2D(new Vector2D(800, 100)), 40,
				chainRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLChain2D(new VLVertex2D(new Vector2D(850, 15)).setPinned(true), cuboidHook, 60, chainRenderer, plainEdgeRenderer));

		edgeContainers.add(new VLFacet2D(new VLVertex2D(new Vector2D(250, 150)), new VLVertex2D(new Vector2D(400, 160)),
				new VLVertex2D(new Vector2D(320, 170)), facetRenderer).setColor(colors[0]));

		edgeContainers.add(new VLFacet2D(new VLVertex2D(new Vector2D(250, 150)), new VLVertex2D(new Vector2D(400, 160)),
				new VLVertex2D(new Vector2D(360, 170)), facetInterpolatingRenderer).setColor(colors[0]));
//
//		edgeContainers.add(new VLTetragon2D(new VLVertex2D(new Vector2D(253, 128)), new VLVertex2D(new Vector2D(553, 126)),
//				new VLVertex2D(new Vector2D(552, 228)), new VLVertex2D(new Vector2D(253, 238)), facetInterpolatingRenderer).setColor(colors[0]));

		chainNet = new VLChainNet2D(new VLVertex2D(new Vector2D(900, 15)).setPinned(true), new VLVertex2D(new Vector2D(1400, 15)).setPinned(true), 30,
				10, 15, 16, chainNetRenderer, adjustingEdgeRenderer).setColor(colors[0]);
		edgeContainers.add(chainNet);

		for (IVLEdgeContainer2D edgeContainer : edgeContainers) {
			edges.addAll(edgeContainer.getEdges());
		}

		for (final VLEdge2D edge : edges) {
			vertices.add(edge.getFirst());
			vertices.add(edge.getSecond());
		}

	}

	private void calculatePhysics(Dimension dimension) {

		if (useWind) {
			windSimulator.progressWindflaw();
		}

		final double width = dimension.getWidth();
		final double height = dimension.getHeight();

		if (useWind && useWindparticles) {

			if (windParticleCnt == 0) {
				windParticles.clear();
			}

			final VectorField2D airstreamField = windSimulator.getAirstreamField();

			if (windParticleCnt % windParticleFrequence == 0) {

				final List<Integer> airstreamdimensions = airstreamField.getDimensions();
				final int windWidth = airstreamdimensions.get(0);
				final int windHeight = airstreamdimensions.get(1);

				windParticles
						.add(new VLVertex2D(new Vector2D(rnd.nextInt(windWidth) * width / windWidth, rnd.nextInt(windHeight) * height / windHeight)));
			} else {

				final Iterator<VLVertex2D> iterator = windParticles.iterator();
				while (iterator.hasNext()) {

					final VLVertex2D windParticle = iterator.next();
					final Vector2D newPosition = calculateNewPosition(windParticle, 1);
					final Vector2D wind = windSimulator.calculateWind(windParticle.getCurrent());
					newPosition.add(wind.mult(1 / wind.length()));

					if (newPosition.x >= width || newPosition.x < 0 || newPosition.y >= height || newPosition.y < 0) {
						iterator.remove();
					} else {
						repositionVertex(windParticle, newPosition);
					}
				}
			}

			if (windParticleCnt == Long.MAX_VALUE) {
				windParticleCnt = 0;
			} else {
				windParticleCnt++;
			}
		}

		synchronized (edgeContainers) {

			vertices.parallelStream().forEach(new Consumer<VLVertex2D>() {

				@Override
				public void accept(VLVertex2D vertex) {

					if (vertex.isPinned()) {
						vertex.setPrevious(vertex.getCurrent());
						return;
					}

					double frictionFac = 1;
					// friction
					if (vertex.getCurrent().y == height - 1) {
						frictionFac = friction;
					}

					final Vector2D newCurrent = calculateNewPosition(vertex, frictionFac);

					if (useGravity) {
						newCurrent.add(gravity);
					}

					if (useWind) {
						newCurrent.add(windSimulator.calculateWind(vertex.getCurrent()));
					}

					repositionVertex(vertex, newCurrent);

				}
			});

			for (int i = 0; i < iterations; i++) {
				handleConstraints(dimension);
			}

		}
	}

	/**
	 * calculates the new position. Uses verlet integration.
	 * 
	 * @param vertex
	 * @param friction
	 * @return
	 */
	public Vector2D calculateNewPosition(VLVertex2D vertex, double friction) {
		return Vector2D.add(vertex.getCurrent(), Vector2D.mult(friction, Vector2D.substract(vertex.getCurrent(), vertex.getPrevious())));
	}

	/**
	 * sets the vertex to the new position
	 * 
	 * @param vertex
	 * @param newCurrent
	 */
	public void repositionVertex(VLVertex2D vertex, final Vector2D newCurrent) {
		vertex.setPrevious(vertex.getCurrent());
		vertex.setCurrent(newCurrent);
	}

	private void handleConstraints(Dimension dimension) {

		vertices.parallelStream().forEach(vertex -> handleBorderConstraints(vertex, dimension));

		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final VLEdge2D edge : edges) {
			handleStickConstraints(edge);
		}

	}

	private void handleStickConstraints(final VLEdge2D edge) {

		final Vector2D cFirst = edge.getFirst().getCurrent();
		final Vector2D cSecond = edge.getSecond().getCurrent();
		Vector2D dV = Vector2D.substract(cFirst, cSecond);
		if (dV.isNullVector()) {
			// Problem!!! no line anymore
			// System.out.println("Nullvector! edge: " + edge);
			// do no adjustment to prevent NaN
			return;
			// dV =
			// Vector2D.max(Vector2D.substract(edge.getFirst().getPrevious(),
			// edge.getSecond().getCurrent()),
			// Vector2D.substract(edge.getSecond().getPrevious(),
			// edge.getFirst().getCurrent()));
			// dV.mult(0.001);
		}

		if (dV.length() != edge.getPreferredLength()) {

			double diff = dV.length() - edge.getPreferredLength();
			Vector2D slackV = Vector2D.mult(diff / dV.length() / 2, dV);

			if (!edge.getFirst().isPinned()) {
				if (edge.getSecond().isPinned()) {
					cFirst.substract(Vector2D.mult(2, slackV));
				} else {
					cFirst.substract(slackV);
				}
			}
			if (!edge.getSecond().isPinned()) {
				if (edge.getFirst().isPinned()) {
					cSecond.add(Vector2D.mult(2, slackV));
				} else {
					cSecond.add(slackV);
				}
			}
		}

	}

	private void handleBorderConstraints(final VLVertex2D vertex, Dimension dimension) {

		final Vector2D current = vertex.getCurrent();
		final Vector2D previous = vertex.getPrevious();

		// bounce
		if (current.x > dimension.getWidth() - 1) {
			current.x = dimension.getWidth() - 1 - ((current.x - (dimension.getWidth() - 1)) * decay);
			previous.x = dimension.getWidth() - 1 - (previous.x - (dimension.getWidth() - 1));
		} else if (current.x < 0) {
			current.x = -current.x * decay;
			previous.x = -previous.x;
		}

		if (current.y > dimension.getHeight() - 1) {
			current.y = dimension.getHeight() - 1 - ((current.y - (dimension.getHeight() - 1)) * decay);
			previous.y = dimension.getHeight() - 1 - (previous.y - (dimension.getHeight() - 1));
		} else if (current.y < 0) {
			current.y = -current.y * decay;
			previous.y = -previous.y;
		}

	}

	@Override
	public void renderSim(IGraphicsSubsystem graphicsSubsystem) {

		if (showAirstreamVectors) {

			final List<Integer> winddimensions = windSimulator.getAirstreamField().getDimensions();
			final int windWidth = winddimensions.get(0);
			final int windHeight = winddimensions.get(1);
			final int scale = 20;
			final int alpha = 255;

			for (int x = 0; x < windWidth; x++) {
				for (int y = 0; y < windHeight; y++) {

					final VectorND windvector = windSimulator.getAirstreamField().getValue(new VectorND(Arrays.asList((double) x, (double) y)));

					int xpos = (int) (((double) x) * getScreenParameters().WIDTH / windWidth);
					int ypos = (int) (((double) y) * getScreenParameters().HEIGHT / windHeight);
					final int xend = xpos + (int) (scale * windvector.get(0));
					final int yend = ypos + (int) (scale * windvector.get(1));
					final Color lineColor = transparent(Color.RED, alpha);
					graphicsSubsystem.drawLine(xpos, ypos, xend, yend, lineColor, Color.GREEN);
					graphicsSubsystem.drawFilledCircle(xend, yend, 2, () -> transparent(Color.ORANGE, alpha));
				}
			}
		}

		if (showAirstream || showWind) {

			final int windWidth = getScreenParameters().WIDTH;
			final int windHeight = getScreenParameters().HEIGHT;
			final int scaleAirstream = 20;
			final int scaleWind = 2;
			final int alpha = Math.min(200, 10 * airstreamdegree);

			final Function<Integer, Integer> randomdistributor = v -> airstreamdegree < 2 ? v
					: v + rnd.nextInt(airstreamdegree / 2) - airstreamdegree / 4;

			final Function<Double, Integer> ccv = length -> (int) (255 * (1 - (1 / (1 + length / 20))));
			for (int posx = rnd.nextInt(airstreamdegree); posx < windWidth; posx += airstreamdegree) {
				for (int posy = rnd.nextInt(airstreamdegree); posy < windHeight; posy += airstreamdegree) {

					int x = randomdistributor.apply(posx);
					int y = randomdistributor.apply(posy);

					if (showAirstream) {
						final VectorND airstreamvector = windSimulator.calculateAirstream(new Vector2D((double) x, (double) y));
						final double airstreamlength = airstreamvector.length();
						final Integer value = ccv.apply(airstreamlength);
						Color colorStart = new Color(Color.RED.getRed(), value, Color.BLUE.getBlue(), alpha);
						Color colorEnd = new Color(Color.RED.getRed(), value, value, alpha);
						graphicsSubsystem.drawLine(x, y, (int) (x + scaleAirstream * airstreamvector.get(0)),
								(int) (y + scaleAirstream * airstreamvector.get(1)), colorStart, colorEnd);
					}

					if (showWind) {
						final Vector2D windvector = windSimulator.calculateWind(new Vector2D((double) x, (double) y));
						final double windlength = windvector.length();
						if (windlength > 0) {
							final Integer value = ccv.apply(windlength);
							Color colorStart = new Color(value, value, Color.BLUE.getBlue(), alpha);
							Color colorEnd = new Color(value, Color.GREEN.getGreen(), value, alpha);
							graphicsSubsystem.drawLine(x, y, (int) (x + scaleWind * windvector.x), (int) (y + scaleWind * windvector.y), colorStart,
									colorEnd);
						}
					}
				}
			}
		}

		if (useWind && useWindparticles) {

			windParticles.stream().forEach(particle -> {
				final int change = 50;
				final Color centerColor = new Color(Color.RED.getRed() - rnd.nextInt(change), 0 + rnd.nextInt(change),
						Color.BLUE.getBlue() - rnd.nextInt(change), 200);
				final Color edgeColor = new Color(centerColor.getRed(), centerColor.getGreen(), centerColor.getBlue(), 1).brighter().brighter()
						.brighter();
				graphicsSubsystem.drawFilledCircle((int) particle.getCurrent().x, (int) particle.getCurrent().y, 2,
						new CUtils.SphericalColorGenerator(centerColor, edgeColor));
			});
		}

		(syncRendering ? dcopyEdgeContainers() : edgeContainers).stream().forEach(IVLEdgeContainer2D::render);

		// show "grab" on vertices
		vertices.stream().filter(vertex -> isHit(mousePoint, vertex) && !isGrabbed(vertex)).forEach(
				vertex -> graphicsSubsystem.drawFilledCircle((int) vertex.getCurrent().x, (int) vertex.getCurrent().y, bobbleSize, () -> Color.RED));

	}

	private Collection<IVLEdgeContainer2D> dcopyEdgeContainers() {

		synchronized (edgeContainers) {
			return edgeContainers.parallelStream().map(container -> container.dcopy()).collect(Collectors.toList());
		}
	}

	private Color transparent(final Color color, final int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	@Override
	public void keyPressed(KeyEvent ke) {

		super.keyPressed(ke);

		if (ke.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
			gravity = GRAV_DOWN;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_UP) {
			gravity = GRAV_UP;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_RIGHT) {
			gravity = GRAV_RIGHT;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_LEFT) {
			gravity = GRAV_LEFT;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_C) {

			colorCycleCnt++;
			if (colorCycleCnt >= colors.length) {
				colorCycleCnt = 0;
			}

			chainNet.setColor(colors[colorCycleCnt]);
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private boolean isHit(final Vector2D point, final VLVertex2D vertex) {
		return Vector2D.distance(point, vertex.getCurrent()) <= bobbleSize;
	}

	@Override
	public void mousePressed(MouseEvent meEvent) {

		setMousePoint(meEvent);

		if (isShift()) {

		} else {

			for (VLVertex2D vertex : vertices) {
				if (isHit(mousePoint, vertex)) {
					grabbedVertices.add(vertex);
				}
			}

			final Vector2D mPoint = new Vector2D(mousePoint);
			for (VLVertex2D vertex : grabbedVertices) {

				vertex.setPinned(true);
				vertex.setCurrent(mPoint);
				vertex.setPrevious(mPoint);

			}
		}

		mousePressed = true;

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		mousePressed = false;

		setMousePoint(e);

		if (isShift()) {

		} else {

			final Vector2D mPoint = new Vector2D(mousePoint);
			if (e.getButton() == MouseEvent.BUTTON1) {
				// unpin
				for (VLVertex2D vertex : grabbedVertices) {
					vertex.setPinned(false);
					vertex.setCurrent(mPoint);
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				// glue together
				for (VLVertex2D vertex : grabbedVertices) {
					for (VLEdge2D edge : edges) {
						if (isHit(vertex.getCurrent(), edge.getFirst())) {
							if (!isHit(vertex.getCurrent(), edge.getSecond())) {
								// hm, is it really necessary???
								edge.setFirst(vertex);
							}
						}
						if (isHit(vertex.getCurrent(), edge.getSecond())) {
							if (!isHit(vertex.getCurrent(), edge.getFirst())) {
								edge.setSecond(vertex);
							}
						}
					}
				}
			}

			grabbedVertices.clear();

		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		setMousePoint(e);

		if (mousePressed) {

			if (isShift()) {

				if (showAirstream) {
					final VectorField2D airstreamField = windSimulator.getAirstreamField();
					final List<Integer> dimensions = airstreamField.getDimensions();
					final Integer fieldwidth = dimensions.get(0);
					final Integer fieldheight = dimensions.get(1);
					for (int x = 0; x < fieldwidth; x++) {
						for (int y = 0; y < fieldheight; y++) {

							final VectorND fieldpos = new VectorND(Arrays.asList((double) x, (double) y));
							final VectorND realpos = new VectorND(Arrays.asList(((double) x) * getScreenParameters().WIDTH / fieldwidth,
									((double) y) * getScreenParameters().HEIGHT / fieldheight));
							final VectorND diffVector = VectorND.substract(new VectorND(Arrays.asList(mousePoint.x, mousePoint.y)), realpos);
							VectorND dirVec = VectorND.normalize(diffVector);
							dirVec = dirVec.isNullVector() ? dirVec : dirVec.mult(100 / Math.pow(diffVector.length(), 2));
							airstreamField.setValue(fieldpos, VectorND.add(dirVec, airstreamField.getValue(fieldpos)));
						}
					}
				}

			} else {

				final Vector2D mPoint = new Vector2D(mousePoint);
				for (VLVertex2D vertex : grabbedVertices) {
					vertex.setCurrent(mPoint);
				}

			}
		}
	}

	private boolean isGrabbed(VLVertex2D vertex) {
		return grabbedVertices.contains(vertex);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setMousePoint(e);
	}

	private void setMousePoint(MouseEvent e) {

		mousePoint.x = e.getX();
		mousePoint.y = e.getY();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub

	}

	private void initKeyActions() {
		addKeyAction(KeyEvent.VK_W, new IKeyAction() {

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
				return TXT_ID_WIND;
			}

			@Override
			public String text() {
				return "wind";
			}

			@Override
			public void plus() {
				useWind = true;
			}

			@Override
			public void minus() {
				useWind = false;
				windSimulator.resetWindflaw();
			}

			@Override
			public String getValue() {
				return String.valueOf(useWind);
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
				return TXT_ID_FILLED;
			}

			@Override
			public String text() {
				return "filled";
			}

			@Override
			public void plus() {
				chainNet.setRenderer(new SmoothFilledChainNetRenderer(getGraphicsSubsystem()));
				filled = true;
			}

			@Override
			public void minus() {
				chainNet.setRenderer(new WireMeshChainNetRenderer());
				filled = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(filled);
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
				return TXT_ID_GRAVITY;
			}

			@Override
			public String text() {
				return "gravity";
			}

			@Override
			public void plus() {
				useGravity = true;
			}

			@Override
			public void minus() {
				useGravity = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(useGravity);
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
				return TXT_ID_SHOWAIRSTREAMVECTORS;
			}

			@Override
			public String text() {
				return "show airstreamvectors";
			}

			@Override
			public void plus() {
				showAirstreamVectors = true;
			}

			@Override
			public void minus() {
				showAirstreamVectors = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(showAirstreamVectors);
			}
		});
		addKeyAction(KeyEvent.VK_A, new IKeyAction() {

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
				return TXT_ID_SHOWAIRSTREAM;
			}

			@Override
			public String text() {
				return "show airstream";
			}

			@Override
			public void plus() {
				showAirstream = true;
			}

			@Override
			public void minus() {
				showAirstream = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(showAirstream);
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
				return TXT_ID_SHOWWIND;
			}

			@Override
			public String text() {
				return "show wind";
			}

			@Override
			public void plus() {
				showWind = true;
			}

			@Override
			public void minus() {
				showWind = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(showWind);
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
				return TXT_ID_AIRSTREAMDEGREE;
			}

			@Override
			public String text() {
				return "degree of wind visualization";
			}

			@Override
			public void plus() {
				airstreamdegree++;
			}

			@Override
			public void minus() {
				if (airstreamdegree > 1) {
					airstreamdegree--;
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(airstreamdegree);
			}
		});
		addKeyAction(KeyEvent.VK_R, new IKeyAction() {

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
				return TXT_ID_RATE_AIRSTREAMCHANGE;
			}

			@Override
			public String text() {
				return "rate of airstream change";
			}

			@Override
			public void plus() {
				rateOfAirstreamChange++;
				windSimulator.setRateOfAirstreamChange(rateOfAirstreamChange);
			}

			@Override
			public void minus() {
				if (rateOfAirstreamChange > 0) {
					rateOfAirstreamChange--;
					windSimulator.setRateOfAirstreamChange(rateOfAirstreamChange);
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(rateOfAirstreamChange);
			}
		});
		addKeyAction(KeyEvent.VK_P, new IKeyAction() {

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
				return TXT_ID_WIND_PARTICLES;
			}

			@Override
			public String text() {
				return "use wind particles";
			}

			@Override
			public void plus() {
				useWindparticles = true;
			}

			@Override
			public void minus() {
				useWindparticles = false;
				windParticleCnt = 0;
			}

			@Override
			public String getValue() {
				return String.valueOf(useWindparticles);
			}
		});
		addKeyAction(KeyEvent.VK_I, new IKeyAction() {

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
				return TXT_ID_ITERATIONS;
			}

			@Override
			public String text() {
				return "iterations";
			}

			@Override
			public void plus() {
				iterations++;
			}

			@Override
			public void minus() {
				if (iterations > 1) {
					iterations--;
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(iterations);
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
				return TXT_SYNC_RENDERING;
			}

			@Override
			public String text() {
				return "synchronize rendering";
			}

			@Override
			public void plus() {
				syncRendering = true;
			}

			@Override
			public void minus() {
				syncRendering = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(syncRendering);
			}
		});
		addKeyAction(KeyEvent.VK_N, new IKeyAction() {

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
				return TXT_ID_AIRSTREAMINTENSITY;
			}

			@Override
			public String text() {
				return "airstream intensity";
			}

			@Override
			public void plus() {
				modifyAirstreamIntensity(1.1);
			}

			@Override
			public void minus() {
				modifyAirstreamIntensity(0.9);
			}

			@Override
			public String getValue() {
				return String.valueOf(
						windSimulator.getAirstreamField().asList().stream().collect(Collectors.summarizingDouble(VectorND::length)).getAverage());
			}

			private void modifyAirstreamIntensity(final double modificator) {

				final VectorField2D airstreamField = windSimulator.getAirstreamField();
				final List<Integer> dimensions = airstreamField.getDimensions();
				for (int x = 0; x < dimensions.get(0); x++) {
					for (int y = 0; y < dimensions.get(1); y++) {
						final VectorND pos = new VectorND(Arrays.asList((double) x, (double) y));
						airstreamField.setValue(pos, VectorND.mult(modificator, airstreamField.getValue(pos)));
					}
				}
			}
		});
	}
}
