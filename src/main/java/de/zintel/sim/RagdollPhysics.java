/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.Koordination;
import de.zintel.gfx.g2d.Chain2D;
import de.zintel.gfx.g2d.Cuboid2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.EdgeContainer2D;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.gfx.g2d.Vertex2D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.IRendererListener;

/**
 * @author friedemann.zintel
 *
 */
public class RagdollPhysics implements MouseListener, MouseMotionListener, ActionListener, KeyListener, IRendererListener {

	private static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final int iterations = 20;

	private final double calmnessThreshold = 2;

	private static final int vertexSize = 3;

	private final Vector2D gravity = new Vector2D(0, 0.8);

	private final double decay = 0.99;

	private final double friction = 0.999;

	private IGraphicsSubsystem graphicsSubsystem;

	private static Koordination koordination = new Koordination();

	private long calculations = 0;

	private long rStartTs = 0;

	private long renderings = 0;

	private volatile boolean mousePressed = false;

	private Collection<Vertex2D> grabbedVertices = null;

	public static void main(String args[]) throws InterruptedException {
		new RagdollPhysics().start();
	}

	private final Collection<EdgeContainer2D> edgeContainers = new LinkedList<EdgeContainer2D>() {
		{
			add(new EdgeContainer2D() {
				{
					addEdge(new Edge2D(new Vertex2D(new Vector2D(100, 100), new Vector2D(99, 100)), new Vertex2D(new Vector2D(230, 120))));
				}
			});

			final Vertex2D cuboidHook = new Vertex2D(new Vector2D(400, 100), new Vector2D(380, 95));
			add(new Cuboid2D(cuboidHook, new Vertex2D(new Vector2D(430, 100)), new Vertex2D(new Vector2D(430, 130)),
					new Vertex2D(new Vector2D(400, 130))));
			add(new Cuboid2D(new Vertex2D(new Vector2D(450, 100), new Vector2D(410, 105)), new Vertex2D(new Vector2D(490, 100)),
					new Vertex2D(new Vector2D(490, 140)), new Vertex2D(new Vector2D(450, 140))));
			add(new Cuboid2D(new Vertex2D(new Vector2D(600, 10), new Vector2D(605, 105)), new Vertex2D(new Vector2D(700, 10)),
					new Vertex2D(new Vector2D(700, 140)), new Vertex2D(new Vector2D(600, 140))));

			add(new Chain2D(new Vertex2D(new Vector2D(500, 15)).setPinned(true), new Vertex2D(new Vector2D(800, 100)), 40));
			add(new Chain2D(new Vertex2D(new Vector2D(850, 15)).setPinned(true), cuboidHook, 60));

			add(new ChainNet2D(new Vertex2D(new Vector2D(900, 15)).setPinned(true), new Vertex2D(new Vector2D(1400, 15)).setPinned(true),
					50, 10, 11, 11).setColor(Color.LIGHT_GRAY));
		}
	};

	private Collection<Edge2D> edges = new ArrayList<Edge2D>() {
		{
			for (EdgeContainer2D edgeContainer : edgeContainers) {
				addAll(edgeContainer.getEdges());
			}
		}
	};

	@SuppressWarnings("serial")
	private Collection<Vertex2D> vertices = new LinkedHashSet<Vertex2D>() {
		{
			for (final Edge2D edge : edges) {
				add(edge.getFirst());
				add(edge.getSecond());
			}
		}
	};

	private void start() throws InterruptedException {

		final IGraphicsSubsystemFactory graphicsSubsystemFactory = GfxUtils.graphicsSubsystemFactories.get(GFX_SSYSTEM);
		graphicsSubsystem = graphicsSubsystemFactory.newGraphicsSubsystem("Ragdoll physics", koordination.WIDTH, koordination.HEIGHT);
		graphicsSubsystem.init();
		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		graphicsSubsystem.setFullScreen();
		graphicsSubsystem.addMouseListener(this);
		graphicsSubsystem.addKeyListener(this);
		graphicsSubsystem.addRendererListener(this);
		graphicsSubsystem.addMouseMotionListener(this);

		graphicsSubsystem.synchronize(false);
		graphicsSubsystem.display();

		long crStartTs = 0;
		while (true) {

			long startTs = System.currentTimeMillis();

			calculations++;
			if (crStartTs == 0) {
				crStartTs = System.currentTimeMillis();
			}

			calculatePhysics();

			graphicsSubsystem.repaint();

			long crStopTs = System.currentTimeMillis();
			if (crStopTs - crStartTs >= 1000) {

				double calculationrate = calculations / ((crStopTs - crStartTs) / (double) 1000);
				System.out.println("calculationrate: " + calculationrate + " cps");

				crStartTs = System.currentTimeMillis();
				calculations = 0;

			}

			int delay = 1000 / 60;
			long diffTs = System.currentTimeMillis() - startTs;
			if (diffTs < delay) {
				Thread.sleep(delay - diffTs);
			}
		}

	}

