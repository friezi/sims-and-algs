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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.g2d.BezierPointInterpolater;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class Bezier extends JPanel implements MouseListener, ActionListener {

	private static final int MAX_TEXTURE_X = 1000;
	private static final int MAX_TEXTURE_Y = 260;

	private static final boolean SHOW_CONTROLPOINTS = false;
	private static final int MAX_ITERATIONS = 200;
	private static final boolean SCATTERING = false;
	private static final boolean AUTOCONNECT_POINTS = true;

	private static final Random RANDOM = new Random();

	private JFrame mainFrame;

	private BezierPointInterpolater interpolater;

	private List<Point> points = new ArrayList<>();

	private int iterations = 0;

	private ScreenParameters screenParameters = new ScreenParameters();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Bezier application = new Bezier();
		application.start();

	}

	public void start() throws Exception {

		init();

		mainFrame = new JFrame("Static Texture mapping");
		mainFrame.addMouseListener(this);
		mainFrame.setSize(screenParameters.WIDTH, screenParameters.HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel gfxPanel = this;
		gfxPanel.setLayout(new BoxLayout(gfxPanel, BoxLayout.Y_AXIS));
		gfxPanel.setOpaque(true);
		mainFrame.setContentPane(gfxPanel);

		mainFrame.setVisible(true);

	}

	private void init() {

		points.clear();
		interpolater = new BezierPointInterpolater(makeRandomPoint(), makeRandomPoint(), AUTOCONNECT_POINTS, SCATTERING);

		iterations = RANDOM.nextInt(MAX_ITERATIONS) + 1;
		for (int i = 0; i < iterations; i++) {
			interpolater.addControlPoint(makeRandomPoint());
		}

		while (interpolater.hasNext()) {
			points.add(interpolater.next().getPoint());
		}

	}

	private Point makeRandomPoint() {
		return new Point(RANDOM.nextInt(screenParameters.RENDER_MAX_RAND_X),
				screenParameters.RENDER_STARTY + RANDOM.nextInt(screenParameters.RENDER_MAX_RAND_Y));
	}

	private void draw(Graphics graphics) {

		graphics.setColor(Color.RED);

		graphics.drawString("iterations=" + iterations, 10, 10);

		for (Point point : points) {
			graphics.drawLine(point.x, point.y, point.x, point.y);
		}
		//
		// final Point p1 = points.get(0);
		// final Point p2 = points.get(points.size() - 1);
		// p1.y -= 10;
		// p2.y -= 10;
		// final APointInterpolater2D controlInterpolater = new
		// LinearPointInterpolater2D(p1, p2, false);
		// while (controlInterpolater.hasNext()) {
		// final IterationUnit2D next = controlInterpolater.next();
		// final Point point = next.getPoint();
		// graphics.drawLine(point.x, point.y, point.x, point.y);
		// }

		if (SHOW_CONTROLPOINTS) {

			graphics.setColor(Color.GREEN);
			final List<Point> controlPoints = interpolater.getControlPoints();
			for (int i = 0; i < controlPoints.size() - 1; i++) {

				final Point s = controlPoints.get(i);
				final Point e = controlPoints.get(i + 1);
				graphics.drawLine(s.x, s.y, e.x, e.y);
			}

			int i = 1;
			for (Point point : controlPoints) {
				graphics.drawString("c" + i++, point.x, point.y);
			}

			Point point;
			graphics.setColor(Color.BLUE);

			point = interpolater.getStart();
			graphics.drawString("s", point.x, point.y);

			point = interpolater.getEnd();
			graphics.drawString("e", point.x, point.y);

			if (!controlPoints.isEmpty()) {

				Point s = interpolater.getStart();
				Point e = controlPoints.get(0);
				graphics.drawLine(s.x, s.y, e.x, e.y);

				s = controlPoints.get(controlPoints.size() - 1);
				e = interpolater.getEnd();
				graphics.drawLine(s.x, s.y, e.x, e.y);

			}
		}

	}

	@Override
	protected void paintComponent(Graphics graphics) {

		super.paintComponent(graphics);
		draw(graphics);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		init();
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
