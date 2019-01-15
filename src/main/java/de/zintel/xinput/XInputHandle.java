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

	private float deadzoneStick = DEADZONE_STICK_DFLT;

	private float deadzoneLRT = DEADZONE_LRT_DFLT;

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
