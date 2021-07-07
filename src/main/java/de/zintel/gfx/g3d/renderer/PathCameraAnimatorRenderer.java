package de.zintel.gfx.g3d.renderer;

import java.awt.Color;

import de.zintel.animator.PathCameraAnimator;
import de.zintel.camera.ICamera3D;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.utils.Pair;
import de.zintel.utils.StepUnit;

public class PathCameraAnimatorRenderer implements IRenderer {

	private final PathCameraAnimator animator;

	public PathCameraAnimatorRenderer(PathCameraAnimator animator) {
		this.animator = animator;
	}

	@Override
	public void render(IGraphicsSubsystem graphicsSubsystem, ICamera3D<?> camera) {

		// fixpoint for centering
		final Vector3D pcenter = camera.projectWorld(animator.getCenter());
		if (camera.inScreenRange(pcenter)) {
			graphicsSubsystem.drawFilledCircle((int) pcenter.x(), (int) pcenter.y(), 5, () -> Color.GREEN);
		}

		// rotation axis
		final Axis3D axis = animator.getLastAxis();
		if (axis != null) {

			final Vector3D v = Vector3D.normalize(Vector3D.substract(axis.getP2(), axis.getP1())).mult(250);
			final Vector3D p1 = Vector3D.substract(axis.getP1(), v);
			final Vector3D p2 = Vector3D.add(axis.getP1(), v);

			final ICamera3D<?> acamera = animator.getCamera();
			drawLine(graphicsSubsystem, new Pair<>(camera.projectWorld(acamera.toWorld(p1)), Color.YELLOW),
					new Pair<>(camera.projectWorld(acamera.toWorld(p2)), Color.YELLOW));
		}

		// path
		final StepUnit<Vector3D> unit = animator.getCurrentUnit();
		final Color mincolor = new Color(0, 0, 40, 5);
		final Color maxcolor = new Color(10, 10, 255, 100);
		final int rng = 500;
		for (StepUnit<Vector3D> currUnit : animator.getPathpoints()) {

			final Color color = unit == null ? mincolor
					: currUnit.getStep() >= unit.getStep() - rng && currUnit.getStep() <= unit.getStep() + rng ? CUtils.morphColor(mincolor,
							maxcolor, x -> Math.sin(MathUtils.scalel(unit.getStep() - rng, unit.getStep() + rng, 0, Math.PI, x)),
							currUnit.getStep()) : mincolor;
			final Vector3D bpointWorld = currUnit.getElement();
			final Vector3D bpoint = camera.projectWorld(bpointWorld);
			if (bpoint == null) {
				continue;
			}
			graphicsSubsystem.drawFilledCircle((int) bpoint.x(), (int) bpoint.y(), 4, () -> color);
		}

	}

	private void drawLine(final IGraphicsSubsystem graphicsSubsystem, final Pair<Vector3D, Color> p1, final Pair<Vector3D, Color> p2) {

		if (p1.getFirst() != null & p2.getFirst() != null) {
			graphicsSubsystem.drawLine((int) p1.getFirst().x(), (int) p1.getFirst().y(), (int) p2.getFirst().x(), (int) p2.getFirst().y(),
					p1.getSecond(), p2.getSecond());
		}

	}

}