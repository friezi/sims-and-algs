/**
 * 
 */
package de.zintel.xinput;

import com.github.strikerx3.jxinput.listener.XInputDeviceListener;

/**
 * @author friedemann.zintel
 *
 */
public class XInputBundle {

	public static final float DEADZONE__DFLT = 0.23F;

	private final int playerNmb;

	private XInputDeviceListener xInputDeviceListener = null;

	private IXInputAnalogHandler xInputAnalogHandler = null;

	private float deadzone = DEADZONE__DFLT;

	public XInputBundle(int playerNmb) {
		this.playerNmb = playerNmb;
	}

	public XInputDeviceListener getxInputDeviceListener() {
		return xInputDeviceListener;
	}

	public XInputBundle setxInputDeviceListener(XInputDeviceListener xInputDeviceListener) {
		this.xInputDeviceListener = xInputDeviceListener;
		return this;
	}

	public IXInputAnalogHandler getxInputAnalogHandler() {
		return xInputAnalogHandler;
	}

	public XInputBundle setxInputAnalogHandler(IXInputAnalogHandler xInputAnalogHandler) {
		this.xInputAnalogHandler = xInputAnalogHandler;
		return this;
	}

	public int getPlayerNmb() {
		return playerNmb;
	}

	public float getDeadzone() {
		return deadzone;
	}

	public void setDeadzone(float deadzone) {
		this.deadzone = deadzone;
	}

}
