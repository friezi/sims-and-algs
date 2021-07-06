/**
 * 
 */
package de.zintel.xinput;

/**
 * @author friedemann.zintel
 *
 */
public class XInputHandle {

	public static final float DEADZONE_STICK_DFLT = 0.25F;

	public static final float DEADZONE_LRT_DFLT = 0F;

	private final int playerNmb;

	private IXInputCombinedHandler xInputCombinedHandler = null;

	private float leftStickDeadzone = DEADZONE_STICK_DFLT;

	private float rightStickDeadzone = DEADZONE_STICK_DFLT;

	private float rtDeadzone = DEADZONE_LRT_DFLT;

	private float ltDeadzone = DEADZONE_LRT_DFLT;

	public XInputHandle(int playerNmb) {
		this.playerNmb = playerNmb;
	}

	public IXInputCombinedHandler getXInputCombinedHandler() {
		return xInputCombinedHandler;
	}

	public XInputHandle setXInputCombinedHandler(IXInputCombinedHandler xInputCombinedHandler) {
		this.xInputCombinedHandler = xInputCombinedHandler;
		return this;
	}

	public int getPlayerNmb() {
		return playerNmb;
	}

	public float getLeftStickDeadzone() {
		return leftStickDeadzone;
	}

	public void setLeftStickDeadzone(float deadzoneStick) {
		this.leftStickDeadzone = deadzoneStick;
	}

	public float getRightStickDeadzone() {
		return rightStickDeadzone;
	}

	public void setRightStickDeadzone(float rightStickDeadzone) {
		this.rightStickDeadzone = rightStickDeadzone;
	}

	public float getRTDeadzone() {
		return rtDeadzone;
	}

	public void setRTDeadzone(float deadzoneLRT) {
		this.rtDeadzone = deadzoneLRT;
	}

	public float getLTDeadzone() {
		return ltDeadzone;
	}

	public void setLTDeadzone(float ltDeadzone) {
		this.ltDeadzone = ltDeadzone;
	}

}
