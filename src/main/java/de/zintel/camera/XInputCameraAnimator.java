/**
 * 
 */
package de.zintel.camera;

import com.github.strikerx3.jxinput.enums.XInputButton;

import de.zintel.animation.IAnimator;
import de.zintel.math.Axis3D;
import de.zintel.math.Vector3D;
import de.zintel.xinput.IXInputCombinedHandler;

/**
 * @author friedo
 *
 */
public class XInputCameraAnimator implements IXInputCombinedHandler, IAnimator {

	private static final double CURVATURE_MAX = 20;

	private final ICamera3D camera;

	private final double maxspeed;

	private double hrotationspeed = 0;

	private double vrotationspeed = 0;

	private double zrotationspeed = 0;

	private double sidespeed = 0;

	private double frontspeed = 0;

	private double upspeed = 0;

	private boolean buttonRB = false;

	private boolean buttonLB = false;

	private boolean doReset = false;

	public XInputCameraAnimator(ICamera3D camera, double maxspeed) {
		this.camera = camera;
		this.maxspeed = maxspeed;
	}

	public void step() {

		if (doReset) {

			camera.reset();
			doReset = false;

		}

		if (hrotationspeed != 0) {

			final double angle = speedAngle(hrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x(), viewpoint.y() - 1, viewpoint.z()));
			camera.rotate(axis, angle);

		}

		if (vrotationspeed != 0) {

			final double angle = speedAngle(vrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x() + 1, viewpoint.y(), viewpoint.z()));
			camera.rotate(axis, angle);
		}

		if (zrotationspeed != 0) {

			final double angle = speedAngle(zrotationspeed);
			final Vector3D viewpoint = camera.getViewpoint();
			final Axis3D axis = new Axis3D(viewpoint, new Vector3D(viewpoint.x(), viewpoint.y(), viewpoint.z() + 1));
			camera.rotate(axis, angle);
		}

		if (frontspeed != 0) {
			final Vector3D translationVector = Vector3D.mult(frontspeed, new Vector3D(0, 0, 1));
			camera.translate(translationVector);
		}

		if (sidespeed != 0) {
			// s. a.
			final Vector3D tv = Vector3D.mult(sidespeed, new Vector3D(1, 0, 0));
			camera.translate(tv);
		}

		if (upspeed != 0) {
			// s. a.
			final Vector3D tv = Vector3D.mult(upspeed, new Vector3D(0, -1, 0));
			camera.translate(tv);
		}

	}

	private double speedAngle(final double speed) {
		return speed * 2 * Math.PI / (60 * 60);
	}

	@Override
	public void handleXInputLeftStick(float x, float y) {

		if (buttonLB) {

			sidespeed = 0;
			frontspeed = 0;
			upspeed = 4 * maxspeed * y;

		} else {

			sidespeed = 4 * maxspeed * x;
			frontspeed = 4 * maxspeed * y;
			upspeed = 0;

		}

	}

	@Override
	public void handleXInputRightStick(float x, float y) {

		if (buttonRB) {
			hrotationspeed = 0;
			vrotationspeed = 0;
			zrotationspeed = maxspeed * x;
		} else {

			hrotationspeed = -maxspeed * x;
			vrotationspeed = maxspeed * y;
			zrotationspeed = 0;

		}
	}

	@Override
	public void handleXInputRT(float value) {
		camera.setCurvature(Math.max(camera.getCurvature(), CURVATURE_MAX * value));
	}

	@Override
	public void handleXInputLT(float value) {
		camera.setCurvature(Math.min(camera.getCurvature(), CURVATURE_MAX * (1 - value)));
	}

	@Override
	public void buttonChanged(XInputButton button, boolean pressed) {

		if (button == XInputButton.X && pressed) {
			doReset = true;
		} else if (button == XInputButton.RIGHT_SHOULDER) {
			buttonRB = pressed;
		} else if (button == XInputButton.LEFT_SHOULDER) {
			buttonLB = pressed;
		}

	}

	@Override
	public void connected() {
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void reinit() {
		doReset = true;
	}

	@Override
	public boolean finished() {
		return false;
	}
}