	/**
	 * 
	 */
	public RagdollPhysics() {
	}

	private void calculatePhysics() {

		vertices.parallelStream().forEach(new Consumer<Vertex2D>() {

			@Override
			public void accept(Vertex2D vertex) {

				if (vertex.isPinned()) {
					return;
				}

				double frictionFac = 1;
				// friction
				if (vertex.getCurrent().y == graphicsSubsystem.getHeight() - 1) {
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
			handleConstraints();
		}
	}

	private void handleConstraints() {

		vertices.parallelStream().forEach(vertex -> handleBorderConstraints(vertex));

		// here parallelisation should not be done because some edges access the
		// same vertex
		for (final Edge2D edge : edges) {
			handleStickConstraints(edge);
		}

	}

	private void handleStickConstraints(final Edge2D edge) {

		final Vector2D cFirst = edge.getFirst().getCurrent();
		final Vector2D cSecond = edge.getSecond().getCurrent();
		final Vector2D dV = Vector2D.substract(cFirst, cSecond);

		if (dV.length() > edge.getLength() || dV.length() < edge.getLength()) {

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

	private void handleBorderConstraints(final Vertex2D vertex) {

		final Vector2D current = vertex.getCurrent();
		final Vector2D previous = vertex.getPrevious();
		final double velocity = Vector2D.distance(current, previous);

		// bounce
		if (current.x > graphicsSubsystem.getWidth() - 1) {

			if (velocity < calmnessThreshold) {
				current.x = graphicsSubsystem.getWidth() - 1;
				previous.x = current.x;
			} else {
				current.x = graphicsSubsystem.getWidth() - 1 - ((current.x - (graphicsSubsystem.getWidth() - 1)) * decay);
				previous.x = graphicsSubsystem.getWidth() - 1 + (current.x - previous.x) * decay;
			}
		} else if (current.x < 0) {
			current.x *= -decay;
			previous.x = -previous.x * decay;
		}

		if (current.y > graphicsSubsystem.getHeight() - 1) {
			if (velocity < calmnessThreshold) {
				current.y = graphicsSubsystem.getHeight() - 1;
				previous.y = current.y;
			} else {
				current.y = graphicsSubsystem.getHeight() - 1 - ((current.y - (graphicsSubsystem.getHeight() - 1)) * decay);
				previous.y = graphicsSubsystem.getHeight() - 1 + (current.y - previous.y) * decay;
			}
		} else if (current.y < 0) {
			current.y *= -decay;
			previous.y = -previous.y * decay;
		}

	}

	@Override
	public void render() {

		renderings++;

		if (rStartTs == 0) {
			rStartTs = System.currentTimeMillis();
		}

		for (final Edge2D edge : edges) {
			graphicsSubsystem.drawFilledCircle((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y, vertexSize,
					() -> edge.getColor());
			graphicsSubsystem.drawFilledCircle((int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, vertexSize,
					() -> edge.getColor());
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, edge.getColor());
		}

		long rStopTs = System.currentTimeMillis();
		if (rStopTs - rStartTs >= 1000) {

			double framerate = renderings / ((rStopTs - rStartTs) / (double) 1000);
			System.out.println("framerate: " + framerate + " fps");

			rStartTs = System.currentTimeMillis();
			renderings = 0;

		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			graphicsSubsystem.shutdown();
			System.exit(0);
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
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

		grabbedVertices = new ArrayList<>();
		Vector2D mPoint = new Vector2D(meEvent.getPoint());

		for (Vertex2D vertex : vertices) {
			if (isHit(mPoint, vertex)) {
				grabbedVertices.add(vertex);
			}
		}

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
		if (e.getButton() == MouseEvent.BUTTON1) {
			// unpin
			for (Vertex2D vertex : grabbedVertices) {
				vertex.setPinned(false);
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

		grabbedVertices = null;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (mousePressed) {

			final Vector2D mPoint = new Vector2D(e.getPoint());
			for (Vertex2D vertex : grabbedVertices) {
				vertex.setCurrent(mPoint);
				vertex.setPrevious(mPoint);

			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

}
