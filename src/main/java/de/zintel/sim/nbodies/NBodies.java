/**
 * 
 */
package de.zintel.sim.nbodies;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.Koordination;
import de.zintel.gfx.g2d.Field;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.IRendererListener;
import de.zintel.physics.Body;
import de.zintel.physics.gravitation.IBodyProducer;
import de.zintel.sim.nbodies.sceneries.BlackholeOnlySceneryConfig;
import de.zintel.sim.nbodies.sceneries.BlackholeSceneryConfig;
import de.zintel.sim.nbodies.sceneries.DataSceneryConfig;
import de.zintel.sim.nbodies.sceneries.ExplosionSceneryConfig;
import de.zintel.sim.nbodies.sceneries.Scenery;
import de.zintel.sim.nbodies.sceneries.SceneryConfig;
import de.zintel.sim.nbodies.sceneries.StarfieldSceneryConfig;

/**
 * @author Friedemann
 *
 */
public class NBodies implements MouseListener, ActionListener, MouseWheelListener, KeyListener, IRendererListener {

	private final boolean doRecord = false;

	private final long maxFrames = 15 * 60 * 60;

	private final int recordingRate = 1;

	private final String recordFilename = "D:/video/gravitation.mp4";

	private final GfxUtils.EGraphicsSubsystem eGrapicsSubsystem = GfxUtils.EGraphicsSubsystem.GL;

	private Koordination Koordination = new Koordination();

	private int width = Koordination.WIDTH;

	private int height = Koordination.HEIGHT;

	private IGraphicsSubsystem graphicsSubsystem;

	private IRenderer renderer;

	private IBodyProducer bodyProducer;

	private final BodySerializer bodySerializer;

	private final Collection<Consumer<Collection<Body>>> bodyConsumers = new ArrayList<>();

	private static final int IDX_SCENERY = 2;

	@SuppressWarnings("serial")
	private List<Scenery> sceneries = new ArrayList<Scenery>() {
		{
			add(new Scenery(new StarfieldSceneryConfig(width, height)));
			add(new Scenery(new ExplosionSceneryConfig(width, height)));
			add(new Scenery(new BlackholeSceneryConfig(width, height)));
			add(new Scenery(new BlackholeOnlySceneryConfig(width, height)));
			add(new Scenery(new DataSceneryConfig(width, height, "c:/tmp/grav.dat")));
		}
	};

	private Scenery scenery = sceneries.get(IDX_SCENERY);

	private volatile boolean paused = false;

	private volatile boolean stopped = false;

	private long rStartTs = 0;

	private long crStartTs = 0;

	private long renderings = 0;

