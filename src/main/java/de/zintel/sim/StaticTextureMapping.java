/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.zintel.gfx.Coordination;
import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.Pin2D;
import de.zintel.gfx.g2d.Tetragon2D;
import de.zintel.gfx.g2d.View2D;
import de.zintel.gfx.texture.BilinearFilter;
import de.zintel.gfx.texture.ITexture;
import de.zintel.gfx.texture.ImageTexture;
import de.zintel.gfx.texture.InvertFilter;
import de.zintel.gfx.texture.MorphTexture;
import de.zintel.gfx.texture.TxCrd;
import de.zintel.math.MathUtils;
import de.zintel.math.VectorND;
import de.zintel.utils.Processor;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class StaticTextureMapping extends JPanel implements MouseListener, ActionListener {

	private static Coordination coordination = new Coordination();

	private static final int SPEED = 20;

	private final Random rnd = new Random();

	private static final int MAX_TEXTURE_X = 1000;
	private static final int MAX_TEXTURE_Y = 260;

	private static final boolean SCALE_BRIGHTNESS = false;

	private static final View2D VIEW = new View2D(coordination.HEIGHT, coordination.XNULL, coordination.YNULL);

	private static final class SimpleTexture implements ITexture {

		private final int width;
		private final int height;
		private final Color[][] colors;

		public SimpleTexture(int width, int height, Color[][] colors) {
			super();
			this.width = width;
			this.height = height;
			this.colors = colors;
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public Color getColor(double x, double y) {
			return colors[(int) x][(int) y];
		}

	}

	private static final class MorphFunctionSpec {

		private final Function<VectorND, Double> morphFunction;

		private final VectorND xRange;

		private final VectorND yRange;

		public MorphFunctionSpec(Function<VectorND, Double> morphFunction, double xMin, double xMax, double yMin, double yMax) {
			this.morphFunction = morphFunction;
			this.xRange = new VectorND(Arrays.asList(xMin, xMax));
			this.yRange = new VectorND(Arrays.asList(yMin, yMax));
		}

		public Function<VectorND, Double> getMorphFunction() {
			return morphFunction;
		}

		public VectorND getxRange() {
			return xRange;
		}

		public VectorND getyRange() {
			return yRange;
		}

	}

	private static final class StepperInfo {

		private final Processor<IterationUnit2D> stepper;

		private IterationUnit2D stepUnit;

		public void setStepUnit(IterationUnit2D stepUnit) {
			this.stepUnit = stepUnit;
		}

		public StepperInfo(Processor<IterationUnit2D> stepper, IterationUnit2D stepUnit) {
			super();
			this.stepper = stepper;
			this.stepUnit = stepUnit;
		}

		public Processor<IterationUnit2D> getStepper() {
			return stepper;
		}

		public IterationUnit2D getStepUnit() {
			return stepUnit;
		}

	}

	private static class StepperComparator implements Comparator<StepperInfo> {

		@Override
		public int compare(StepperInfo o1, StepperInfo o2) {
			return (o1.getStepUnit().getMaxIterations() >= o2.getStepUnit().getMaxIterations() ? -1 : 1);
		}
	}

	private static final Random RANDOM = new Random();

	private JFrame mainFrame;

	private ITexture texture_noninterpolated;

	private ITexture texture_interpolated;

	private ITexture texture_inverted;

	private ITexture texture_morphed;

	private IterationUnit2D p11;
	private IterationUnit2D p12;
	private IterationUnit2D p21;
	private IterationUnit2D p22;

	private Processor<IterationUnit2D> l11;
	private Processor<IterationUnit2D> l12;
	private Processor<IterationUnit2D> l21;
	private Processor<IterationUnit2D> l22;

	private Set<StepperInfo> stepperInfos;
	private StepperInfo mainStepperInfo;

	private Timer timer;

	private boolean isAnimation = false;
	private boolean isRendering = false;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		StaticTextureMapping application = new StaticTextureMapping();
		application.start();

	}

	public void start() throws Exception {

		initAnimation();

		mainFrame = new JFrame("Static Texture mapping");
		// mainFrame.addMouseListener(this);
		mainFrame.setSize(coordination.WIDTH, coordination.HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel gfxPanel = this;
		gfxPanel.setLayout(new BoxLayout(gfxPanel, BoxLayout.Y_AXIS));
		gfxPanel.setOpaque(true);
		mainFrame.setContentPane(gfxPanel);

		mainFrame.setVisible(true);

	}

	private void makeSimpleTexture() {

		int width = RANDOM.nextInt(MAX_TEXTURE_X) + 10;
		int height = RANDOM.nextInt(MAX_TEXTURE_Y) + 10;
		Color[][] colors = new Color[width][height];

		final Color tColor1 = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
		final Color tColor2 = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));

		final int rs = RANDOM.nextInt(80) + 10;
		final int rx = RANDOM.nextInt(3) + 2;
		final int ry = RANDOM.nextInt(3) + 2;

		for (int y = 0; y < height; y++) {

			int my = (y % rs < rs / ry ? -1 : 1);
			for (int x = 0; x < width; x++) {

				int mx = (x % rs < rs / rx ? 1 : -1);
				int ci = mx * my;

				colors[x][y] = (ci == 1 ? tColor1 : tColor2);

			}
		}

		texture_noninterpolated = new SimpleTexture(width, height, colors);
	}

	private void makeImageTexture() throws IOException {

		texture_noninterpolated = new ImageTexture(getClass().getClassLoader().getResourceAsStream("pics/Schimpanse_klein.jpg"));
		texture_interpolated = new BilinearFilter(
				new ImageTexture(getClass().getClassLoader().getResourceAsStream("pics/Schimpanse_klein.jpg")));
		texture_inverted = new InvertFilter(
				new BilinearFilter(new ImageTexture(getClass().getClassLoader().getResourceAsStream("pics/Schimpanse_klein.jpg"))));

		final List<MorphFunctionSpec> morphFunctionSpecs = Arrays.asList(
				new MorphFunctionSpec(xy -> Math.sin(xy.get(0)) * Math.sin(xy.get(1)), 0.0, Math.PI, 0.0, Math.PI),
				new MorphFunctionSpec(xy -> MathUtils.sigmoid(xy.get(0)) * MathUtils.sigmoid(xy.get(1)), -8.0, 8.0, -8.0, 8.0),
				new MorphFunctionSpec(xy -> (double) ((int) ((xy.get(0) + xy.get(1)))) % 2, 0.0, (double) texture_interpolated.getWidth(),
						0.0, (double) texture_interpolated.getHeight()),

				// Gauss'sche Normalverteilung:
				new MorphFunctionSpec(
						xy -> MathUtils.morphRange(0, 0.1, 0, 1.0, 1 / (2 * Math.PI) * Math.pow(Math.E, -0.5 * Math.pow(xy.get(0), 2)))
								* MathUtils.morphRange(0, 0.1, 0, 1.0, 1 / (2 * Math.PI) * Math.pow(Math.E, -0.5 * Math.pow(xy.get(1), 2))),
						-4, 4, -4, 4),
				new MorphFunctionSpec(xy -> (double) rnd.nextInt(2), 0.0, (double) texture_interpolated.getWidth(), 0.0,
						(double) texture_interpolated.getHeight()));
		final MorphFunctionSpec morphFunctionSpec = morphFunctionSpecs.get(rnd.nextInt(morphFunctionSpecs.size()));

		texture_morphed = new MorphTexture(texture_interpolated, texture_inverted, morphFunctionSpec.getMorphFunction(),
				morphFunctionSpec.getxRange(), morphFunctionSpec.getyRange());
	}

	private void drawTexture(Graphics g) {

		final int minx = 0;
		final int miny = 0;

		for (int y = 0; y < texture_noninterpolated.getHeight(); y++) {
			for (int x = 0; x < texture_noninterpolated.getWidth(); x++) {
				g.setColor(texture_noninterpolated.getColor(x, y));
				g.drawLine(minx + x, miny + y, minx + x, miny + y);
			}
		}

	}

	private void initTimer() {

		isAnimation = true;
		timer = new Timer(1, this);
		timer.start();

	}

	private void init() {

		stepperInfos = new TreeSet<StaticTextureMapping.StepperInfo>(new StepperComparator());

		l11 = new Processor<IterationUnit2D>(new LinearPointInterpolater2D(
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				false), stepUnit -> {
					p11 = stepUnit;
				});
		l11.progress();
		stepperInfos.add(new StepperInfo(l11, p11));

		l12 = new Processor<IterationUnit2D>(new LinearPointInterpolater2D(
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				false), stepUnit -> {
					p12 = stepUnit;
				});
		l12.progress();
		stepperInfos.add(new StepperInfo(l12, p12));

		l21 = new Processor<IterationUnit2D>(new LinearPointInterpolater2D(
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				false), stepUnit -> {
					p21 = stepUnit;
				});
		l21.progress();
		stepperInfos.add(new StepperInfo(l21, p21));

		l22 = new Processor<IterationUnit2D>(new LinearPointInterpolater2D(
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				new Point(RANDOM.nextInt(coordination.RENDER_MAX_RAND_X),
						coordination.RENDER_STARTY + RANDOM.nextInt(coordination.RENDER_MAX_RAND_Y)),
				false), stepUnit -> {
					p22 = stepUnit;
				});
		l22.progress();
		stepperInfos.add(new StepperInfo(l22, p22));

		Iterator<StepperInfo> iterator = stepperInfos.iterator();
		mainStepperInfo = iterator.next();
		iterator.remove();

	}

	private void initAnimation() throws Exception {

		if (!isAnimation) {

			makeImageTexture();
			init();
			repaint();
			initTimer();

		}
	}

	private void animate() {

		isRendering = true;

		for (int i = 1; i < SPEED; i++) {
			if (mainStepperInfo.getStepper().inProcess()) {
				mainStepperInfo.getStepper().progress();

				for (StepperInfo stepperInfo : stepperInfos) {
					if (MathUtils.interpolateLinear(0, stepperInfo.getStepUnit().getMaxIterations(),
							mainStepperInfo.getStepper().getCurrent().getIteration(),
							mainStepperInfo.getStepper().getCurrent().getMaxIterations()) > stepperInfo.getStepUnit().getIteration()) {

						stepperInfo.getStepper().progress();
						stepperInfo.setStepUnit(stepperInfo.getStepper().getCurrent());

					}
				}
			}
		}

		repaint();
		if (!mainStepperInfo.getStepper().inProcess()) {

			timer.stop();
			isAnimation = false;

		}

		isRendering = false;

	}

	@Override
	protected void paintComponent(Graphics graphics) {

		super.paintComponent(graphics);

		drawTexture(graphics);
		// renderColored(graphics);
		renderTextured(graphics);
	}

	private void renderTextured(Graphics graphics) {

		int factor = 8;

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 0)),
				new Pin2D(new Point(0, texture_noninterpolated.getHeight() * factor), new TxCrd(0, 1)),
				new Pin2D(new Point(texture_noninterpolated.getWidth() * factor, 0), new TxCrd(1, 0)),
				new Pin2D(new Point(texture_noninterpolated.getWidth() * factor, texture_noninterpolated.getHeight() * factor),
						new TxCrd(1, 1)),
				texture_noninterpolated).draw(new Point(texture_noninterpolated.getWidth() + 10, 0), graphics);

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 0)),
				new Pin2D(new Point(0, texture_interpolated.getHeight() * factor), new TxCrd(0, 1)),
				new Pin2D(new Point(texture_interpolated.getWidth() * factor, 0), new TxCrd(1, 0)),
				new Pin2D(new Point(texture_interpolated.getWidth() * factor, texture_interpolated.getHeight() * factor), new TxCrd(1, 1)),
				texture_interpolated).draw(new Point(texture_interpolated.getWidth() * factor + 60, 0), graphics);

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 1)),
				new Pin2D(new Point(0, texture_noninterpolated.getHeight() * factor), new TxCrd(0, 0)),
				new Pin2D(new Point(texture_noninterpolated.getWidth() * factor, 0), new TxCrd(1, 1)),
				new Pin2D(new Point(texture_noninterpolated.getWidth() * factor, texture_noninterpolated.getHeight() * factor),
						new TxCrd(1, 0)),
				texture_noninterpolated).draw(new Point(2 * texture_noninterpolated.getWidth() * factor + 60, 0), graphics);

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 0)),
				new Pin2D(new Point(0, texture_inverted.getHeight() * factor), new TxCrd(0, 1)),
				new Pin2D(new Point(texture_inverted.getWidth() * factor, 0), new TxCrd(1, 0)),
				new Pin2D(new Point(texture_inverted.getWidth() * factor, texture_inverted.getHeight() * factor), new TxCrd(1, 1)),
				texture_inverted).draw(new Point(texture_inverted.getWidth() * factor + 60, texture_inverted.getHeight() * factor + 10),
						graphics);

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 0)),
				new Pin2D(new Point(0, texture_morphed.getHeight() * factor), new TxCrd(0, 1)),
				new Pin2D(new Point(texture_morphed.getWidth() * factor, 0), new TxCrd(1, 0)),
				new Pin2D(new Point(texture_morphed.getWidth() * factor, texture_morphed.getHeight() * factor), new TxCrd(1, 1)),
				texture_morphed).draw(new Point(2 * texture_morphed.getWidth() * factor + 60, texture_morphed.getHeight() * factor + 10),
						graphics);

		new Tetragon2D(new Pin2D(new Point(0, 0), new TxCrd(0, 0)),
				new Pin2D(new Point(0, texture_interpolated.getHeight() * factor), new TxCrd(0, 1)),
				new Pin2D(new Point(texture_interpolated.getWidth() * factor, 0), new TxCrd(1, 0)),
				new Pin2D(new Point(texture_interpolated.getWidth() * factor, texture_interpolated.getHeight() * factor), new TxCrd(1, 1)),
				texture_interpolated, (x, max) -> {
					return x + (x < max / 2 ? -1 : 1) * 100 * Math.sin((x * Math.PI) / max) * Math.cos((x * Math.PI) / max);
				}).draw(new Point(0, texture_interpolated.getHeight() * factor + 10), graphics);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		try {
			initAnimation();
		} catch (Exception e) {
			e.printStackTrace();
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
	public void actionPerformed(ActionEvent arg0) {
		if (!isRendering) {
			animate();
		}
	}

}
