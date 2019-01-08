/**
 * 
 */
package de.zintel.math.transform;

import java.util.Arrays;

import de.zintel.math.AMatrix;
import de.zintel.math.matrix.NumberStringMatrix;

/**
 * @author friedemann.zintel
 *
 */
public class StringRotationMatrix3DGenerator {

	private static NumberStringMatrix rotX = new NumberStringMatrix(Arrays.asList(Arrays.asList("1", "0", "0"),
			Arrays.asList("0", "cos(ax)", "-sin(ax)"), Arrays.asList("0", "sin(ax)", "cos(ax)")), AMatrix.Order.ROWS);
	private static NumberStringMatrix rotY = new NumberStringMatrix(Arrays.asList(Arrays.asList("cos(ay)", "0", "-sin(ay)"),
			Arrays.asList("0", "1", "0"), Arrays.asList("sin(ay)", "0", "cos(ay)")), AMatrix.Order.ROWS);
	private static NumberStringMatrix rotZ = new NumberStringMatrix(Arrays.asList(Arrays.asList("cos(az)", "-sin(az)", "0"),
			Arrays.asList("sin(az)", "cos(az)", "0"), Arrays.asList("0", "0", "1")), AMatrix.Order.ROWS);

	/**
	 * 
	 */
	public StringRotationMatrix3DGenerator() {
	}

	public String generate() {
		return AMatrix.mmult(AMatrix.mmult(rotX, rotY), rotZ).toString();
	}

}