	private long calculations = 0;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		NBodies application = new NBodies();
		application.start();

	}

	public NBodies() throws IOException {
		this.bodySerializer = null;//new BodySerializer("c:/tmp/grav.dat");
	}

	public void start() throws Exception {

		if (bodySerializer != null) {
			bodyConsumers.add(bodySerializer);
		}

		bodyProducer = scenery.createGravitationSystem();

		final IGraphicsSubsystemFactory graphicsSubsystemFactory = GfxUtils.graphicsSubsystemFactories.get(eGrapicsSubsystem);
		if (graphicsSubsystemFactory == null) {
			System.out.println("unknown graphics subsystem!");
			System.exit(1);
		}
		graphicsSubsystem = graphicsSubsystemFactory.newGraphicsSubsystem("NBodies", width, height);
		graphicsSubsystem.recordSession(doRecord, recordFilename);
		renderer = new DefaultRenderer(graphicsSubsystem, scenery);

		renderer.initGraphics();

		graphicsSubsystem.addMouseWheelListener(this);
		graphicsSubsystem.addKeyListener(this);
		graphicsSubsystem.addRendererListener(this);
		graphicsSubsystem.addMouseListener(this);

		renderer.display();

		Collection<Body> bodies = bodyProducer.getBodies();
		renderer.initBodyProperties(bodies);

		long rIter = 0;
		while (!stopped && (!doRecord || rIter < maxFrames)) {

			calculations++;

			long startTs = System.currentTimeMillis();

			if (!paused) {

				if (crStartTs == 0) {
					crStartTs = System.currentTimeMillis();
				}
				bodyProducer.calculate();

				bodies = bodyProducer.getBodies();
				for (final Consumer<Collection<Body>> bodyConsumer : bodyConsumers) {
					bodyConsumer.accept(bodies);
				}

				long crStopTs = System.currentTimeMillis();
				if (crStopTs - crStartTs >= 1000) {

					double calculationrate = calculations / ((crStopTs - crStartTs) / (double) 1000);
					System.out.println("calculationrate: " + calculationrate + " cps" + " objects: " + bodies.size());

					crStartTs = System.currentTimeMillis();
					calculations = 0;

				}

				if (!doRecord || calculations % recordingRate == 0) {
					graphicsSubsystem.repaint();
					rIter++;
					if (rIter % 100 == 0) {
						System.out.println("frames: " + rIter);
					}
				}
			}

			long diffTs = System.currentTimeMillis() - startTs;

			if (!doRecord) {
				if (diffTs < scenery.getSceneryConfig().getDelay()) {
					Thread.sleep(scenery.getSceneryConfig().getDelay() - diffTs);
				}
			}

		}

		graphicsSubsystem.shutdown();
		bodyProducer.shutdown();
		if (bodySerializer != null) {
			bodySerializer.close();
		}
		System.exit(0);

	}

	private void initBodies() {
		// gravitationSystem.addBody(new Body("1", 20, 2000, new
		// Vector2D(500,
		// 100), new Vector2D(14, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("2", 50, 200000, new
		// Vector2D(500,
		// 300), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("3", 20, 2000, new
		// Vector2D(500,
		// 500), new Vector2D(-14, 0), CUtils.makeRandomColor()));
		//
		// gravitationSystem.addBody(new Body("1", 50, 2000, new
		// Vector2D(500,
		// 320), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("2", 50, 2, new
		// Vector2D(300,
		// 400), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("3", 50, 2000, new
		// Vector2D(500,
		// 480), new Vector2D(0, 0), CUtils.makeRandomColor()));

		// gravitationSystem.addBody(new Body("1", 50, 2000, new Vector2D(500,
		// 400), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("2", 50, 2000, new Vector2D(100,
		// 400), new Vector2D(10, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("3", 50, 2000, new Vector2D(600,
		// 400), new Vector2D(0, 0), CUtils.makeRandomColor()));
		//
		// gravitationSystem.addBody(new Body("1", 50, 2000, new Vector2D(50,
		// 350), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("2", 50, 2000, new Vector2D(650,
		// 350), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("3", 50, 2000, new Vector2D(350,
		// 50), new Vector2D(0, 0), CUtils.makeRandomColor()));
		// gravitationSystem.addBody(new Body("4", 50, 2000, new Vector2D(350,
		// 650), new Vector2D(0, 0), CUtils.makeRandomColor()));
		//
		//
		// gravitationSystem.addBody(new Body("1", 50, 1, new Vector2D(50, 50),
		// new Vector2D(0, 2)));
		// gravitationSystem.addBody(new Body("2", 50, 1, new Vector2D(50, 550),
		// new Vector2D(0, 0)));
		// gravitationSystem.addBody(new Body("3", 50, 1, new Vector2D(50, 651),
		// new Vector2D(0, 0)));

	}

	@Override
	public synchronized void render() {

		renderings++;

		if (rStartTs == 0) {
			rStartTs = System.currentTimeMillis();
		}

		Collection<Body> bodies = bodyProducer.getBodies();
		final SceneryConfig sceneryConfig = scenery.getSceneryConfig();

		for (final Body body : bodies) {

			if (stopped) {
				return;
			}

			if (sceneryConfig.isStarfield()) {

				if (body.isParticle()) {

					// Feuerschweif
					renderer.renderFiretail(body);

				} else {

					// Corona
					renderer.renderCorona(body);
				}
			}

			if (!sceneryConfig.isStarfield() || !body.isParticle()) {
				renderer.renderBody(body);
			}

			if (sceneryConfig.isDrawVectors()) {
				renderer.renderVelocity(body);
			}
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
	public void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON3) {
			paused ^= true;
		}
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

	@Override
	public void actionPerformed(ActionEvent event) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {

		int notches = event.getWheelRotation();
		scenery.getSceneryConfig().setDistance(scenery.getSceneryConfig().getDistance() - notches * 0.1);
		bodyProducer.setField(new Field(scenery.getSceneryConfig().spaceMin(width), scenery.getSceneryConfig().spaceMin(height),
				scenery.getSceneryConfig().spaceMax(width), scenery.getSceneryConfig().spaceMax(height)));
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			stopped = true;
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
