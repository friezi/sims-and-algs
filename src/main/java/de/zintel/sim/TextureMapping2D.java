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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.zintel.gfx.Koordination;
import de.zintel.gfx.g2d.APointInterpolater2D;
import de.zintel.gfx.g2d.BezierPointInterpolater;
import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.Pin2D;
import de.zintel.gfx.g2d.Tetragon2D;
import de.zintel.gfx.g2d.View2D;
import de.zintel.gfx.texture.ITexture;
import de.zintel.gfx.texture.ImageTexture;
import de.zintel.gfx.texture.SmoothingFilter;
import de.zintel.gfx.texture.TxCrd;
import de.zintel.math.MathUtils;
import de.zintel.utils.Processor;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class TextureMapping2D extends JPanel implements MouseListener, ActionListener {

	private static final int SPEED = 20;

	private static final int MAX_TEXTURE_X = 1000;
	private static final int MAX_TEXTURE_Y = 260;

	private static final int maxControlPoints = 20;
	private static final boolean SCALE_BRIGHTNESS = false;

	private static Koordination koordination = new Koordination();

	private static final View2D VIEW = new View2D(koordination.HEIGHT, koordination.XNULL, koordination.YNULL);

	private static class ColorPoint {

		private final Point point;

		private final Color color;

		public ColorPoint(Point point, Color color) {
			super();
			this.point = point;
			this.color = color;
		}

		public Point getPoint() {
			return point;
		}

		public Color getColor() {
			return color;
		}

	}

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

	private ITexture texture;

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

		TextureMapping2D application = new TextureMapping2D();
		application.start();

	}

	public void start() throws Exception {

		initAnimation();

		mainFrame = new JFrame("Texture mapping and animation 2D");
		mainFrame.addMouseListener(this);
		mainFrame.setSize(koordination.WIDTH, koordination.HEIGHT);
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

		texture = new SimpleTexture(width, height, colors);
	}

	private void makeImageTexture() throws IOException {
		texture = new SmoothingFilter(new ImageTexture(getClass().getClassLoader().getResourceAsStream("pics/Schimpanse.jpg")));

	}

	private void drawTexture(Graphics g) {

		final int minx = 0;
		final int miny = 0;

		for (int y = 0; y < texture.getHeight(); y++) {
			for (int x = 0; x < texture.getWidth(); x++) {
				g.setColor(texture.getColor(x, y));
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

		stepperInfos = new TreeSet<TextureMapping2D.StepperInfo>(new StepperComparator());

		l11 = new Processor<IterationUnit2D>(makeRandomInterpolater(), stepUnit -> {
			p11 = stepUnit;
		});
		l11.next();
		stepperInfos.add(new StepperInfo(l11, p11));

		l12 = new Processor<IterationUnit2D>(makeRandomInterpolater(), stepUnit -> {
			p12 = stepUnit;
		});
		l12.next();
		stepperInfos.add(new StepperInfo(l12, p12));

		l21 = new Processor<IterationUnit2D>(makeRandomInterpolater(), stepUnit -> {
			p21 = stepUnit;
		});
		l21.next();
		stepperInfos.add(new StepperInfo(l21, p21));

		l22 = new Processor<IterationUnit2D>(makeRandomInterpolater(), stepUnit -> {
			p22 = stepUnit;
		});
		l22.next();
		stepperInfos.add(new StepperInfo(l22, p22));

		Iterator<StepperInfo> iterator = stepperInfos.iterator();
		mainStepperInfo = iterator.next();
		iterator.remove();

	}

	private APointInterpolater2D makeRandomInterpolater() {

		final BezierPointInterpolater interpolater = new BezierPointInterpolater(
				new Point(RANDOM.nextInt(koordination.RENDER_MAX_RAND_X),
						koordination.RENDER_STARTY + RANDOM.nextInt(koordination.RENDER_MAX_RAND_Y)),
				new Point(RANDOM.nextInt(koordination.RENDER_MAX_RAND_X),
						koordination.RENDER_STARTY + RANDOM.nextInt(koordination.RENDER_MAX_RAND_Y)),
				false, false);
		int max = RANDOM.nextInt(maxControlPoints);
		for (int i = 0; i < max; i++) {
			interpolater.addControlPoint(new Point(RANDOM.nextInt(koordination.RENDER_MAX_RAND_X),
					koordination.RENDER_STARTY + RANDOM.nextInt(koordination.RENDER_MAX_RAND_Y)));
		}

		return interpolater;

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
			if (mainStepperInfo.getStepper().hasNext()) {
				mainStepperInfo.getStepper().next();

				for (StepperInfo stepperInfo : stepperInfos) {
					if (MathUtils.interpolateLinear(0, stepperInfo.getStepUnit().getMaxIterations(),
							mainStepperInfo.getStepper().getCurrent().getIteration(),
							mainStepperInfo.getStepper().getCurrent().getMaxIterations()) > stepperInfo.getStepUnit().getIteration()) {

						stepperInfo.getStepper().next();
						stepperInfo.setStepUnit(stepperInfo.getStepper().getCurrent());

					}
				}
			}
		}

		repaint();
		if (!mainStepperInfo.getStepper().hasNext()) {

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

	private void renderColored(Graphics graphics) {

		final int max_rgb = 256;

		final Color ssColor = new Color(RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb));
		final Color tsColor = new Color(RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb));
		final Color stColor = new Color(RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb));
		final Color ttColor = new Color(RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb), RANDOM.nextInt(max_rgb));

		List<ColorPoint> points1 = new ArrayList<>();
		List<ColorPoint> points2 = new ArrayList<>();

		new Processor<IterationUnit2D>(new LinearPointInterpolater2D(p11.getPoint(), p12.getPoint(), false),
				new Consumer<IterationUnit2D>() {

					@Override
					public void accept(IterationUnit2D stepUnit) {

						int step = stepUnit.getIteration();
						int stepMax = stepUnit.getMaxIterations();
						points1.add(new ColorPoint(stepUnit.getPoint(),
								new Color(MathUtils.interpolateLinear(ssColor.getRed(), stColor.getRed(), step, stepMax),
										MathUtils.interpolateLinear(ssColor.getGreen(), stColor.getGreen(), step, stepMax),
										MathUtils.interpolateLinear(ssColor.getBlue(), stColor.getBlue(), step, stepMax))));
					}
				}).iterate();

		new Processor<IterationUnit2D>(new LinearPointInterpolater2D(p21.getPoint(), p22.getPoint(), false),
				new Consumer<IterationUnit2D>() {

					@Override
					public void accept(IterationUnit2D stepUnit) {

						int step = stepUnit.getIteration();
						int stepMax = stepUnit.getMaxIterations();
						points2.add(new ColorPoint(stepUnit.getPoint(),
								new Color(MathUtils.interpolateLinear(tsColor.getRed(), ttColor.getRed(), step, stepMax),
										MathUtils.interpolateLinear(tsColor.getGreen(), ttColor.getGreen(), step, stepMax),
										MathUtils.interpolateLinear(tsColor.getBlue(), ttColor.getBlue(), step, stepMax))));
					}
				}).iterate();

		List<ColorPoint> startpoints;
		List<ColorPoint> endpoints;

		if (points1.size() >= points2.size()) {
			startpoints = points1;
			endpoints = points2;
		} else {
			startpoints = points2;
			endpoints = points1;
		}

		for (int i = 0; i < startpoints.size(); i++) {

			ColorPoint p1 = startpoints.get(i);
			ColorPoint p2 = endpoints.get((i * endpoints.size()) / startpoints.size());

			Color sColor = p1.getColor();
			Color tColor = p2.getColor();

			new Processor<IterationUnit2D>(new LinearPointInterpolater2D(p1.getPoint(), p2.getPoint(), true),
					new Consumer<IterationUnit2D>() {

						@Override
						public void accept(IterationUnit2D stepUnit) {

							int step = stepUnit.getIteration();
							int stepMax = stepUnit.getMaxIterations();
							Point point = stepUnit.getPoint();

							graphics.setColor(new Color(MathUtils.interpolateLinear(sColor.getRed(), tColor.getRed(), step, stepMax),
									MathUtils.interpolateLinear(sColor.getGreen(), tColor.getGreen(), step, stepMax),
									MathUtils.interpolateLinear(sColor.getBlue(), tColor.getBlue(), step, stepMax)));
							graphics.drawLine(point.x, point.y, point.x, point.y);

						}
					}).iterate();
		}
	}

	private void renderTextured(Graphics graphics) {

		new Tetragon2D(new Pin2D(p11.getPoint(), new TxCrd(0, 1)), new Pin2D(p12.getPoint(), new TxCrd(0, 0)),
				new Pin2D(p21.getPoint(), new TxCrd(1, 1)), new Pin2D(p22.getPoint(), new TxCrd(1, 0)), texture).draw(new Point(0, 0),
						graphics);
	}

	private int minDark(final int colorValue) {
		return colorValue / 2;
	}

	private int maxBright(final int colorValue) {
		return colorValue + (255 - colorValue) / 2;
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
