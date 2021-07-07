/**
 * 
 */
package de.zintel.gfx.g3d.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.util.SortedSet;
import java.util.TreeSet;

import de.zintel.camera.ICamera3D;
import de.zintel.camera.PlaneCamera3D;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.utils.Pair;

/**
 * @author friedemann.zintel
 *
 */
public class PlaneCamera3DRenderer implements IRenderer {

	private final PlaneCamera3D renderedCamera;

	private final Color color;

	public PlaneCamera3DRenderer(PlaneCamera3D renderedCamera, Color color) {
		this.renderedCamera = renderedCamera;
		this.color = color;
	}

	@Override
	public void render(IGraphicsSubsystem graphicsSubsystem, ICamera3D<?> camera) {

		if (camera == renderedCamera) {
			return;
		}

		final CoordinateTransformation3D ctf = camera.getTransformationToCamera();
		final CoordinateTransformation3D rtf = renderedCamera.getTransformationToCamera();

		final CoordinateTransformation3D cattf = ctf.cat(rtf.inverse());
		final Dimension rsd = renderedCamera.getScreenDimension();

		final Pair<Vector3D, Color> bl = new Pair<>(cattf.transformPoint(new Vector3D()), color);
		final Pair<Vector3D, Color> tl = new Pair<>(cattf.transformPoint(new Vector3D(0, rsd.getHeight() - 1, 0)), color);
		final Pair<Vector3D, Color> br = new Pair<>(cattf.transformPoint(new Vector3D(rsd.getWidth() - 1, 0, 0)), color);
		final Pair<Vector3D, Color> tr = new Pair<>(cattf.transformPoint(new Vector3D(rsd.getWidth() - 1, rsd.getHeight(), 0)), color);
		final Pair<Vector3D, Color> vp = new Pair<>(cattf.transformPoint(renderedCamera.getViewpoint()), color);

		// sort points according to z-value
		final SortedSet<Pair<Vector3D, Color>> coll = new TreeSet<>((o1, o2) -> o1.getFirst().z() >= o2.getFirst().z() ? 1 : -1);
		coll.add(bl);
		coll.add(tl);
		coll.add(br);
		coll.add(tr);
		coll.add(vp);

		// front-points should be brighter, back-points darker.
		Color currColor = color;
		for (Pair<Vector3D, Color> el : coll) {
			el.setSecond(currColor);
			currColor = currColor.darker();
		}

		drawLine(graphicsSubsystem, bl, tl, camera);
		drawLine(graphicsSubsystem, bl, br, camera);
		drawLine(graphicsSubsystem, tr, tl, camera);
		drawLine(graphicsSubsystem, tr, br, camera);

		drawLine(graphicsSubsystem, bl, vp, camera);
		drawLine(graphicsSubsystem, tl, vp, camera);
		drawLine(graphicsSubsystem, br, vp, camera);
		drawLine(graphicsSubsystem, tr, vp, camera);

	}

	private void drawLine(final IGraphicsSubsystem graphicsSubsystem, final Pair<Vector3D, Color> p1, final Pair<Vector3D, Color> p2,
			ICamera3D<?> camera) {

		final Pair<Vector3D, Vector3D> line = camera.cameraLineToScreen(p1.getFirst(), p2.getFirst());
		if (line != null) {
			graphicsSubsystem.drawLine((int) line.getFirst().x(), (int) line.getFirst().y(), (int) line.getSecond().x(),
					(int) line.getSecond().y(), p1.getSecond(), p2.getSecond());
		}
	}

}
