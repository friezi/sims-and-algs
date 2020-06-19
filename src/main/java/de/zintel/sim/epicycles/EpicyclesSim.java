/**
 * 
 */
package de.zintel.sim.epicycles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import de.zintel.gfx.GfxUtils.EGraphicsSubsystem;
import de.zintel.control.IKeyAction;
import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.color.EColorMixture;
import de.zintel.gfx.g3d.EpicyclesPointGenerator;
import de.zintel.gfx.g3d.EpicyclesPointGenerator.Epicycle;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.physics.particles.Particle;
import de.zintel.sim.SimulationScreen;
import de.zintel.utils.Pair;

/**
 * @author friedo
 *
 */
public class EpicyclesSim extends SimulationScreen {

	private int speed = 200;

	private final static ScreenParameters SCREENPARAMETERS = new ScreenParameters();

	private EColorMixture colorMixture = EColorMixture.ADDITIVE;

	private static final Color COLOR_BACKGROUND = new Color(0, 0, 50);

	private EpicyclesPointGenerator interpolater;

	private Collection<Particle<Color>> particles = Collections.synchronizedCollection(new LinkedList<>());

	private volatile boolean init = true;

	private volatile boolean showCircles = false;

	private int iterations = 0;

	private int iteration = 0;

	private int iterationReduction;
	
//	 private Supplier<IECArgumentSet> argumentSetSupplier = () ->
//	 getParameterSetUnperiodical();

	private Supplier<IECArgumentSet> argumentSetSupplier = getParameterSupplierFile("coords/epicycles.txt");

	/**
	 * @param title
	 * @param gfxSsystem
	 * @param screenParameters
	 * @param doRecord
	 * @param recordFilename
	 * @param recordingRate
	 */
	public EpicyclesSim(String title, EGraphicsSubsystem gfxSsystem, ScreenParameters screenParameters, boolean doRecord,
			String recordFilename, int recordingRate) {
		super(title, gfxSsystem, screenParameters, doRecord, recordFilename, recordingRate);
	}

	public static void main(String args[]) throws Exception {
		new EpicyclesSim("Fourier", GFX_SSYSTEM, SCREENPARAMETERS, false, "", 0).start();
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

		initKeyActions();
	}

	private void initEpicycles() {

		particles.clear();
		iteration = 0;

		final IECArgumentSet argumentSet = argumentSetSupplier.get();
		iterations = Math.max(argumentSet.getIterations() - iterationReduction, 1);

		interpolater = new EpicyclesPointGenerator(new Vector3D(SCREENPARAMETERS.WIDTH / 2, SCREENPARAMETERS.HEIGHT / 2, 0), new Vector3D(),
				iterations);
		for (final Epicycle circle : argumentSet.getCycles()) {
			interpolater.addCircle(circle);
		}
		speed = argumentSet.getSpeed();
	}

	private IECArgumentSet getParameterSetObjects() {
		return new IECArgumentSet() {

			@Override
			public int getSpeed() {
				return 20;
			}

			@SuppressWarnings("serial")
			@Override
			public Collection<Epicycle> getCycles() {

				final double angle = 0.1;
				return new LinkedList<Epicycle>() {
					{

						add(new Epicycle(new Vector3D(-12.64, 20.90, 0), 1 * angle));
						add(new Epicycle(new Vector3D(-135.66, -45.57, 0), -1 * angle));
						add(new Epicycle(new Vector3D(-44.85, -23.71, 0), 2 * angle));
						add(new Epicycle(new Vector3D(66.75, -53.07, 0), -2 * angle));

					}
				};
			}

			@Override
			public int getIterations() {
				return 10000;
			}
		};
	}

	private IECArgumentSet getParameterSetPI() {
		return new IECArgumentSet() {

			@Override
			public int getSpeed() {
				return 20;
			}

			@Override
			public Collection<Epicycle> getCycles() {

				final double angle = 0.1;
				return new LinkedList<Epicycle>() {
					{

						int i = 0;
						boolean negative = false;
						final PICoordinates piCoordinates = new PICoordinates();
						for (final Pair<Double, Double> ccor : piCoordinates) {

							add(new Epicycle(new Vector3D(ccor.getFirst(), ccor.getSecond(), 0), (negative ? -1 : 1) * i * angle));
							if (negative) {
								i++;
							}

							negative = !negative;

						}
					}
				};
			}

			@Override
			public int getIterations() {
				return 10000;
			}
		};
	}

