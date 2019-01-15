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

	private final XInputHandle xInputHandle;

	public XInputController(XInputDevice xInputDevice, XInputHandle xInputHandle) {
		this.xInputDevice = xInputDevice;
		this.xInputHandle = xInputHandle;
	}

	public XInputDevice getXInputDevice() {
		return xInputDevice;
	}

	public XInputHandle getXInputHandle() {
		return xInputHandle;
	}

	public void handleXInput() {

		if (xInputDevice != null) {
			if (xInputDevice.poll()) {

				final IXInputCombinedHandler combinedHandler = xInputHandle.getXInputCombinedHandler();

				if (combinedHandler != null) {

					XInputComponents components = xInputDevice.getComponents();
					final XInputAxes axes = components.getAxes();

					combinedHandler.handleXInputLeftStick(adjustToDeadZone(axes.lx, xInputHandle.getDeadzoneStick()),
							adjustToDeadZone(axes.ly, xInputHandle.getDeadzoneStick()));
					combinedHandler.handleXInputRightStick(adjustToDeadZone(axes.rx, xInputHandle.getDeadzoneStick()),
							adjustToDeadZone(axes.ry, xInputHandle.getDeadzoneStick()));
					combinedHandler.handleXInputLT(adjustToDeadZone(axes.lt, xInputHandle.getDeadzoneLRT()));
					combinedHandler.handleXInputRT(adjustToDeadZone(axes.rt, xInputHandle.getDeadzoneLRT()));
				}
			}
		}
	}

	private float adjustToDeadZone(float value, final float deadzone) {
		return Math.abs(value) < deadzone ? 0 : (value - Math.signum(value) * deadzone) / (1 - deadzone);
	}

}
