/**
 * 
 */
package de.zintel.sim.nbodies;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.Coordination;
import de.zintel.gfx.g2d.Field;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.physics.Body;
import de.zintel.physics.gravitation.IBodyProducer;
import de.zintel.sim.SimulationScreen;
import de.zintel.sim.nbodies.sceneries.BlackholeOnlySceneryConfig;
import de.zintel.sim.nbodies.sceneries.BlackholeSceneryConfig;
import de.zintel.sim.nbodies.sceneries.BodyDeserializerSceneryConfig;
import de.zintel.sim.nbodies.sceneries.BodyParameterDeserializerSceneryConfig;
import de.zintel.sim.nbodies.sceneries.ExplosionSceneryConfig;
import de.zintel.sim.nbodies.sceneries.Scenery;
import de.zintel.sim.nbodies.sceneries.SceneryConfig;
import de.zintel.sim.nbodies.sceneries.StarfieldSceneryConfig;

/**
 * @author Friedemann
 *
 */
public class NBodies extends SimulationScreen {

	private static final boolean doRecord = false;

	private static final long maxFrames = 15 * 60 * 60;

	private static final int recordingRate = 1;

	private static final String recordFilename = "D:/video/gravitation.mp4";

	private static final GfxUtils.EGraphicsSubsystem eGrapicsSubsystem = GfxUtils.EGraphicsSubsystem.GL;

	private static final Coordination coordination = new Coordination();

	private int width = coordination.WIDTH;

	private int height = coordination.HEIGHT;

	private IRenderer renderer;

	private IBodyProducer bodyProducer;

	private final Collection<BodyConsumer> bodyConsumers = new ArrayList<>();

	private static final int IDX_SCENERY = 2;

	@SuppressWarnings("serial")
	private List<Scenery> sceneries = new ArrayList<Scenery>() {
		{
			add(new Scenery(new StarfieldSceneryConfig(width, height)));
			add(new Scenery(new ExplosionSceneryConfig(width, height)));
			add(new Scenery(new BlackholeSceneryConfig(width, height)));
			add(new Scenery(new BlackholeOnlySceneryConfig(width, height)));
			add(new Scenery(new BodyDeserializerSceneryConfig(width, height, "c:/tmp/grav1.dat")));
			add(new Scenery(new BodyParameterDeserializerSceneryConfig(width, height, "c:/tmp/grav.par")));
		}
	};

	private Scenery scenery = sceneries.get(IDX_SCENERY);

	private volatile boolean stopped = false;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		NBodies application = new NBodies(eGrapicsSubsystem, coordination, doRecord, recordFilename, recordingRate);
		application.start();

	}

	public NBodies(EGraphicsSubsystem gfxSsystem, de.zintel.gfx.Coordination coordination, boolean doRecord, String recordFilename,
			int recordingRate) {
		super("NBodies", gfxSsystem, coordination, doRecord, recordFilename, recordingRate);
	}

	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		bodyProducer = scenery.createGravitationSystem();

		renderer = new DefaultRenderer(graphicsSubsystem, scenery);
		renderer.initGraphics();

		Collection<Body> bodies = bodyProducer.getBodies();
		renderer.initBodyProperties(bodies);

		setMaxFrames(maxFrames);

	}

	@Override
	protected void calculate(Dimension dimension) throws Exception {

		bodyProducer.calculate();

		Collection<Body> bodies = bodyProducer.getBodies();
		for (final Consumer<Collection<Body>> bodyConsumer : bodyConsumers) {
			bodyConsumer.accept(bodies);
		}

	}

	@Override
	protected void renderSim(IGraphicsSubsystem graphicsSubsystem) {

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

	}

	@Override
	protected void shutdown() throws Exception {

		bodyProducer.shutdown();

		for (BodyConsumer bodyConsumer : bodyConsumers) {
			bodyConsumer.close();
		}

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
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
