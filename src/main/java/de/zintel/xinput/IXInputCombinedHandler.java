/**
 * 
 */
package de.zintel.xinput;

import com.github.strikerx3.jxinput.listener.XInputDeviceListener;

/**
 * @author friedemann.zintel
 *
 */
public interface IXInputCombinedHandler extends XInputDeviceListener {

	void handleXInputLeftStick(float x, float y);

	void handleXInputRightStick(float x, float y);

	void handleXInputLT(float value);

	void handleXInputRT(float value);

}
