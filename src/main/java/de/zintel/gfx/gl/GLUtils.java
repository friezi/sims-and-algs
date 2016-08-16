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

		private static final float DFLT_STEP = (float) (2 * Math.PI / 32);
		private static final float MAX_ANGLE = (float) (2 * Math.PI);

		private final double[] sine;
		private final double[] cosine;

		public CircleDrawer() {
			this(DFLT_STEP);
		}

		public CircleDrawer(float step) {

			int nmb = (int) Math.ceil((2 * Math.PI / step)) + 1;

			sine = new double[nmb];
			cosine = new double[nmb];

			int i = 0;
			float angle;
			while (true) {

				angle = i * step;
				if (angle > MAX_ANGLE) {
					angle = MAX_ANGLE;
				}

				sine[i] = Math.sin(angle);
				cosine[i] = Math.cos(angle);

				if (angle >= MAX_ANGLE) {
					break;
				}

				i++;
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
			drawFilledCircle(x, y, radius, () -> color, dimension, gl);
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
		public void drawFilledCircle(int x, int y, int radius, final ColorGenerator colorGenerator, final Dimension dimension,
				final GL2 gl) {

			float x1, y1, x2, y2;
			float rx = (projectX(radius, dimension) + 1);
			float ry = (-projectY(radius, dimension) + 1);

			x1 = projectX(x, dimension);
			y1 = projectY(y, dimension);

			final Color color = colorGenerator.generateColor();

			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glColor4f(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()),
					projectColorValue2GL(color.getBlue()), projectColorValue2GL(color.getAlpha()));
			gl.glVertex2d(x1, y1);

			for (int i = 0; i < sine.length; i++) {

				x2 = (float) (x1 + sine[i] * rx);
				y2 = (float) (y1 + cosine[i] * ry);

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
