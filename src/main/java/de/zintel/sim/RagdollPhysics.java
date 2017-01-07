/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.Koordination;
import de.zintel.gfx.g2d.Chain2D;
import de.zintel.gfx.g2d.ChainNet2D;
import de.zintel.gfx.g2d.Cuboid2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.IEdgeContainer2D;
import de.zintel.gfx.g2d.IRenderer;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.gfx.g2d.Vertex2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;

/**
 * @author friedemann.zintel
 *
 */
public class RagdollPhysics extends SimulationScreen {

	private static final boolean doRecord = false;

	private static final String recordFilename = "D:/cloth-sim.mpg";

	private static final int recordingRate = 2;

	private static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final int iterations = 40;

	private static final int vertexSize = 3;

	private static final Vector2D GRAV_DOWN = new Vector2D(0, 0.8);

	private static final Vector2D GRAV_UP = new Vector2D(0, -0.8);

	private static final Vector2D GRAV_RIGHT = new Vector2D(0.8, 0);

	private static final Vector2D GRAV_LEFT = new Vector2D(-0.8, 0);

	private Vector2D gravity = GRAV_DOWN;

	private final double decay = 0.2;

	private final double friction = 0.999;

	private static Koordination koordination = new Koordination();

	private volatile boolean mousePressed = false;

	private final Collection<Vertex2D> grabbedVertices = Collections.synchronizedCollection(new ArrayList<>());

	private Vector2D mousePoint = new Vector2D();

	private ChainNet2D chainNet;

	private final Color[] colors = { Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.GRAY, Color.CYAN, Color.MAGENTA,
			Color.PINK };

	private int colorCycleCnt = 0;

	private static class DfltEdgeRenderer implements IRenderer<Edge2D> {

		private final IGraphicsSubsystem graphicsSubsystem;

		public DfltEdgeRenderer(IGraphicsSubsystem graphicsSubsystem) {
			this.graphicsSubsystem = graphicsSubsystem;
		}

		@Override
		public void render(Edge2D edge) {
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, edge.getColor());
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
		new RagdollPhysics(GFX_SSYSTEM, koordination, doRecord, recordFilename, recordingRate).start();
	}

	private final Collection<IEdgeContainer2D> edgeContainers = new LinkedList<>();

	private Collection<Edge2D> edges = new ArrayList<>();

	private Collection<Vertex2D> vertices = new LinkedHashSet<>();

	public RagdollPhysics(EGraphicsSubsystem gfxSsystem, Koordination coordination, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("Ragdoll physics", gfxSsystem, coordination, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		initScene(graphicsSubsystem);

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

		vertices.parallelStream().forEach(new Consumer<Vertex2D>() {

			@Override
			public void accept(Vertex2D vertex) {

				if (vertex.isPinned()) {
					vertex.setPrevious(vertex.getCurrent());
					return;
				}

				double frictionFac = 1;
				// friction
				if (vertex.getCurrent().y == dimension.getHeight() - 1) {
					frictionFac = friction;
				}

				final Vector2D newCurrent = Vector2D.add(vertex.getCurrent(),
						Vector2D.mult(frictionFac, Vector2D.substract(vertex.getCurrent(), vertex.getPrevious())));
				newCurrent.add(gravity);
				vertex.setPrevious(vertex.getCurrent());
				vertex.setCurrent(newCurrent);

			}
		});

		for (int i = 0; i < iterations; i++) {
			handleConstraints(dimension);
		}
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

		for (IEdgeContainer2D edgeContainer : edgeContainers) {
			edgeContainer.render();
		}

		for (Vertex2D vertex : vertices) {
			if (isHit(mousePoint, vertex) && !isGrabbed(vertex)) {
				// grabbing
				graphicsSubsystem.drawFilledCircle((int) vertex.getCurrent().x, (int) vertex.getCurrent().y, vertexSize, () -> Color.RED);
			}
		}

	}

	@Override
	public void keyPressed(KeyEvent ke) {

		if (ke.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
			gravity = GRAV_DOWN;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_UP) {
			gravity = GRAV_UP;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_RIGHT) {
			gravity = GRAV_RIGHT;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_LEFT) {
			gravity = GRAV_LEFT;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_PLUS) {
			chainNet.setRenderer(new FilledChainNetRenderer(getGraphicsSubsystem()));
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_MINUS) {
			chainNet.setRenderer(new DfltChainNetRenderer());
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
		return Vector2D.distance(point, vertex.getCurrent()) <= vertexSize;
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
}
