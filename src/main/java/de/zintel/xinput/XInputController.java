/**
 * 
 */
package de.zintel.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;

import de.zintel.math.MathUtils;

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

					combinedHandler.handleXInputLeftStick(adjustToDeadzone(axes.lx, xInputHandle.getLeftStickDeadzone()),
							adjustToDeadzone(axes.ly, xInputHandle.getLeftStickDeadzone()));
					combinedHandler.handleXInputRightStick(adjustToDeadzone(axes.rx, xInputHandle.getRightStickDeadzone()),
							adjustToDeadzone(axes.ry, xInputHandle.getRightStickDeadzone()));
					combinedHandler.handleXInputLT(adjustToDeadzone(axes.lt, xInputHandle.getLTDeadzone()));
					combinedHandler.handleXInputRT(adjustToDeadzone(axes.rt, xInputHandle.getRTDeadzone()));
				}
			}
		}
	}

	private float adjustToDeadzone(float value, final float deadzone) {
		final float sgn = Math.signum(value);
		return Math.abs(value) < deadzone ? 0 : (float) MathUtils.scalel(sgn * deadzone, sgn, 0, sgn, value);
	}

}