	private Supplier<IECArgumentSet> getParameterSupplierFile(final String filename) {

		return new Supplier<IECArgumentSet>() {

			private final IECArgumentSet argumentSet = newSet();

			@Override
			public IECArgumentSet get() {
				return argumentSet;
			}

			private IECArgumentSet newSet() {
				try {
					return new FileCyclesSet(getClass().getClassLoader().getResourceAsStream(filename));
				} catch (IOException e) {

					System.out.println("error on reading '" + filename + "'");

					return new IECArgumentSet() {

						@Override
						public int getSpeed() {
							return 1;
						}

						@Override
						public int getIterations() {
							return 1;
						}

						@Override
						public Collection<Epicycle> getCycles() {
							return Collections.emptyList();
						}
					};
				}
			}
		};
	}

	private IECArgumentSet getParameterSetUnperiodical() {
		return new IECArgumentSet() {

			final int numCircles = MathUtils.RANDOM.nextInt(4) + 2;
			int iterations = (MathUtils.RANDOM.nextInt(10000) + 2000) * numCircles;

			@Override
			public int getSpeed() {
				return MathUtils.RANDOM.nextInt(300) + 30;
			}

			@Override
			public Collection<Epicycle> getCycles() {

				return new LinkedList<Epicycle>() {
					{
						for (int i = 0; i < numCircles; i++) {
							interpolater.addCircle(
									new Epicycle(10 + MathUtils.RANDOM.nextDouble() * 200, MathUtils.RANDOM.nextDouble() * 60 - 30));
						}

					}
				};
			}

			@Override
			public int getIterations() {
				return iterations;
			}
		};
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

		// draw circles
		if (showCircles) {
			Vector3D prev = interpolater.getStart();
			final Color cyan = new Color(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), 150);
			for (final Epicycle circle : interpolater.getCircles()) {
				Vector3D current = Vector3D.add(prev, circle.vector);
				graphicsSubsystem.drawLine((int) prev.x(), (int) prev.y(), (int) current.x(), (int) current.y(), cyan, cyan);
				prev = current;
			}
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
			initEpicycles();

		}

		final int effectiveSpeed = Math.max(interpolater.hasNext() ? speed : speed / 4, 1);
		for (int i = 0; i < effectiveSpeed; i++) {
			if (interpolater.hasNext()) {

				final Vector3D point = interpolater.next().getPoint();
				final Particle<Color> particle = new Particle<>(point);
				final int restvalue = (int) MathUtils.morphRange(0, iterations, 0, 100, iteration);
				particle.setAttribute(new Color(255, restvalue, 0, 100));
				particles.add(particle);
				iteration++;

				if (!interpolater.hasNext()) {
					final Collection<Epicycle> circles = interpolater.getCircles();
					System.out.println("circles: " + circles.size() + ": " + circles);
					// new Timer().schedule(new TimerTask() {
					//
					// @Override
					// public void run() {
					// init = true;
					// }
					// }, 15 * 1000);
				}
			} else {
				final Iterator<Particle<Color>> iterator = particles.iterator();
				if (iterator.hasNext()) {
					iterator.next();
					iterator.remove();
				} else {
					init = true;
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

	private void initKeyActions() {
		addKeyAction(KeyEvent.VK_C, new IKeyAction() {

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
				return "SHC";
			}

			@Override
			public String text() {
				return "show circles";
			}

			@Override
			public void plus() {
				showCircles = true;
			}

			@Override
			public void minus() {
				showCircles = false;
			}

			@Override
			public String getValue() {
				return String.valueOf(showCircles);
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
				return "IRE";
			}

			@Override
			public String text() {
				return "iteration reduction";
			}

			@Override
			public void plus() {
				iterationReduction += 100;
				;
			}

			@Override
			public void minus() {
				iterationReduction -= 100;
			}

			@Override
			public String getValue() {
				return String.valueOf(iterationReduction);
			}
		});
	}
}
