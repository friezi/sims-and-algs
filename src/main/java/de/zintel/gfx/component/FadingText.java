/**
 * 
 */
package de.zintel.gfx.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;

import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.math.Utils;

/**
 * @author Friedemann
 *
 */
public class FadingText implements IGfxComponent {

	private static final int MIN_ALPHA = 0;

	private static final int MAX_ALPHA = 255;

	private static final int MAX_ITERATIONS = 40;

	private static final int MAX_FONT_SIZE = 50;

	private static final int MIN_FONT_SIZE = 20;

	private static final int MAX_X = 200;

	private static final int MAX_Y = 400;

	private final GfxStayAliveMode mode;

	private GfxState state = GfxState.STARTUP;

	private String text;

	private Collection<String> lines;

	private Point position;

	private Color color;

	private long timeout;

	private long startTs = 0;

	private int alpha = MIN_ALPHA;

	private int fontSize = MAX_FONT_SIZE;

	private double x;

	private double y;

	private int iteration = 0;

	public FadingText(String text, Point position, Color color, long timeout) {
		this(text, position, color, timeout, GfxStayAliveMode.AUTO);
	}

	public FadingText(String text, Point position, Color color) {
		this(text, position, color, 0, GfxStayAliveMode.MANUAL);
	}

	private FadingText(String text, Point position, Color color, long timeout, GfxStayAliveMode mode) {
		this.text = text;
		this.position = position;
		this.color = color;
		this.timeout = timeout;
		this.mode = mode;
		setLines(text);
	}

	@Override
	public void draw(IGraphicsSubsystem graphicsSubsystem) {

		if (state == GfxState.STARTUP) {

			iteration++;
			if (iteration > MAX_ITERATIONS) {

				alpha = MAX_ALPHA;
				fontSize = MIN_FONT_SIZE;
				x = position.x;
				y = position.y;
				iteration = MAX_ITERATIONS;
				state = GfxState.COMPLETE;
				startTs = System.currentTimeMillis();

			} else {

				alpha = interpolate(0, MAX_ALPHA, iteration, MAX_ITERATIONS);
				fontSize = interpolate(MAX_FONT_SIZE, MIN_FONT_SIZE, iteration, MAX_ITERATIONS);
				x = interpolate(position.x + MAX_X, position.x, iteration, MAX_ITERATIONS);
				y = interpolate(position.y + MAX_Y, position.y, iteration, MAX_ITERATIONS);

			}

		} else if (state == GfxState.COMPLETE) {

			if (mode == GfxStayAliveMode.AUTO && System.currentTimeMillis() - startTs > timeout) {
				stop();
			}

		} else if (state == GfxState.STOPPING) {

			iteration--;
			if (iteration <= 0) {

				alpha = MIN_ALPHA;
				fontSize = MAX_FONT_SIZE;
				x = position.x + MAX_X;
				y = position.y + MAX_Y;
				iteration = 0;
				state = GfxState.STOPPED;

			} else {

				alpha = interpolate(MAX_ALPHA, 0, MAX_ITERATIONS - iteration, MAX_ITERATIONS);
				fontSize = interpolate(MIN_FONT_SIZE, MAX_FONT_SIZE, MAX_ITERATIONS - iteration, MAX_ITERATIONS);
				x = interpolate(position.x, position.x + MAX_X, MAX_ITERATIONS - iteration, MAX_ITERATIONS);
				y = interpolate(position.y, position.y + MAX_Y, MAX_ITERATIONS - iteration, MAX_ITERATIONS);

			}

		}

		final Color textColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
		final Font font = new Font("SansSerif", Font.PLAIN, 36);
		graphicsSubsystem.setFont(font.deriveFont((float) fontSize));

		double yDelta = 0;
		for (String line : lines) {
			graphicsSubsystem.drawString(line, (int) x, (int) (y + yDelta), textColor);
			yDelta += 1.5 * fontSize;
		}

	}

	private int interpolate(int v1, int v2, int iteration, int maxIterations) {
		return Utils.interpolate(v1, v2, iteration, maxIterations, (x, max) -> {
			return x * Math.sin((Math.PI * x) / (2 * max));
			// return x * Math.sin((Math.PI * x) / (2 * max));
			// return x * Math.tanh(40*(double)x/max);
			// return x * Math.sin(Math.acos(-1+x/(double)max));
			// return x * Math.sqrt((double)x / max);
			// return x * x/max;
		});
	}

	@Override
	public void stop() {
		state = GfxState.STOPPING;
	}

	@Override
	public GfxState getState() {
		return state;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		setLines(text);
		state = GfxState.STARTUP;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
		state = GfxState.STARTUP;
	}

	private void setLines(String text) {

		lines = new LinkedList<>();
		for (String line : text.split("\n")) {
			lines.add(line);
		}

	}

}
