/**
 * 
 */
package de.zintel.xinput;

/**
 * @author friedemann.zintel
 *
 */
public interface IXInputAnalogHandler {

	void handleXInputLeftStick(float x, float y);

	void handleXInputRightStick(float x, float y);

	void handleXInputLT(float value);

	void handleXInputRT(float value);

}
