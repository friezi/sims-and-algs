/**
 * 
 */
package de.zintel.sim.ragdoll;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.zintel.control.IKeyAction;
import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.g2d.verlet.AdjustingColorProvider;
import de.zintel.gfx.g2d.verlet.IVLEdgeContainer2D;
import de.zintel.gfx.g2d.verlet.VLChain2D;
import de.zintel.gfx.g2d.verlet.VLChainNet2D;
import de.zintel.gfx.g2d.verlet.VLCuboid2D;
import de.zintel.gfx.g2d.verlet.VLEdge2D;
import de.zintel.gfx.g2d.verlet.VLFacet2D;
import de.zintel.gfx.g2d.verlet.VLFacetChainNet2D;
import de.zintel.gfx.g2d.verlet.VLTetragon2D;
import de.zintel.gfx.g2d.verlet.VLVertex2D;
import de.zintel.gfx.g2d.verlet.VLVertexSkid;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.AVectorND;
import de.zintel.math.Vector2D;
import de.zintel.math.Vector2DPlain;
import de.zintel.math.VectorField2D;
import de.zintel.physics.simulators.WindController;
import de.zintel.physics.simulators.WindSimulator;
import de.zintel.sim.SimulationScreen;
import de.zintel.verlet.DfltStickConstraintHandler;
import de.zintel.verlet.MeanAdjustingStickConstraintHandler;
import de.zintel.verlet.MeanAdjustingStickConstraintHandlerNoMap;
import de.zintel.verlet.VerletEngine;
import de.zintel.verlet.VertexBorderConstraintHandler;

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

	private static final double GRAV_INFLUENCE = 0.8;

	private static final Vector2DPlain GRAV_DOWN = new Vector2DPlain(0, GRAV_INFLUENCE);

	private static final Vector2DPlain GRAV_UP = new Vector2DPlain(0, -GRAV_INFLUENCE);

	private static final Vector2DPlain GRAV_RIGHT = new Vector2DPlain(GRAV_INFLUENCE, 0);

	private static final Vector2DPlain GRAV_LEFT = new Vector2DPlain(-GRAV_INFLUENCE, 0);

	private final Random rnd = new Random(Instant.now().toEpochMilli());

	private final double decay = 0.3;

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

	private Vector2DPlain gravity = GRAV_DOWN;

	private volatile boolean mousePressed = false;

	private final Collection<VLVertexSkid> grabbedVertices = Collections.synchronizedCollection(new ArrayList<>());

	private Vector2DPlain mousePoint = new Vector2DPlain();

	private VLChainNet2D chainNet;

	private VLFacetChainNet2D facetChainNet;

	private final Color[] colors = { Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.GRAY, Color.CYAN, Color.MAGENTA,
			Color.PINK };

	private int colorCycleCnt = 0;

	@SuppressWarnings("serial")
	private final List<BiConsumer<Collection<VLEdge2D>, Integer>> stickConstraintHandlers = new ArrayList<BiConsumer<Collection<VLEdge2D>, Integer>>() {
		{
			add(new DfltStickConstraintHandler());
			add(new MeanAdjustingStickConstraintHandler());
			add(new MeanAdjustingStickConstraintHandlerNoMap());
		}
	};

	private int stickConstraintHandlerCount = 0;

	private WindSimulator windSimulator;

	private WindController windController;

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

	private VertexBorderConstraintHandler vertexConstraintHandler;

	private BiConsumer<Collection<VLEdge2D>, Integer> edgesHandler = stickConstraintHandlers.get(stickConstraintHandlerCount);

	private VerletEngine verletEngine;

	private Dimension dimension = new Dimension();

	private static class PlainEdgeRenderer implements Consumer<VLEdge2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		public PlainEdgeRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void accept(VLEdge2D edge) {
			graphicsSubsystem.drawLine((int) edge.getFirst().getVertex().getCurrent().x, (int) edge.getFirst().getVertex().getCurrent().y,
					(int) edge.getSecond().getVertex().getCurrent().x, (int) edge.getSecond().getVertex().getCurrent().y, edge.getColor(),
					edge.getColor());
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
			graphicsSubsystem.drawLine((int) edge.getFirst().getVertex().getCurrent().x, (int) edge.getFirst().getVertex().getCurrent().y,
					(int) edge.getSecond().getVertex().getCurrent().x, (int) edge.getSecond().getVertex().getCurrent().y, color, color);
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

	private static class WireMeshFacetChainNetRenderer implements Consumer<VLFacetChainNet2D> {

		@Override
		public void accept(VLFacetChainNet2D item) {
			for (VLEdge2D edge : item.getEdges()) {
				edge.render();
			}
			for (Collection<VLEdge2D> subedges : item.getSublayerEdges()) {
				for (VLEdge2D edge : subedges) {
					edge.render();
				}
			}
		}
	}

	private static class FilledFacetChainNetRenderer implements Consumer<VLFacetChainNet2D> {

		private final Consumer<VLFacet2D> facetRenderer;

		public FilledFacetChainNetRenderer(IGraphicsSubsystem graphicsSubsystem) {
			facetRenderer = new FilledConvexPolygonGSRenderer<VLFacet2D>(graphicsSubsystem);
			// facetRenderer = new
			// PolygonInterpolatingRenderer<VLFacet2D>(graphicsSubsystem, new
			// AdjustingColorProvider()).setContextEdgesProvider(facet->facet.getEdges());
		}

		@Override
		public void accept(VLFacetChainNet2D item) {

			for (VLFacet2D facet : item.getFacets()) {
				facet.setRenderer(facetRenderer);
				facet.render();
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

	private Collection<VLVertexSkid> vertices = new LinkedHashSet<>();

	private final Collection<VLVertex2D> windParticles = new ConcurrentLinkedQueue<>();

	public SimRagdollPhysics(EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("Ragdoll physics", gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		Collection<VerletEngine> sublayerEngines = initScene(graphicsSubsystem);
		windSimulator = new WindSimulator(initAirstreamField(), getScreenParameters()).setRateOfAirstreamChange(rateOfAirstreamChange);
		windController = new WindController(windSimulator).setDoWind(useWind);

		verletEngine = new VerletEngine(vertices, edges, edgeContainers, iterations);
		verletEngine.addProgressor(windController);
		verletEngine.addInfluenceVectorProvider(
				(c, n) -> (c.y == dimension.height - 1 ? Vector2DPlain.mult(1 - friction, Vector2DPlain.substract(c, n))
						: Vector2DPlain.NULL_VECTOR));
		verletEngine.addInfluenceVectorProvider(windController);
		verletEngine.addInfluenceVectorProvider((c, n) -> gravity);

		vertexConstraintHandler = new VertexBorderConstraintHandler(0, dimension.getWidth() - 1, 0, dimension.getHeight() - 1, decay);
		verletEngine.setVertexConstraintHandler(vertexConstraintHandler);
		verletEngine.addEngines(sublayerEngines);
		verletEngine.setEdgesHandler(edgesHandler);

		initKeyActions();

	}

	@Override
	protected void calculate(Dimension dimension) throws Exception {

		this.dimension = dimension;
		vertexConstraintHandler.setXmax(dimension.getWidth() - 1).setYmax(dimension.getHeight() - 1);

		verletEngine.progress();
		// Thread.sleep(5000);

	}

	@Override
	protected void shutdown() throws Exception {

	}

	private VectorField2D<Vector2D> initAirstreamField() {

		final int fieldWidth = getScreenParameters().WIDTH / 80;
		final int fieldHeight = getScreenParameters().HEIGHT / 80;
		Vector2D[][] airstreamfieldarray = new Vector2D[fieldWidth][fieldHeight];
		final VectorField2D<Vector2D> airstreamField = new VectorField2D<Vector2D>(2, airstreamfieldarray);

		final double max1 = 1.2;
		Vector2D[][] initfieldarray1 = { { new Vector2D(Arrays.asList(0.0, 0.0)), new Vector2D(Arrays.asList(0.0, max1)) },
				{ new Vector2D(Arrays.asList(max1, 0.0)), new Vector2D(Arrays.asList(max1, max1)) } };
		VectorField2D<Vector2D> initfield1 = new VectorField2D<Vector2D>(2, initfieldarray1);

		final double max2 = 0.2;
		Vector2D[][] initfieldarray2 = { { new Vector2D(Arrays.asList(max2, 0.0)), new Vector2D(Arrays.asList(0.0, 0.0)) },
				{ new Vector2D(Arrays.asList(max2 / 2, -max2 / 2)), new Vector2D(Arrays.asList(0.0, 0.0)) } };
		VectorField2D<Vector2D> initfield2 = new VectorField2D<Vector2D>(2, initfieldarray2);

		for (int x = 0; x < fieldWidth; x++) {
			for (int y = 0; y < fieldHeight; y++) {

				final Vector2D pos = new Vector2D(Arrays.asList(((double) x) / fieldWidth, ((double) y) / fieldHeight));
				final Vector2D v1 = initfield1.interpolateLinear(pos, pos);
				final Vector2D v2 = initfield2.interpolateLinear(pos, pos);
				airstreamfieldarray[x][y] = AVectorND.add(v1, v2);

			}
		}

		return airstreamField;

	}

	private Collection<VerletEngine> initScene(IGraphicsSubsystem graphicsSubsystem) {

		final Collection<VerletEngine> sublayerEngines = new LinkedList<>();

		final Consumer<VLEdge2D> plainEdgeRenderer = new PlainEdgeRenderer(graphicsSubsystem);
		final Consumer<VLEdge2D> adjustingEdgeRenderer = new AdjustingEdgeRenderer(graphicsSubsystem);
		final Consumer<VLCuboid2D> cuboidRenderer = new WireMeshCuboidRenderer();
		final Consumer<VLChain2D> chainRenderer = new WireMeshChainRenderer();
		final Consumer<VLChainNet2D> chainNetRenderer = new WireMeshChainNetRenderer();
		final Consumer<VLFacetChainNet2D> facetChainNetRenderer = new WireMeshFacetChainNetRenderer();
		final Consumer<VLFacet2D> facetRenderer = new FilledConvexPolygonGSRenderer<VLFacet2D>(graphicsSubsystem);
		final Consumer<VLFacet2D> facetInterpolatingRenderer = new PolygonInterpolatingRenderer<VLFacet2D>(graphicsSubsystem,
				new AdjustingColorProvider()).setContextEdgesProvider(facet -> facet.getEdges());
		final Consumer<VLTetragon2D> tetragonFacetInterpolatingRenderer = new TetragonFacetInterpolatingRenderer(
				facetInterpolatingRenderer);
		final Consumer<VLTetragon2D> tetragonFullInterpolatingFacetRenderer = new TetragonFullInterpolatingFacetRenderer(graphicsSubsystem,
				new AdjustingColorProvider());

		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2DPlain(100, 100), new Vector2DPlain(99, 100)),
				new VLVertex2D(new Vector2DPlain(230, 120)), Color.WHITE, plainEdgeRenderer));
		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2DPlain(100, 100), new Vector2DPlain(101, 100)),
				new VLVertex2D(new Vector2DPlain(230, 120)), Color.WHITE, plainEdgeRenderer));
		edgeContainers.add(new VLEdge2D(new VLVertex2D(new Vector2DPlain(100, 100), new Vector2DPlain(100, 101)),
				new VLVertex2D(new Vector2DPlain(230, 120)), Color.WHITE, plainEdgeRenderer));

		final VLVertexSkid cuboidHook = new VLVertexSkid(new VLVertex2D(new Vector2DPlain(400, 100), new Vector2DPlain(380, 95)));
		edgeContainers.add(new VLCuboid2D(cuboidHook, new VLVertexSkid(new VLVertex2D(new Vector2DPlain(430, 100))),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(430, 130))),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(400, 130))), cuboidRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLCuboid2D(new VLVertex2D(new Vector2DPlain(450, 100), new Vector2DPlain(410, 105)),
				new VLVertex2D(new Vector2DPlain(490, 100)), new VLVertex2D(new Vector2DPlain(490, 140)),
				new VLVertex2D(new Vector2DPlain(450, 140)), cuboidRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLCuboid2D(new VLVertex2D(new Vector2DPlain(600, 10), new Vector2DPlain(605, 105)),
				new VLVertex2D(new Vector2DPlain(700, 10)), new VLVertex2D(new Vector2DPlain(700, 140)),
				new VLVertex2D(new Vector2DPlain(600, 140)), cuboidRenderer, plainEdgeRenderer));

		edgeContainers.add(new VLChain2D(new VLVertexSkid(new VLVertex2D(new Vector2DPlain(500, 15))).setSticky(true),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(800, 100))), 40, chainRenderer, plainEdgeRenderer));
		edgeContainers.add(new VLChain2D(new VLVertexSkid(new VLVertex2D(new Vector2DPlain(850, 15))).setSticky(true), cuboidHook, 60,
				chainRenderer, plainEdgeRenderer));

		edgeContainers.add(new VLFacet2D(new VLVertexSkid(new VLVertex2D(new Vector2DPlain(250, 150))),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(400, 160))),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(320, 170))), facetRenderer).setColor(colors[0]));
		//
		// edgeContainers.add(new VLFacet2D(new VLVertexSkid(new VLVertex2D(new
		// Vector2D(250, 150))),
		// new VLVertexSkid(new VLVertex2D(new Vector2D(400, 160))), new
		// VLVertexSkid(new VLVertex2D(new Vector2D(360, 170))),
		// facetInterpolatingRenderer).setColor(colors[0]));
		//
		// edgeContainers.add(new VLTetragon2D(new VLVertexSkid(new
		// VLVertex2D(new Vector2D(253, 128))),
		// new VLVertexSkid(new VLVertex2D(new Vector2D(553, 126))), new
		// VLVertexSkid(new VLVertex2D(new Vector2D(552, 228))),
		// new VLVertexSkid(new VLVertex2D(new Vector2D(253, 238))),
		// tetragonFullInterpolatingFacetRenderer).setColor(colors[0]));

		chainNet = new VLChainNet2D(new VLVertexSkid(new VLVertex2D(new Vector2DPlain(300, 15))).setSticky(true),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(800, 15))).setSticky(true), 30, 10, 15, 16, chainNetRenderer,
				adjustingEdgeRenderer).setColor(colors[0]);
		edgeContainers.add(chainNet);
		facetChainNet = new VLFacetChainNet2D(new VLVertexSkid(new VLVertex2D(new Vector2DPlain(900, 15))).setSticky(true),
				new VLVertexSkid(new VLVertex2D(new Vector2DPlain(1400, 15))).setSticky(true), 30,
				/* 10 */4, 15, 24, facetChainNetRenderer, adjustingEdgeRenderer).setColor(colors[0]);
		edgeContainers.add(facetChainNet);
		{

			Collection<Collection<VLEdge2D>> sublayerEdges = facetChainNet.getSublayerEdges();
			Collection<VLEdge2D> inneredges = new ArrayList<>();
			for (Collection<VLEdge2D> edges : sublayerEdges) {
				inneredges.addAll(edges);
			}

			Collection<VLVertexSkid> innerVertices = new ArrayList<>();

			for (final VLEdge2D edge : inneredges) {
				innerVertices.add(edge.getFirst());
				innerVertices.add(edge.getSecond());
			}
			sublayerEngines.add(new VerletEngine(innerVertices, inneredges, edgeContainers, 5)
					.setEdgesHandler(edgesHandler)/*
													 * .addInfluenceVectorProvider
													 * ((c, n) -> gravity)
													 */);
		}

		for (IVLEdgeContainer2D edgeContainer : edgeContainers) {
			edges.addAll(edgeContainer.getEdges());
		}

		for (final VLEdge2D edge : edges) {
			vertices.add(edge.getFirst());
			vertices.add(edge.getSecond());
		}

		return sublayerEngines;

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

					final Vector2D windvector = windSimulator.getAirstreamField()
							.getValue(new Vector2D(Arrays.asList((double) x, (double) y)));

					int xpos = (int) (((double) x) * getScreenParameters().WIDTH / windWidth);
					int ypos = (int) (((double) y) * getScreenParameters().HEIGHT / windHeight);
					final int xend = xpos + (int) (scale * windvector.get(0));
					final int yend = ypos + (int) (scale * windvector.get(1));
					final Color lineColor = CUtils.transparent(Color.RED, alpha);
					graphicsSubsystem.drawLine(xpos, ypos, xend, yend, lineColor, Color.GREEN);
					graphicsSubsystem.drawFilledCircle(xend, yend, 2, () -> CUtils.transparent(Color.ORANGE, alpha));
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
						final Vector2D airstreamvector = windSimulator.calculateAirstream(new Vector2DPlain((double) x, (double) y));
						final double airstreamlength = airstreamvector.length();
						final Integer value = ccv.apply(airstreamlength);
						Color colorStart = new Color(Color.RED.getRed(), value, Color.BLUE.getBlue(), alpha);
						Color colorEnd = new Color(Color.RED.getRed(), value, value, alpha);
						graphicsSubsystem.drawLine(x, y, (int) (x + scaleAirstream * airstreamvector.get(0)),
								(int) (y + scaleAirstream * airstreamvector.get(1)), colorStart, colorEnd);
					}

					if (showWind) {
						final Vector2DPlain windvector = windSimulator.calculateWind(new Vector2DPlain((double) x, (double) y));
						final double windlength = windvector.length();
						if (windlength > 0) {
							final Integer value = ccv.apply(windlength);
							Color colorStart = new Color(value, value, Color.BLUE.getBlue(), alpha);
							Color colorEnd = new Color(value, Color.GREEN.getGreen(), value, alpha);
							graphicsSubsystem.drawLine(x, y, (int) (x + scaleWind * windvector.x), (int) (y + scaleWind * windvector.y),
									colorStart, colorEnd);
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
				final Color edgeColor = new Color(centerColor.getRed(), centerColor.getGreen(), centerColor.getBlue(), 1).brighter()
						.brighter().brighter();
				graphicsSubsystem.drawFilledCircle((int) particle.getCurrent().x, (int) particle.getCurrent().y, 2,
						new CUtils.SphericalColorGenerator(centerColor, edgeColor));
			});
		}

		(syncRendering ? dcopyEdgeContainers() : edgeContainers).stream().forEach(IVLEdgeContainer2D::render);

		// show "grab" on vertices
		vertices.stream().filter(vertex -> isHit(mousePoint, vertex.getVertex()) && !isGrabbed(vertex.getVertex()))
				.forEach(vertex -> graphicsSubsystem.drawFilledCircle((int) vertex.getVertex().getCurrent().x,
						(int) vertex.getVertex().getCurrent().y, bobbleSize, () -> Color.RED));

	}

	private Collection<IVLEdgeContainer2D> dcopyEdgeContainers() {

		synchronized (edgeContainers) {
			return edgeContainers.parallelStream().map(container -> container.dcopy()).collect(Collectors.toList());
		}
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
		}

	}

	private boolean isHit(final Vector2DPlain point, final VLVertex2D vertex) {
		return Vector2DPlain.distance(point, vertex.getCurrent()) <= bobbleSize;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		super.mousePressed(e);

		setMousePoint(e);

		if (isShift()) {

		} else {

			for (VLVertexSkid vertex : vertices) {
				if (isHit(mousePoint, vertex.getVertex())) {
					grabbedVertices.add(vertex);
				}
			}

			final Vector2DPlain mPoint = new Vector2DPlain(mousePoint);
			for (VLVertexSkid vertex : grabbedVertices) {

				vertex.setSticky(true);
				vertex.getVertex().setCurrent(mPoint);
				vertex.getVertex().setPrevious(mPoint);

			}
		}

		mousePressed = true;

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		super.mouseReleased(e);

		mousePressed = false;

		setMousePoint(e);

		if (isShift()) {

		} else {

			final Vector2DPlain mPoint = new Vector2DPlain(mousePoint);
			if (e.getButton() == MouseEvent.BUTTON1) {
				// unpin
				for (VLVertexSkid vertex : grabbedVertices) {
					vertex.setSticky(false);
					vertex.getVertex().setCurrent(mPoint);
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				// glue together
				for (VLVertexSkid vertex : grabbedVertices) {
					for (VLEdge2D edge : edges) {
						if (isHit(vertex.getVertex().getCurrent(), edge.getFirst().getVertex())) {
							if (!isHit(vertex.getVertex().getCurrent(), edge.getSecond().getVertex())) {
								// hm, is it really necessary???
								edge.setFirst(vertex);
							}
						}
						if (isHit(vertex.getVertex().getCurrent(), edge.getSecond().getVertex())) {
							if (!isHit(vertex.getVertex().getCurrent(), edge.getFirst().getVertex())) {
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

		super.mouseDragged(e);

		setMousePoint(e);

		if (mousePressed) {

			if (isShift()) {

				if (showAirstream) {
					final VectorField2D<Vector2D> airstreamField = windSimulator.getAirstreamField();
					final List<Integer> dimensions = airstreamField.getDimensions();
					final Integer fieldwidth = dimensions.get(0);
					final Integer fieldheight = dimensions.get(1);
					for (int x = 0; x < fieldwidth; x++) {
						for (int y = 0; y < fieldheight; y++) {

							final Vector2D fieldpos = new Vector2D(Arrays.asList((double) x, (double) y));
							final Vector2D realpos = new Vector2D(Arrays.asList(((double) x) * getScreenParameters().WIDTH / fieldwidth,
									((double) y) * getScreenParameters().HEIGHT / fieldheight));
							final Vector2D diffVector = Vector2D.substract(new Vector2D(Arrays.asList(mousePoint.x, mousePoint.y)),
									realpos);
							Vector2D dirVec = Vector2D.normalize(diffVector);
							dirVec = dirVec.isNullVector() ? dirVec : dirVec.mult(100 / Math.pow(diffVector.length(), 2));
							airstreamField.setValue(fieldpos, AVectorND.add(dirVec, airstreamField.getValue(fieldpos)));
						}
					}
				}

			} else {

				final Vector2DPlain mPoint = new Vector2DPlain(mousePoint);
				for (VLVertexSkid vertex : grabbedVertices) {
					vertex.getVertex().setCurrent(mPoint);
				}

			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		super.mouseMoved(e);

		setMousePoint(e);
	}

	private boolean isGrabbed(VLVertex2D vertex) {
		return grabbedVertices.contains(vertex);
	}

	private void setMousePoint(MouseEvent e) {

		mousePoint.x = e.getX();
		mousePoint.y = e.getY();

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
				windController.setDoWind(true);
			}

			@Override
			public void minus() {
				windController.setDoWind(false);
				windSimulator.resetWindflaw();
			}

			@Override
			public String getValue() {
				return String.valueOf(windController.isDoWind());
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
				if (chainNet != null) {
					chainNet.setRenderer(new SmoothFilledChainNetRenderer(getGraphicsSubsystem()));
				}
				if (facetChainNet != null) {
					facetChainNet.setRenderer(new FilledFacetChainNetRenderer(getGraphicsSubsystem()));
				}
				filled = true;
			}

			@Override
			public void minus() {
				if (chainNet != null) {
					chainNet.setRenderer(new WireMeshChainNetRenderer());
				}
				if (facetChainNet != null) {
					facetChainNet.setRenderer(new WireMeshFacetChainNetRenderer());
				}
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
		addKeyAction(KeyEvent.VK_C, new IKeyAction() {

			private final static String ID = "stick constraint handler";

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
				return ID;
			}

			@Override
			public String text() {
				return ID;
			}

			@Override
			public void plus() {
				stickConstraintHandlerCount++;
				if (stickConstraintHandlerCount >= stickConstraintHandlers.size()) {
					stickConstraintHandlerCount = 0;
				}
				verletEngine.setEdgesHandlerForAll(stickConstraintHandlers.get(stickConstraintHandlerCount));
			}

			@Override
			public void minus() {
				stickConstraintHandlerCount--;
				if (stickConstraintHandlerCount < 0) {
					stickConstraintHandlerCount = stickConstraintHandlers.size() - 1;
				}
				verletEngine.setEdgesHandlerForAll(stickConstraintHandlers.get(stickConstraintHandlerCount));
			}

			@Override
			public String getValue() {
				return stickConstraintHandlers.get(stickConstraintHandlerCount).getClass().getSimpleName();
			}
		});
		addKeyAction(KeyEvent.VK_L, new IKeyAction() {

			private final static String ID = "color";

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
				return ID;
			}

			@Override
			public String text() {
				return ID;
			}

			@Override
			public void plus() {

				colorCycleCnt++;
				if (colorCycleCnt >= colors.length) {
					colorCycleCnt = 0;
				}

				if (chainNet != null) {
					chainNet.setColor(colors[colorCycleCnt]);
				}
				if (facetChainNet != null) {
					facetChainNet.setColor(colors[colorCycleCnt]);
				}
			}

			@Override
			public void minus() {

				colorCycleCnt--;
				if (colorCycleCnt < 0) {
					colorCycleCnt = colors.length - 1;
				}

				if (chainNet != null) {
					chainNet.setColor(colors[colorCycleCnt]);
				}
				if (facetChainNet != null) {
					facetChainNet.setColor(colors[colorCycleCnt]);
				}
			}

			@Override
			public String getValue() {
				return CUtils.toString(colors[colorCycleCnt]);
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
				verletEngine.setIterations(verletEngine.getIterations() + 1);
			}

			@Override
			public void minus() {
				if (verletEngine.getIterations() > 1) {
					verletEngine.setIterations(verletEngine.getIterations() - 1);
				}
			}

			@Override
			public String getValue() {
				return String.valueOf(verletEngine.getIterations());
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
				return String.valueOf(windSimulator.getAirstreamField().asList().stream()
						.collect(Collectors.summarizingDouble(AVectorND::length)).getAverage());
			}

			private void modifyAirstreamIntensity(final double modificator) {

				final VectorField2D<Vector2D> airstreamField = windSimulator.getAirstreamField();
				final List<Integer> dimensions = airstreamField.getDimensions();
				for (int x = 0; x < dimensions.get(0); x++) {
					for (int y = 0; y < dimensions.get(1); y++) {
						final Vector2D pos = new Vector2D(Arrays.asList((double) x, (double) y));
						airstreamField.setValue(pos, AVectorND.mult(modificator, airstreamField.getValue(pos)));
					}
				}
			}
		});
	}
}
