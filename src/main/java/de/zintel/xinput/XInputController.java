/**
 * 
 */
package de.zintel.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;

/**
 * @author friedemann.zintel
 *
 */
public class XInputController {

	private final XInputDevice xInputDevice;

	private final XInputBundle xInputBundle;

	public XInputController(XInputDevice xInputDevice, XInputBundle xInputBundle) {
		this.xInputDevice = xInputDevice;
		this.xInputBundle = xInputBundle;
	}

	public XInputDevice getXInputDevice() {
		return xInputDevice;
	}

	public XInputBundle getXInputBundle() {
		return xInputBundle;
	}

	public void handleXInput() {

		if (xInputDevice != null) {

			if (xInputDevice.poll()) {

				XInputComponents components = xInputDevice.getComponents();
				final XInputAxes axes = components.getAxes();

				xInputBundle.getxInputAnalogHandler().handleXInputLeftStick(adjustToDeadZone(axes.lx), adjustToDeadZone(axes.ly));
				xInputBundle.getxInputAnalogHandler().handleXInputRightStick(adjustToDeadZone(axes.rx), adjustToDeadZone(axes.ry));
				xInputBundle.getxInputAnalogHandler().handleXInputLT(adjustToDeadZone(axes.lt));
				xInputBundle.getxInputAnalogHandler().handleXInputRT(adjustToDeadZone(axes.rt));

			}
		}

	}

	private float adjustToDeadZone(float value) {
		return Math.abs(value) < xInputBundle.getDeadzone() ? 0 : value - Math.signum(value) * xInputBundle.getDeadzone();
	}

}
