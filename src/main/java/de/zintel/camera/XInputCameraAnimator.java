/**
 * 
 */
package de.zintel.camera;

import com.github.strikerx3.jxinput.enums.XInputButton;

import de.zintel.animation.IAnimator;
import de.zintel.math.Axis3D;
import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.xinput.IXInputCombinedHandler;

/**
 * @author friedo
 *
 */
public class XInputCameraAnimator implements IXInputCombinedHandler, IAnimator {

	private final Vector3D center = new Vector3D(0.0, 540.0, 200.0);

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

	private boolean doCenter = false;

	public XInputCameraAnimator(ICamera3D camera, double maxspeed) {
		this.camera = camera;
		this.maxspeed = maxspeed;
	}

	public void step() {

		if (doReset) {

			camera.reset();
			doReset = false;

		}

		if (doCenter) {

			try {

				double angle = 1;
				// because asin only works on [-pi/2,pi/2] we need to iterate
				// the rotation, until the center has been reached (angle > 90
				// degrees will result in smaller angle changes)
				while (MathUtils.inEpsilonRange(angle)) {

					final Vector3D pcenter = camera.getTransformationToCamera().transformPoint(center);

					// rotation to center
					final Vector3D viewpoint = camera.getViewpoint();
					final Vector3D distancevector = Vector3D.substract(pcenter, viewpoint);
					final double dlen = distancevector.length();
					if (dlen == 0) {
						// camera has reached center, no adjustment possible
						return;
					}

					final Vector3D snorm = new Vector3D(0, 0, 1);
					final Vector3D dnormOppDirection = Vector3D.normalize(distancevector).negate();
					final Vector3D rotnorm = Vector3D.crossProduct(distancevector, snorm);

					if (Vector3D.substract(snorm, dnormOppDirection).isNullVector()) {
						// camera points in opposite direction of center, would
						// result in 0 degrees, thus set it to 180 degrees
						angle = Math.PI;
					} else {
						angle = Math.asin(rotnorm.length() / (Math.abs(dlen) * Math.abs(snorm.length())));
					}

					if (MathUtils.inEpsilonRange(angle)) {
						camera.rotate(new Axis3D(viewpoint, Vector3D.add(viewpoint, rotnorm)), -angle);
					}
				}

			} finally {
				doCenter = false;
			}
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
		} else if (button == XInputButton.B && pressed) {
			doCenter = true;
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
