/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.g3d.FourierPointGenerator;
import de.zintel.gfx.g3d.FourierPointGenerator.FourierCircle;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.physics.particles.Particle;

/**
 * @author friedo
 *
 */
public class FourierSim extends SimulationScreen {

	private final int SPEED = 200;

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private EColorMixture colorMixture = EColorMixture.ADDITIVE;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 50);

	private FourierPointGenerator interpolater;

	private Collection<Particle<Color>> particles = Collections.synchronizedCollection(new LinkedList<>());

	private volatile boolean init = true;

	private int iterations = 0;

	private int iteration = 0;

	/**
	 * @param title
	 * @param gfxSsystem
	 * @param screenParameters
	 * @param doRecord
	 * @param recordFilename
	 * @param recordingRate
	 */
	public FourierSim(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord, String recordFilename,
			int recordingRate) {
		super(title, gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
	}

	public static void main(String args[]) throws Exception {
		new FourierSim("Fourier", GFX_SSYSTEM, SCREENPARAMETERS, false, "", 0).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#init(de.zintel.gfx.graphicsubsystem.
	 * IGraphicsSubsystem)
	 */
	@Override
	protected void init(IGraphicsSubsystem graphicsSubsystem) {

		graphicsSubsystem.setBackground(COLOR_BACKGROUND);
		graphicsSubsystem.setColorMixture(colorMixture);
	}

	private void initFourier() {

		particles.clear();
		final int numCircles = MathUtils.RANDOM.nextInt(2) + 2;
		iterations = 17000 * numCircles;
		iteration = 0;
		interpolater = new FourierPointGenerator(new Vector3D(SCREENPARAMETERS.WIDTH / 2, SCREENPARAMETERS.HEIGHT / 2, 0), new Vector3D(),
				iterations);
		for (int i = 0; i < numCircles; i++) {
			interpolater.addCircle(new FourierCircle(10 + MathUtils.RANDOM.nextDouble() * 200, MathUtils.RANDOM.nextDouble() * 60 - 30));
		}
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

		final Collection<Particle<Color>> rPoints = new ArrayList<>(particles);

		Particle<Color> previousPoint = null;
		for (Particle<Color> point : rPoints) {
			final Color color = point.getAttribute();

			if (previousPoint != null) {
				graphicsSubsystem.drawLine((int) previousPoint.getPosition().x(), (int) previousPoint.getPosition().y(),
						(int) point.getPosition().x(), (int) point.getPosition().y(), color, color);
			}

			previousPoint = point;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.sim.SimulationScreen#calculate(java.awt.Dimension)
	 */
	@Override
	protected void calculate(Dimension dimension) throws Exception {

		if (init) {

			init = false;
			initFourier();

		}

		final int effectiveSpeed = SPEED < 1 ? 1 : SPEED;
		for (int i = 0; i < effectiveSpeed; i++) {
			if (interpolater.hasNext()) {

				final Vector3D point = interpolater.next().getPoint();
				final Particle<Color> particle = new Particle<>(point);
				final int restvalue = (int) MathUtils.morphRange(0, iterations, 0, 155, iteration);
				particle.setAttribute(new Color(255, restvalue, 0, 10));
				particles.add(particle);
				iteration++;

				if (!interpolater.hasNext()) {
					final Collection<FourierCircle> circles = interpolater.getCircles();
					System.out.println("circles: " + circles.size() + ": " + circles);
				}
			}
		}
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
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		init = true;
	}

}
