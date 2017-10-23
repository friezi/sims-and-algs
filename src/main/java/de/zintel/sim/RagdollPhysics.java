/**
 * 
 */
package de.zintel.sim;

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

import de.zintel.control.IKeyAction;
import de.zintel.gfx.Coordination;
import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.g2d.Chain2D;
import de.zintel.gfx.g2d.ChainNet2D;
import de.zintel.gfx.g2d.Cuboid2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.IEdgeContainer2D;
import de.zintel.gfx.g2d.IRenderer;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.gfx.g2d.Vertex2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.VectorField2D;
import de.zintel.math.VectorND;
import de.zintel.physics.simulators.WindSimulator;

/**
 * @author friedemann.zintel
 *
 */
public class RagdollPhysics extends SimulationScreen {

	private static final boolean doRecord = false;

	private static final String recordFilename = "D:/wind-sim.mpg";

	private static final int recordingRate = 2;

	private static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final int iterations = 40;

	private static final int bobbleSize = 3;

	private static final Vector2D GRAV_DOWN = new Vector2D(0, 0.8);

	private static final Vector2D GRAV_UP = new Vector2D(0, -0.8);

	private static final Vector2D GRAV_RIGHT = new Vector2D(0.8, 0);

	private static final Vector2D GRAV_LEFT = new Vector2D(-0.8, 0);

	private Vector2D gravity = GRAV_DOWN;

	private final Random rnd = new Random(Instant.now().toEpochMilli());

	private final double decay = 0.2;

	private final double friction = 0.999;

	private final static Coordination COORDINATION = new Coordination();

	private final static String TXT_ID_WIND = "wind";

	private final static String TXT_ID_FILLED = "filled";

	private final static String TXT_ID_GRAVITY = "gravity";

	private final static String TXT_ID_SHOWAIRSTREAMVECTORS = "showairstreamvectors";

	private final static String TXT_ID_SHOWAIRSTREAM = "showairstream";

	private final static String TXT_ID_SHOWWIND = "showwind";

	private final static String TXT_ID_AIRSTREAMDEGREE = "airstreamdegree";

	private final static String TXT_ID_RATE_AIRSTREAMCHANGE = "rate_airstreamchange";

	private final static String TXT_ID_WIND_PARTICLES = "windparticles";

	private volatile boolean mousePressed = false;

	private final Collection<Vertex2D> grabbedVertices = Collections.synchronizedCollection(new ArrayList<>());

	private Vector2D mousePoint = new Vector2D();

	private ChainNet2D chainNet;

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

	private final int windParticleFrequence = 10;

	private long windParticleCnt = 0;

	private int airstreamdegree = 6;

	private int rateOfAirstreamChange = 1;

