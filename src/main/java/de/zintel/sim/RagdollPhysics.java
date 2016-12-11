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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.Koordination;
import de.zintel.gfx.g2d.Chain2D;
import de.zintel.gfx.g2d.Cuboid2D;
import de.zintel.gfx.g2d.Edge2D;
import de.zintel.gfx.g2d.EdgeContainer2D;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.gfx.g2d.Vertex2D;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.IRendererListener;

/**
 * @author friedemann.zintel
 *
 */
public class RagdollPhysics implements MouseListener, ActionListener, KeyListener, IRendererListener {

	private static final EGraphicsSubsystem GFX_SSYSTEM = GfxUtils.EGraphicsSubsystem.GL;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 40);

	private static final int iterations = 20;

	private final double gravity = 0.8;

	private final double decay = 0.99;

	private final double friction = 0.999;

	private IGraphicsSubsystem graphicsSubsystem;

	private static Koordination koordination = new Koordination();

	private long calculations = 0;

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

			add(new Chain2D(Arrays.asList(new Vertex2D(new Vector2D(20, 50)).setPinned(true), new Vertex2D(new Vector2D(30, 40)),
					new Vertex2D(new Vector2D(40, 60), new Vector2D(40, 50)), new Vertex2D(new Vector2D(50, 10)),
					new Vertex2D(new Vector2D(60, 9)), new Vertex2D(new Vector2D(70, 12)), new Vertex2D(new Vector2D(80, 8)),
					new Vertex2D(new Vector2D(85, 5)))));
			add(new Chain2D(new Vertex2D(new Vector2D(500, 15)).setPinned(true), new Vertex2D(new Vector2D(800, 100)), 40));
			add(new Chain2D(new Vertex2D(new Vector2D(850, 15)).setPinned(true), cuboidHook, 60));

			add(new ChainNet2D(new Vertex2D(new Vector2D(900, 15)).setPinned(true), new Vertex2D(new Vector2D(1400, 15)).setPinned(true),
					70, 20, 5, 5));
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

		for (final Vertex2D vertex : vertices) {

			if (vertex.isPinned()) {
				continue;
			}

			double frictionFac = 1;
			// friction
			if (vertex.getCurrent().y == graphicsSubsystem.getHeight() - 1) {
				frictionFac = friction;
			}

			final Vector2D newCurrent = Vector2D.add(vertex.getCurrent(),
					Vector2D.mult(frictionFac, Vector2D.substract(vertex.getCurrent(), vertex.getPrevious())));
			newCurrent.y += gravity;
			vertex.setPrevious(vertex.getCurrent());
			vertex.setCurrent(newCurrent);

		}

		for (int i = 0; i < iterations; i++) {
			handleConstraints();
		}
	}

	private void handleConstraints() {

		for (final Vertex2D vertex : vertices) {
			handleBorderConstraints(vertex);
		}

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

		// bounce
		if (current.x > graphicsSubsystem.getWidth() - 1) {
			current.x = graphicsSubsystem.getWidth() - 1 - ((current.x - (graphicsSubsystem.getWidth() - 1)) * decay);
			previous.x = graphicsSubsystem.getWidth() - 1 + (current.x - previous.x) * decay;
		} else if (current.x < 0) {
			current.x *= -decay;
			previous.x = -previous.x * decay;
		}

		if (current.y > graphicsSubsystem.getHeight() - 1) {
			current.y = graphicsSubsystem.getHeight() - 1 - ((current.y - (graphicsSubsystem.getHeight() - 1)) * decay);
			previous.y = graphicsSubsystem.getHeight() - 1 + (current.y - previous.y) * decay;
		} else if (current.y < 0) {
			current.y *= -decay;
			previous.y = -previous.y * decay;
		}

	}

	@Override
	public void render() {

		for (final Edge2D edge : edges) {
			graphicsSubsystem.drawFilledCircle((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y, 3,
					() -> Color.WHITE);
			graphicsSubsystem.drawFilledCircle((int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, 3,
					() -> Color.WHITE);
			graphicsSubsystem.drawLine((int) edge.getFirst().getCurrent().x, (int) edge.getFirst().getCurrent().y,
					(int) edge.getSecond().getCurrent().x, (int) edge.getSecond().getCurrent().y, Color.WHITE);
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

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
