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

	public static final float DEADZONE_STICK_DFLT = 0.25F;

	public static final float DEADZONE_LRT_DFLT = 0F;

	private final int playerNmb;

	private XInputDeviceListener xInputDeviceListener = null;

	private IXInputAnalogHandler xInputAnalogHandler = null;

	private float deadzoneStick = DEADZONE_STICK_DFLT;

	private float deadzoneLRT = DEADZONE_LRT_DFLT;

	public XInputBundle(int playerNmb) {
		this.playerNmb = playerNmb;
	}

	public XInputDeviceListener getXInputDeviceListener() {
		return xInputDeviceListener;
	}

	public XInputBundle setXInputDeviceListener(XInputDeviceListener xInputDeviceListener) {
		this.xInputDeviceListener = xInputDeviceListener;
		return this;
	}

	public IXInputAnalogHandler getXInputAnalogHandler() {
		return xInputAnalogHandler;
	}

	public XInputBundle setXInputAnalogHandler(IXInputAnalogHandler xInputAnalogHandler) {
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
