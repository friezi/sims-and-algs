/**
 * 
 */
package de.zintel.gfx.gl;

import java.awt.Color;
import java.awt.Dimension;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import de.zintel.gfx.color.CUtils.ColorGenerator;

/**
 * @author Friedemann
 *
 */
public final class GLUtils {

	public static class CircleDrawer {

		private static final int DFLT_STEPS = 32;
		private static final float MAX_ANGLE = (float) (2 * Math.PI);

		private final int steps;
		private final int nmbValues;

		private final double[] sine;
		private final double[] cosine;

		public CircleDrawer() {
			this(DFLT_STEPS);
		}

		public CircleDrawer(int steps) {

			this.steps = steps;

			final float stepAngleRadian = (float) (2 * Math.PI / steps);
			this.nmbValues = (int) Math.ceil((2 * Math.PI / stepAngleRadian)) + 1;

			sine = new double[nmbValues];
			cosine = new double[nmbValues];

			float currentAngleRadian;
			for (int i = 0; i < nmbValues; i++) {

				currentAngleRadian = i * stepAngleRadian;
				if (currentAngleRadian > MAX_ANGLE) {
					currentAngleRadian = MAX_ANGLE;
				}

				sine[i] = Math.sin(currentAngleRadian);
				cosine[i] = Math.cos(currentAngleRadian);

				if (currentAngleRadian >= MAX_ANGLE) {
					break;
				}
			}
		}

		/**
		 * Circle with fixed position and size.
		 * 
		 * @param x
		 * @param y
		 * @param radius
		 * @param color
		 * @param gl
		 */
		public void drawFilledCircle(int x, int y, int radius, final Color color, final Dimension dimension, final GL2 gl) {
			drawFilledEllipse(x, y, radius, () -> color, dimension, 1, 0, gl);
		}

		/**
		 * Circle with fixed position and size.
		 * 
		 * @param x
		 * @param y
		 * @param radius
		 * @param color
		 * @param gl
		 */
		public void drawFilledEllipse(int x, int y, int radius, final ColorGenerator colorGenerator, final Dimension dimension,
				double ratioYX, float angle, final GL2 gl) {

			float x1, y1, x2, y2;
			float rx = projectX(radius, dimension) + 1;
			float ry = -projectY(radius, dimension) + 1;

			x1 = projectX(x, dimension);
			y1 = projectY(y, dimension);

			final Color color = colorGenerator.generateColor();

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glColor4f(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()),
					projectColorValue2GL(color.getBlue()), projectColorValue2GL(color.getAlpha()));
			gl.glVertex2d(x1, y1);
			int angleIndex = (int) Math.round((steps / (2 * Math.PI / angle)));

			if (ratioYX < 1) {

				ratioYX = 1 / ratioYX;
				angleIndex += steps / 4;

			}
			// adjust to positive
			angleIndex = angleIndex < 0 ? steps + angleIndex : angleIndex;
			// adjust to 0 <= x < 180
			angleIndex = angleIndex >= steps / 2 ? angleIndex - steps / 2 : angleIndex;
			boolean adjustCosine = angleIndex > steps / 8 && angleIndex < steps / 4 + steps / 8;
			boolean adjustSine = !adjustCosine;
			int dIdxSine = !adjustSine ? 0 : (angleIndex < steps / 4 ? angleIndex : steps / 2 + angleIndex);
			int dIdxCosine = !adjustCosine ? 0
					: (angleIndex > steps / 8 && angleIndex < steps / 4 ? steps - angleIndex - steps / 4 : angleIndex - steps / 4);
			for (int i = 0; i < sine.length; i++) {

				x2 = (float) (x1 + sine[(i + dIdxSine) % nmbValues] * rx / (adjustSine ? ratioYX : 1));
				y2 = (float) (y1 + cosine[(i + dIdxCosine) % nmbValues] * ry / (adjustCosine ? ratioYX : 1));

				final Color nextColor = colorGenerator.generateColor();
				gl.glColor4f(projectColorValue2GL(nextColor.getRed()), projectColorValue2GL(nextColor.getGreen()),
						projectColorValue2GL(nextColor.getBlue()), projectColorValue2GL(nextColor.getAlpha()));

				gl.glVertex2f(x2, y2);
			}

			gl.glEnd();
		}

	}

	/**
	 * 
	 */
	private GLUtils() {
	}

	public static void drawLine(final int x1, final int y1, final int x2, final int y2, final Color color, final Dimension dimension,
			final GL2 gl) {
		drawLine(projectX(x1, dimension), projectY(y1, dimension), projectX(x2, dimension), projectY(y2, dimension), color, gl);
	}

	public static void drawLine(float x1, float y1, float x2, float y2, Color color, final GL2 gl) {

		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()), projectColorValue2GL(color.getBlue()),
				projectColorValue2GL(color.getAlpha()));
		gl.glVertex2d(x1, y1);
		gl.glVertex2d(x2, y2);
		gl.glEnd();

	}

	public static float projectColorValue2GL(final int value) {
		return value / 255f;
	}

	public static float projectY(int y, final Dimension dimension) {
		return -projectHomogenous(y, dimension.height);
	}

	public static float projectX(int x, final Dimension dimension) {
		return projectHomogenous(x, dimension.width);
	}

	public static float projectHomogenous(int value, int max) {
		return 2 * normalize(value, max) - 1;
	}

	public static float normalize(int value, int max) {
		return (float) value / max;
	}

}