	private static class DfltEdgeRenderer implements IRenderer<Edge2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		public DfltEdgeRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void render(Edge2D edge) {
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, edge.getColor(), edge.getColor());
		}

	}

	private static class DfltChainRenderer implements IRenderer<Chain2D> {

		@Override
		public void render(Chain2D item) {
			for (Edge2D edge : item.getEdges()) {
				edge.render();
			}
		}

	}

	private static class DfltCuboidRenderer implements IRenderer<Cuboid2D> {

		@Override
		public void render(Cuboid2D item) {
			for (Edge2D edge : item.getEdges()) {
				edge.render();
			}
		}

	}

	private static class DfltChainNetRenderer implements IRenderer<ChainNet2D> {

		@Override
		public void render(ChainNet2D item) {
			for (Edge2D edge : item.getEdges()) {
				edge.render();
			}
		}

	}

	private static class FilledChainNetRenderer implements IRenderer<ChainNet2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		public FilledChainNetRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void render(ChainNet2D item) {

			final List<List<List<Edge2D>>> edgesH = item.getEdgesH();
			final List<List<List<Edge2D>>> edgesV = item.getEdgesV();

			for (int v = edgesH.size() - 2; v >= 0; v--) {
				// rendering from bottom to top to overcome OpenGL convex-only
				// polygon-rendering

				final List<List<Edge2D>> currentEdgesHTop = edgesH.get(v);
				final List<List<Edge2D>> currentEdgesHBottom = edgesH.get(v + 1);
				for (int h = 0; h < edgesV.size() - 1; h++) {

					final List<Edge2D> hTop = currentEdgesHTop.get(h);
					final List<Edge2D> vRight = edgesV.get(h + 1).get(v);
					final List<Edge2D> hBottom = currentEdgesHBottom.get(h);
					final List<Edge2D> vLeft = edgesV.get(h).get(v);

					final Collection<Vector2D> points = new ArrayList<>(hTop.size() + vRight.size() + hBottom.size() + vLeft.size());
					for (int i = 0; i < hTop.size(); i++) {
						points.add(hTop.get(i).getFirst().getCurrent());
					}
					for (int i = 0; i < vRight.size(); i++) {
						points.add(vRight.get(i).getFirst().getCurrent());
					}
					for (int i = hBottom.size() - 1; i >= 0; i--) {
						points.add(hBottom.get(i).getSecond().getCurrent());
					}
					for (int i = vLeft.size() - 1; i >= 0; i--) {
						points.add(vLeft.get(i).getSecond().getCurrent());
					}
					final Color hTopColor = hTop.iterator().next().getColor();
					final Color vRightColor = vRight.iterator().next().getColor();
					final Color hBottomColor = hBottom.iterator().next().getColor();
					final Color vLeftColor = vLeft.iterator().next().getColor();
					graphicsSubsystem.drawFilledPolygon(points,
							new Color((hTopColor.getRed() + vRightColor.getRed() + hBottomColor.getRed() + vLeftColor.getRed()) / 4,
									(hTopColor.getGreen() + vRightColor.getGreen() + hBottomColor.getGreen() + vLeftColor.getGreen()) / 4,
									(hTopColor.getBlue() + vRightColor.getBlue() + hBottomColor.getBlue() + vLeftColor.getBlue()) / 4,
									(hTopColor.getAlpha() + vRightColor.getAlpha() + hBottomColor.getAlpha() + vLeftColor.getAlpha()) / 4));

				}
			}
		}
	}

	public static void main(String args[]) throws Exception {
		new RagdollPhysics(GFX_SSYSTEM, COORDINATION, doRecord, recordFilename, recordingRate).start();
	}

	private final Collection<IEdgeContainer2D> edgeContainers = new LinkedList<>();

	private Collection<Edge2D> edges = new ArrayList<>();

	private Collection<Vertex2D> vertices = new LinkedHashSet<>();

	private final Collection<Vertex2D> windParticles = new ConcurrentLinkedQueue<>();

	public RagdollPhysics(EGraphicsSubsystem gfxSsystem, Coordination coordination, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("Ragdoll physics", gfxSsystem, coordination, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		initScene(graphicsSubsystem);
		windSimulator = new WindSimulator(initAirstreamField(), getCoordination()).setRateOfAirstreamChange(rateOfAirstreamChange);
		initKeyActions();

	}

	@Override
	protected void calculate(Dimension dimension) throws Exception {

		calculatePhysics(dimension);

		for (Vertex2D vertex : vertices) {
			if (Double.isNaN(vertex.getCurrent().x) || Double.isNaN(vertex.getCurrent().y) || Double.isNaN(vertex.getPrevious().x)
					|| Double.isNaN(vertex.getPrevious().y) || Double.isInfinite(vertex.getCurrent().x)
					|| Double.isInfinite(vertex.getCurrent().y) || Double.isInfinite(vertex.getPrevious().x)
					|| Double.isInfinite(vertex.getPrevious().y)) {
				System.out.println(vertex);
			}
		}

	}

	@Override
	protected void shutdown() throws Exception {

	}

	private VectorField2D initAirstreamField() {

		final int fieldWidth = getCoordination().WIDTH / 80;
		final int fieldHeight = getCoordination().HEIGHT / 80;
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

		final IRenderer<Edge2D> edgeRenderer = new DfltEdgeRenderer(graphicsSubsystem);
		final IRenderer<Cuboid2D> cuboidRenderer = new DfltCuboidRenderer();
		final IRenderer<Chain2D> chainRenderer = new DfltChainRenderer();
		final IRenderer<ChainNet2D> chainNetRenderer = new DfltChainNetRenderer();

		edgeContainers.add(new Edge2D(new Vertex2D(new Vector2D(100, 100), new Vector2D(99, 100)), new Vertex2D(new Vector2D(230, 120)),
				edgeRenderer));
		edgeContainers.add(new Edge2D(new Vertex2D(new Vector2D(100, 100), new Vector2D(101, 100)), new Vertex2D(new Vector2D(230, 120)),
				edgeRenderer));
		edgeContainers.add(new Edge2D(new Vertex2D(new Vector2D(100, 100), new Vector2D(100, 101)), new Vertex2D(new Vector2D(230, 120)),
				edgeRenderer));

		final Vertex2D cuboidHook = new Vertex2D(new Vector2D(400, 100), new Vector2D(380, 95));
		edgeContainers.add(new Cuboid2D(cuboidHook, new Vertex2D(new Vector2D(430, 100)), new Vertex2D(new Vector2D(430, 130)),
				new Vertex2D(new Vector2D(400, 130)), cuboidRenderer, edgeRenderer));
		edgeContainers.add(new Cuboid2D(new Vertex2D(new Vector2D(450, 100), new Vector2D(410, 105)), new Vertex2D(new Vector2D(490, 100)),
				new Vertex2D(new Vector2D(490, 140)), new Vertex2D(new Vector2D(450, 140)), cuboidRenderer, edgeRenderer));
		edgeContainers.add(new Cuboid2D(new Vertex2D(new Vector2D(600, 10), new Vector2D(605, 105)), new Vertex2D(new Vector2D(700, 10)),
				new Vertex2D(new Vector2D(700, 140)), new Vertex2D(new Vector2D(600, 140)), cuboidRenderer, edgeRenderer));

		edgeContainers.add(new Chain2D(new Vertex2D(new Vector2D(500, 15)).setPinned(true), new Vertex2D(new Vector2D(800, 100)), 40,
				chainRenderer, edgeRenderer));
		edgeContainers.add(new Chain2D(new Vertex2D(new Vector2D(850, 15)).setPinned(true), cuboidHook, 60, chainRenderer, edgeRenderer));

		chainNet = new ChainNet2D(new Vertex2D(new Vector2D(900, 15)).setPinned(true), new Vertex2D(new Vector2D(1400, 15)).setPinned(true),
				30, 10, 15, 16, chainNetRenderer, edgeRenderer).setColor(colors[0]);
		edgeContainers.add(chainNet);

		for (IEdgeContainer2D edgeContainer : edgeContainers) {
			edges.addAll(edgeContainer.getEdges());
		}

		for (final Edge2D edge : edges) {
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

				for (int x = 0; x < windWidth; x++) {
					for (int y = 0; y < windHeight; y++) {
						windParticles.add(new Vertex2D(new Vector2D(x * width / windWidth, y * height / windHeight)));
					}
				}
			} else {

				final Iterator<Vertex2D> iterator = windParticles.iterator();
				while (iterator.hasNext()) {

					final Vertex2D windParticle = iterator.next();
					final Vector2D newPosition = calculateNewPosition(windParticle, 1);
					newPosition.add(windSimulator.calculateWind(windParticle.getCurrent()));

					if (newPosition.x >= width || newPosition.x < 0 || newPosition.y >= height || newPosition.y < 0) {
						iterator.remove();
					} else {
						repositionVertex(windParticle, newPosition);
					}
				}
			}

			windParticleCnt++;
		}

		vertices.parallelStream().forEach(new Consumer<Vertex2D>() {

			@Override
			public void accept(Vertex2D vertex) {

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

	/**
	 * calculates the new position. Uses verlet integration.
	 * 
	 * @param vertex
	 * @param friction
	 * @return
	 */
	public Vector2D calculateNewPosition(Vertex2D vertex, double friction) {
		return Vector2D.add(vertex.getCurrent(), Vector2D.mult(friction, Vector2D.substract(vertex.getCurrent(), vertex.getPrevious())));
	}

	/**
	 * sets the vertex to the new position
	 * 
	 * @param vertex
	 * @param newCurrent
	 */
	public void repositionVertex(Vertex2D vertex, final Vector2D newCurrent) {
		vertex.setPrevious(vertex.getCurrent());
		vertex.setCurrent(newCurrent);
	}

	private void handleConstraints(Dimension dimension) {

		vertices.parallelStream().forEach(vertex -> handleBorderConstraints(vertex, dimension));

		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final Edge2D edge : edges) {
			handleStickConstraints(edge);
		}

	}

	private void handleStickConstraints(final Edge2D edge) {

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

		if (dV.length() != edge.getLength()) {

			double diff = dV.length() - edge.getLength();
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

	private void handleBorderConstraints(final Vertex2D vertex, Dimension dimension) {

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

					final VectorND windvector = windSimulator.getAirstreamField()
							.getValue(new VectorND(Arrays.asList((double) x, (double) y)));

					int xpos = (int) (((double) x) * getCoordination().WIDTH / windWidth);
					int ypos = (int) (((double) y) * getCoordination().HEIGHT / windHeight);
					final int xend = xpos + (int) (scale * windvector.get(0));
					final int yend = ypos + (int) (scale * windvector.get(1));
					final Color lineColor = transparent(Color.RED, alpha);
					graphicsSubsystem.drawLine(xpos, ypos, xend, yend, lineColor, Color.GREEN);
					graphicsSubsystem.drawFilledCircle(xend, yend, 2, () -> transparent(Color.ORANGE, alpha));
				}
			}
		}

		if (showAirstream || showWind) {

			final int windWidth = getCoordination().WIDTH;
			final int windHeight = getCoordination().HEIGHT;
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
							graphicsSubsystem.drawLine(x, y, (int) (x + scaleWind * windvector.x), (int) (y + scaleWind * windvector.y),
									colorStart, colorEnd);
						}
					}
				}
			}
		}

		if (useWind && useWindparticles) {
			windParticles.stream().forEach(particle -> {
				graphicsSubsystem.drawFilledCircle((int) particle.getCurrent().x, (int) particle.getCurrent().y, 2 * bobbleSize,
						() -> new Color(Color.RED.getRed(), 0, Color.BLUE.getBlue(), 100).brighter());
				graphicsSubsystem.drawFilledCircle((int) particle.getCurrent().x, (int) particle.getCurrent().y, bobbleSize,
						() -> new Color(Color.RED.getRed(), 0, Color.BLUE.getBlue(), 100));
			});
		}

		edgeContainers.stream().forEach(IEdgeContainer2D::render);
		vertices.stream().filter(vertex -> isHit(mousePoint, vertex) && !isGrabbed(vertex)).forEach(vertex -> graphicsSubsystem
				.drawFilledCircle((int) vertex.getCurrent().x, (int) vertex.getCurrent().y, bobbleSize, () -> Color.RED));

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

	private boolean isHit(final Vector2D point, final Vertex2D vertex) {
		return Vector2D.distance(point, vertex.getCurrent()) <= bobbleSize;
	}

	@Override
	public void mousePressed(MouseEvent meEvent) {

		setMousePoint(meEvent);

		for (Vertex2D vertex : vertices) {
			if (isHit(mousePoint, vertex)) {
				grabbedVertices.add(vertex);
			}
		}

		final Vector2D mPoint = new Vector2D(mousePoint);
		for (Vertex2D vertex : grabbedVertices) {

			vertex.setPinned(true);
			vertex.setCurrent(mPoint);
			vertex.setPrevious(mPoint);

		}

		mousePressed = true;

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		mousePressed = false;

		setMousePoint(e);
		final Vector2D mPoint = new Vector2D(mousePoint);
		if (e.getButton() == MouseEvent.BUTTON1) {
			// unpin
			for (Vertex2D vertex : grabbedVertices) {
				vertex.setPinned(false);
				vertex.setCurrent(mPoint);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// glue together
			for (Vertex2D vertex : grabbedVertices) {
				for (Edge2D edge : edges) {
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

	@Override
	public void mouseDragged(MouseEvent e) {

		setMousePoint(e);

		if (mousePressed) {

			final Vector2D mPoint = new Vector2D(mousePoint);
			for (Vertex2D vertex : grabbedVertices) {
				vertex.setCurrent(mPoint);
			}
		}
	}

	private boolean isGrabbed(Vertex2D vertex) {
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
				chainNet.setRenderer(new FilledChainNetRenderer(getGraphicsSubsystem()));
				filled = true;
			}

			@Override
			public void minus() {
				chainNet.setRenderer(new DfltChainNetRenderer());
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
	}
}
