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
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.zintel.gfx.Koordination;
import de.zintel.gfx.g3d.Component3D;
import de.zintel.gfx.g3d.Line3D;
import de.zintel.gfx.g3d.Pin3D;
import de.zintel.gfx.g3d.Point3D;
import de.zintel.gfx.g3d.Projector2D;
import de.zintel.gfx.g3d.Tetragon3D;
import de.zintel.gfx.g3d.Triangle3D;
import de.zintel.gfx.g3d.View3D;
import de.zintel.gfx.texture.ITexture;
import de.zintel.gfx.texture.ImageTexture;
import de.zintel.gfx.texture.TxCrd;

/**
 * @author Friedemann
 *
 */
@SuppressWarnings("serial")
public class TextureMapping3D extends JPanel implements MouseListener, ActionListener {

	private Koordination koordination = new Koordination();

	private Point3D viewpoint = new Point3D(koordination.WIDTH * 2 / 3, 1420, -1000);
	private Point3D nullpoint = new Point3D(30, 200, 100);

	private View3D view = new View3D(nullpoint, new Projector2D(viewpoint, koordination.HEIGHT));

	private JFrame mainFrame;

	private Component3D component;
	private Point3D point;

	private Timer timer;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		TextureMapping3D application = new TextureMapping3D();
		application.start();

	}

	public void start() throws Exception {
		//
		// initAnimation();
		initGraphicalObjects();
		initTimer();

		mainFrame = new JFrame("Texture mapping and animation 3D");
		mainFrame.addMouseListener(this);
		mainFrame.setSize(koordination.WIDTH, koordination.HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel gfxPanel = this;
		gfxPanel.setLayout(new BoxLayout(gfxPanel, BoxLayout.Y_AXIS));
		gfxPanel.setOpaque(true);
		mainFrame.setContentPane(gfxPanel);

		mainFrame.setVisible(true);

	}

	private void initGraphicalObjects() throws IOException {

		ITexture texture = new ImageTexture(getClass().getClassLoader().getResourceAsStream("pics/goethe.jpg"));

		component = new Component3D();

		component.add(makeHouse(texture));
		component.add(makeTriangles(texture));

		point = new Point3D(10, 0, -20);
	}

	private Component3D makeHouse(ITexture texture) {

		Component3D house = new Component3D();
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, 0, 0), new TxCrd(0, 0)),
				new Pin3D(new Point3D(texture.getHeight(), 0, 0), new TxCrd(1, 0)),
				new Pin3D(new Point3D(0, 0, texture.getWidth()), new TxCrd(0, 1)),
				new Pin3D(new Point3D(texture.getHeight(), 0, texture.getWidth()), new TxCrd(1, 1)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, 0, texture.getWidth() + 1), new TxCrd(1, 0)),
				new Pin3D(new Point3D(texture.getHeight(), 0, texture.getWidth() + 1), new TxCrd(0, 0)),
				new Pin3D(new Point3D(0, 0, 2 * texture.getWidth()), new TxCrd(1, 1)),
				new Pin3D(new Point3D(texture.getHeight(), 0, 2 * texture.getWidth()), new TxCrd(0, 1)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, texture.getHeight(), 0), new TxCrd(1, 1)),
				new Pin3D(new Point3D(0, 0, 0), new TxCrd(0, 0)),
				new Pin3D(new Point3D(0, texture.getHeight(), texture.getWidth()), new TxCrd(0, 1)),
				new Pin3D(new Point3D(0, 0, texture.getWidth()), new TxCrd(0, 0)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, texture.getHeight(), texture.getWidth() + 1), new TxCrd(0.5, 1)),
				new Pin3D(new Point3D(0, 0, texture.getWidth() + 1), new TxCrd(1, 0.5)),
				new Pin3D(new Point3D(0, texture.getHeight(), 2 * texture.getWidth()), new TxCrd(1, 1)),
				new Pin3D(new Point3D(0, 0, 2 * texture.getWidth()), new TxCrd(0.5, 0.5)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 0), new TxCrd(0.2, 0.4)),
				new Pin3D(new Point3D(texture.getHeight(), 0, 0), new TxCrd(0.5, 0.1)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth()), new TxCrd(1, 1)),
				new Pin3D(new Point3D(texture.getHeight(), 0, texture.getWidth()), new TxCrd(1, 1)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth() + 1), new TxCrd(1, 1)),
				new Pin3D(new Point3D(texture.getHeight(), 0, texture.getWidth() + 1), new TxCrd(0.5, 0)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 2 * texture.getWidth()), new TxCrd(0, 0)),
				new Pin3D(new Point3D(texture.getHeight(), 0, 2 * texture.getWidth()), new TxCrd(1, 1)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, texture.getHeight(), 0), new TxCrd(1, 1)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 0), new TxCrd(0, 1)),
				new Pin3D(new Point3D(0, texture.getHeight(), texture.getWidth()), new TxCrd(0, 0)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth()), new TxCrd(0, 0)), texture));
		house.add(new Tetragon3D(new Pin3D(new Point3D(0, texture.getHeight(), texture.getWidth() + 1), new TxCrd(1, 0)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth() + 1), new TxCrd(0, 1)),
				new Pin3D(new Point3D(0, texture.getHeight(), 2 * texture.getWidth()), new TxCrd(0, 0)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 2 * texture.getWidth()), new TxCrd(1, 0)), texture));
		house.add(
				new Tetragon3D(
						new Pin3D(
								new Point3D(
										0, texture
												.getHeight(),
										0),
								new TxCrd(0.8, 0.6)),
						new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), 0), new TxCrd(0.6, 0.8)),
						new Pin3D(new Point3D(0, texture.getHeight(), texture.getWidth()), new TxCrd(0.2, 0.4)),
						new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), texture.getWidth()),
								new TxCrd(0.4, 0.2)),
						texture));
		house.add(
				new Tetragon3D(
						new Pin3D(
								new Point3D(0, texture.getHeight(),
										texture.getWidth()
												+ 1),
								new TxCrd(1, 1.5)),
						new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), texture.getWidth() + 1),
								new TxCrd(5.3, 3.2)),
				new Pin3D(new Point3D(0, texture.getHeight(), 2 * texture.getWidth()), new TxCrd(1.8, 8.1)),
				new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), 2 * texture.getWidth()),
						new TxCrd(0.1, 1.2)), texture));
		house.add(
				new Tetragon3D(new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), 0), new TxCrd(0.9, 0.4)),
						new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 0), new TxCrd(0.2, 0.5)),
						new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), texture.getWidth()),
								new TxCrd(0.7, 0.3)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth()), new TxCrd(0.6, 0.1)), texture));
		house.add(
				new Tetragon3D(
						new Pin3D(
								new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2),
										texture.getWidth()
												+ 1),
								new TxCrd(1, 1)),
						new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), texture.getWidth() + 1), new TxCrd(0, 0)),
						new Pin3D(new Point3D((2 * texture.getHeight() / 3), (3 * texture.getHeight() / 2), 2 * texture.getWidth()),
								new TxCrd(1, 0)),
				new Pin3D(new Point3D(texture.getHeight(), texture.getHeight(), 2 * texture.getWidth()), new TxCrd(1, 1)), texture));

		return house;

	}

	private Component3D makeTriangles(ITexture texture) {

		Component3D triangles = new Component3D();

		triangles.add(new Triangle3D(new Pin3D(new Point3D(800, 100, texture.getWidth()), new TxCrd(1, 1)),
				new Pin3D(new Point3D(800 + texture.getHeight(), 100, texture.getWidth()), new TxCrd(1, 0)),
				new Pin3D(new Point3D(800 + texture.getHeight() / 2, 200, texture.getWidth() / 2), new TxCrd(0.5, 0.5)), texture));
		triangles.add(new Triangle3D(new Pin3D(new Point3D(800, 100, 0), new TxCrd(0, 1)),
				new Pin3D(new Point3D(800, 100, texture.getWidth()), new TxCrd(1, 1)),
				new Pin3D(new Point3D(800 + texture.getHeight() / 2, 200, texture.getWidth() / 2), new TxCrd(0.5, 0.5)), texture));
		triangles.add(new Triangle3D(new Pin3D(new Point3D(800, 100, 0), new TxCrd(0, 1)),
				new Pin3D(new Point3D(800 + texture.getHeight(), 100, 0), new TxCrd(0, 0)),
				new Pin3D(new Point3D(800 + texture.getHeight() / 2, 200, texture.getWidth() / 2), new TxCrd(0.5, 0.5)), texture));
		triangles.add(new Triangle3D(new Pin3D(new Point3D(800 + texture.getHeight(), 100, 0), new TxCrd(0, 0)),
				new Pin3D(new Point3D(800 + texture.getHeight(), 100, texture.getWidth()), new TxCrd(1, 0)),
				new Pin3D(new Point3D(800 + texture.getHeight() / 2, 200, texture.getWidth() / 2), new TxCrd(0.5, 0.5)), texture));

		return triangles;
	}

	private void initTimer() {

		timer = new Timer(1, this);
		timer.start();

	}

	private void animate() {

		point = point.add(new Point3D(0, 0, 20));
		view = new View3D(view.getNullpoint(),
				new Projector2D(view.getProjector().getViewpoint().add(new Point3D(-10, -10, 0)), view.getProjector().getHeight()));

		repaint();
	}

	@Override
	protected void paintComponent(Graphics graphics) {

		super.paintComponent(graphics);

		// drawTexture(graphics);
		paintCross(graphics);
		renderTextured(graphics);
	}

	private void paintCross(Graphics graphics) {

		Point3D p0 = new Point3D(0, 0, 0);
		Line3D lineX = new Line3D(p0, new Point3D(koordination.WIDTH / 2, 0, 0));
		Line3D lineY = new Line3D(p0, new Point3D(0, koordination.HEIGHT / 2, 0));
		Line3D lineZ = new Line3D(p0, new Point3D(0, 0, 2 * koordination.WIDTH));

		graphics.setColor(Color.orange);

		lineX.draw(p0, graphics, view);
		lineY.draw(p0, graphics, view);
		lineZ.draw(p0, graphics, view);
		//
		// for (int x = 1; x < 10; x++) {
		// Point p = project2Plane(new Point3D(x, 0, 0));
		// graphics.drawLine(Koordination.translateX(p.x),
		// Koordination.translateY(p.y - 10),
		// Koordination.translateX(p.x),
		// Koordination.translateY(p.y + 10));
		// }
		//
		// for (int y = 1; y < 10; y++) {
		// Point p = project2Plane(new Point3D(0, y, 0));
		// graphics.drawLine(Koordination.translateX(p.x),
		// Koordination.translateY(p.y - 10),
		// Koordination.translateX(p.x),
		// Koordination.translateY(p.y + 10));
		// }
		//
		// for (int z = 1; z < 10; z++) {
		// Point p = project2Plane(new Point3D(0, 0, z));
		// graphics.drawLine(Koordination.translateX(p.x),
		// Koordination.translateY(p.y - 10),
		// Koordination.translateX(p.x),
		// Koordination.translateY(p.y + 10));
		// }

	}

	private void renderTextured(Graphics graphics) {
		component.draw(point, graphics, view);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
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
		animate();
	}

}
