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

	public static final float DEADZONE_STICK_DFLT = 0.21F;

	public static final float DEADZONE_LRT_DFLT = 0F;

	private final int playerNmb;

	private XInputDeviceListener xInputDeviceListener = null;

	private IXInputAnalogHandler xInputAnalogHandler = null;

	private float deadzoneStick = DEADZONE_STICK_DFLT;

	private float deadzoneLRT = DEADZONE_LRT_DFLT;

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

	public float getDeadzoneStick() {
		return deadzoneStick;
	}

	public void setDeadzoneStick(float deadzoneStick) {
		this.deadzoneStick = deadzoneStick;
	}

	public float getDeadzoneLRT() {
		return deadzoneLRT;
	}

	public void setDeadzoneLRT(float deadzoneLRT) {
		this.deadzoneLRT = deadzoneLRT;
	}

}
