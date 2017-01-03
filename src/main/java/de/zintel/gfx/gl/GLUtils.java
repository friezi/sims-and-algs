/**
 * 
 */
package de.zintel.gfx.gl;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import de.zintel.gfx.color.CUtils.ColorGenerator;
import de.zintel.gfx.g2d.Vector2D;

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
				double ratioYX, double angle, final GL2 gl) {

			double x1, y1, x2, y2;
			double rx = projectX(radius, dimension) + 1;
			double ry = -projectY(radius, dimension) + 1;

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

				gl.glVertex2d(x2, y2);
			}

			gl.glEnd();
		}

	}

	/**
	 * 
	 */
	private GLUtils() {
	}

	public static void drawFilledTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color, final Dimension dimension,
			final GL2 gl) {
		drawFilledTriangle(projectX(x1, dimension), projectY(y1, dimension), projectX(x2, dimension), projectY(y2, dimension),
				projectX(x3, dimension), projectY(y3, dimension), color, gl);
	}

	private static void drawFilledTriangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color, final GL2 gl) {

		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glColor4f(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()), projectColorValue2GL(color.getBlue()),
				projectColorValue2GL(color.getAlpha()));
		gl.glVertex2d(x1, y1);
		gl.glVertex2d(x2, y2);
		gl.glVertex2d(x3, y3);
		gl.glEnd();

	}

	public static void drawFilledPolygon(final Collection<Vector2D> points, Color color, final Dimension dimension, final GL2 gl) {

		final Collection<Vector2D> projectedPoints = new ArrayList<>(points.size());
		for (Vector2D point : points) {
			projectedPoints.add(new Vector2D(projectX((int) point.x, dimension), projectY((int) point.y, dimension)));
		}

		drawFilledPolygon(projectedPoints, color, gl);
	}

	public static void drawFilledPolygon(final Collection<Vector2D> hPoints, Color color, final GL2 gl) {

		gl.glBegin(GL2.GL_POLYGON);
		gl.glColor4f(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()), projectColorValue2GL(color.getBlue()),
				projectColorValue2GL(color.getAlpha()));
		for (Vector2D point : hPoints) {
			gl.glVertex2d(point.x, point.y);
		}

		gl.glEnd();

	}

	public static void drawLine(final int x1, final int y1, final int x2, final int y2, final Color color, final Dimension dimension,
			final GL2 gl) {
		drawLine(projectX(x1, dimension), projectY(y1, dimension), projectX(x2, dimension), projectY(y2, dimension), color, gl);
	}

	/**
	 * zeichnet Linie auf Basis von homogenen Koordinaten.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 * @param gl
	 */
	public static void drawLine(double x1, double y1, double x2, double y2, Color color, final GL2 gl) {

		gl.glBegin(GL2.GL_LINES);
		gl.glColor4d(projectColorValue2GL(color.getRed()), projectColorValue2GL(color.getGreen()), projectColorValue2GL(color.getBlue()),
				projectColorValue2GL(color.getAlpha()));
		gl.glVertex2d(x1, y1);
		gl.glVertex2d(x2, y2);
		gl.glEnd();

	}

	public static float projectColorValue2GL(final int value) {
		return value / 255f;
	}

	public static double projectY(int y, final Dimension dimension) {
		return -projectHomogenous(y, dimension.height);
	}

	public static double projectX(int x, final Dimension dimension) {
		return projectHomogenous(x, dimension.width);
	}

	public static double projectHomogenous(int value, int max) {
		return 2 * normalize(value, max) - 1;
	}

	public static double normalize(int value, int max) {
		return (double) value / max;
	}

}
