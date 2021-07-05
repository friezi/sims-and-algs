/**
 * 
 */
package de.zintel.sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.zintel.gfx.ScreenParameters;
import de.zintel.gfx.g3d.EpicyclesPointGenerator;
import de.zintel.gfx.g3d.EpicyclesPointGenerator.Epicycle;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.sim.epicycles.FileCyclesSet;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class Fourier extends JPanel implements MouseListener, ActionListener {

	private static final int MAX_TEXTURE_X = 1000;
	private static final int MAX_TEXTURE_Y = 260;

	private static final boolean SHOW_CONTROLPOINTS = false;
	private static final int MAX_ITERATIONS = 200;
	private static final boolean SCATTERING = false;
	private static final boolean AUTOCONNECT_POINTS = true;

	private static final Random RANDOM = new Random();

	private JFrame mainFrame;

	private EpicyclesPointGenerator interpolater;

	private List<Vector3D> points = new LinkedList<>();

	private int iterations = 0;

	private ScreenParameters screenParameters = new ScreenParameters();

	private FileCyclesSet cycleSet;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Fourier application = new Fourier();
		application.start();

	}

	public void start() throws Exception {

		cycleSet = new FileCyclesSet(getClass().getClassLoader().getResourceAsStream("coords/epicycles.txt"));

		init();

		mainFrame = new JFrame("Fourier");
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
		iterations = 10000/* MathUtils.RANDOM.nextInt(1000) + 1000 */;
		interpolater = new EpicyclesPointGenerator(new Vector3D(screenParameters.WIDTH / 2, screenParameters.HEIGHT / 2, 0), new Vector3D(),
				iterations);
//		for (int i = 0; i < MathUtils.RANDOM.nextInt(30) + 2; i++) {
//			interpolater.addCircle(new Epicycle(10 + MathUtils.RANDOM.nextDouble() * 200, MathUtils.RANDOM.nextDouble() * 60 - 30));
//		}

		 for (final Epicycle cycle : cycleSet.getCycles()) {
		 interpolater.addCircle(cycle);
		 }

		while (interpolater.hasNext()) {
			points.add(interpolater.next().getElement());
		}

	}

	private void draw(Graphics graphics) {

		Vector3D previousPoint = null;

		graphics.drawString("iterations=" + iterations, 10, 10);

		for (Vector3D point : points) {

			if (previousPoint != null) {
				graphics.setColor(Color.RED);
				graphics.drawLine((int) previousPoint.x(), (int) previousPoint.y(), (int) point.x(), (int) point.y());
			} else {
				graphics.setColor(Color.GREEN.darker());
				graphics.fillOval((int) point.x(), (int) point.y(), 10, 10);
			}

			previousPoint = point;
		}

		graphics.setColor(Color.BLUE);
		graphics.fillOval((int) previousPoint.x(), (int) previousPoint.y(), 10, 10);

		final Collection<Epicycle> circles = interpolater.getCircles();
		System.out.println("circles: " + circles.size() + ": " + circles);

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
