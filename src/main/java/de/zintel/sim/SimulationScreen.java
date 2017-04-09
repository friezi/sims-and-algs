/**
 * 
 */
package de.zintel.sim;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import de.zintel.gfx.GfxUtils;
import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.gfx.Koordination;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystemFactory;
import de.zintel.gfx.graphicsubsystem.IRendererListener;

/**
 * @author friedemann.zintel
 *
 */
public abstract class SimulationScreen implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener, IRendererListener {

	private static final int DFLT_CALCULATION_RATE = 1000 / 60;

	private final boolean doRecord;

	private final String recordFilename;

	private final int recordingRate;

	private final IGraphicsSubsystem graphicsSubsystem;

	private boolean stopped = false;

	private long calculations = 0;

	private long rStartTs = 0;

	private long renderings = 0;

	private int calculationRate = DFLT_CALCULATION_RATE;

	private long maxFrames = 0;

	private volatile boolean paused = false;

	public SimulationScreen(String title, EGraphicsSubsystem gfxSsystem, Koordination coordination, boolean doRecord, String recordFilename,
			int recordingRate) {
		this.doRecord = doRecord;
		this.recordFilename = recordFilename;
		this.recordingRate = recordingRate;

		final IGraphicsSubsystemFactory graphicsSubsystemFactory = GfxUtils.graphicsSubsystemFactories.get(gfxSsystem);
		graphicsSubsystem = graphicsSubsystemFactory.newGraphicsSubsystem(title, coordination.WIDTH, coordination.HEIGHT);
	}

	public void start() throws Exception {

		graphicsSubsystem.init(doRecord, recordFilename);

		graphicsSubsystem.setFullScreen();
		graphicsSubsystem.addMouseListener(this);
		graphicsSubsystem.addMouseWheelListener(this);
		graphicsSubsystem.addMouseMotionListener(this);
		graphicsSubsystem.addKeyListener(this);
		graphicsSubsystem.addRendererListener(this);

		init(graphicsSubsystem);

		graphicsSubsystem.display();

		loop();

	}

	/**
	 * @param graphicsSubsystem
	 */
	protected abstract void init(final IGraphicsSubsystem graphicsSubsystem);

	/**
	 * @throws Exception
	 */
	private void loop() throws Exception {

		long crStartTs = 0;
		long rIter = 0;
		while (!stopped && (!doRecord || maxFrames <= 0 || rIter < maxFrames)) {

			long startTs = System.currentTimeMillis();

			if (!paused) {

				calculations++;
				if (crStartTs == 0) {
					crStartTs = System.currentTimeMillis();
				}

				calculate(graphicsSubsystem.getDimension());

				if (!doRecord || calculations % recordingRate == 0) {
					rIter++;
					graphicsSubsystem.repaint();
				}

				long crStopTs = System.currentTimeMillis();
				if (crStopTs - crStartTs >= 1000) {

					double calculationrate = calculations / ((crStopTs - crStartTs) / (double) 1000);
					System.out.println("calculationrate: " + calculationrate + " cps");

					crStartTs = System.currentTimeMillis();
					calculations = 0;

				}
			}
			long diffTs = System.currentTimeMillis() - startTs;
			if (diffTs < calculationRate) {
				Thread.sleep(calculationRate - diffTs);
			}
		}

		shutdown();
		graphicsSubsystem.shutdown();
		System.exit(0);

	}

	@Override
	public final void render(IGraphicsSubsystem graphicsSubsystem) {

		renderings++;

		if (rStartTs == 0) {
			rStartTs = System.currentTimeMillis();
		}

		renderSim(graphicsSubsystem);

		long rStopTs = System.currentTimeMillis();
		if (rStopTs - rStartTs >= 1000) {

			double framerate = renderings / ((rStopTs - rStartTs) / (double) 1000);
			System.out.println("framerate: " + framerate + " fps");

			rStartTs = System.currentTimeMillis();
			renderings = 0;

		}

	}

	protected abstract void renderSim(IGraphicsSubsystem graphicsSubsystem);

	/**
	 * @throws Exception
	 */
	protected abstract void calculate(Dimension dimension) throws Exception;

	/**
	 * @throws Exception
	 */
	protected abstract void shutdown() throws Exception;

	@Override
	public void keyReleased(KeyEvent ke) {

		if (ke.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
			stopped = true;
		} else if (ke.getExtendedKeyCode() == KeyEvent.VK_SPACE) {
			paused ^= true;
		}

	}

	public IGraphicsSubsystem getGraphicsSubsystem() {
		return graphicsSubsystem;
	}

	public int getCalculationRate() {
		return calculationRate;
	}

	/**
	 * calculations per millisecond.
	 * 
	 * @param calculationRate
	 */
	public void setCalculationRate(int calculationRate) {
		this.calculationRate = calculationRate;
	}

	public long getMaxFrames() {
		return maxFrames;
	}

	public void setMaxFrames(long maxFrames) {
		this.maxFrames = maxFrames;
	}

}
