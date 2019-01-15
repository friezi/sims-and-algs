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

				xInputBundle.getXInputAnalogHandler().handleXInputLeftStick(adjustToDeadZone(axes.lx, xInputBundle.getDeadzoneStick()),
						adjustToDeadZone(axes.ly, xInputBundle.getDeadzoneStick()));
				xInputBundle.getXInputAnalogHandler().handleXInputRightStick(adjustToDeadZone(axes.rx, xInputBundle.getDeadzoneStick()),
						adjustToDeadZone(axes.ry, xInputBundle.getDeadzoneStick()));
				xInputBundle.getXInputAnalogHandler().handleXInputLT(adjustToDeadZone(axes.lt, xInputBundle.getDeadzoneLRT()));
				xInputBundle.getXInputAnalogHandler().handleXInputRT(adjustToDeadZone(axes.rt, xInputBundle.getDeadzoneLRT()));

			}
		}

	}

	private float adjustToDeadZone(float value, final float deadzone) {
		return Math.abs(value) < deadzone ? 0 : (value - Math.signum(value) * deadzone) / (1 - deadzone);
	}

}
