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
import de.zintel.math.MathUtils;

/**
 * @author Friedemann
 *
 */
public class FadingText implements IGfxComponent {

	private static final int MIN_ALPHA = 0;

	private static final int MAX_ALPHA = 255;

	private static final int DFLT_MAX_ITERATIONS = 20;

	private static final int MAX_FONT_SIZE = 50;

	private static final int MIN_FONT_SIZE = 15;

	private static final int MAX_X = 200;

	private static final int MAX_Y = 400;

	private final GfxStayAliveMode mode;

	private GfxState state = GfxState.STARTUP;

	private String text;

	private Collection<String> lines;

	private Point position;

	private Color color;

	private long timeout;

	private int maxIterations = DFLT_MAX_ITERATIONS;

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
			if (iteration > maxIterations) {

				alpha = MAX_ALPHA;
				fontSize = MIN_FONT_SIZE;
				x = position.x;
				y = position.y;
				iteration = maxIterations;
				state = GfxState.COMPLETE;
				startTs = System.currentTimeMillis();

			} else {

				alpha = interpolate(0, MAX_ALPHA, iteration, maxIterations);
				fontSize = interpolate(MAX_FONT_SIZE, MIN_FONT_SIZE, iteration, maxIterations);
				x = interpolate(position.x + MAX_X, position.x, iteration, maxIterations);
				y = interpolate(position.y + MAX_Y, position.y, iteration, maxIterations);

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

				alpha = interpolate(MAX_ALPHA, 0, maxIterations - iteration, maxIterations);
				fontSize = interpolate(MIN_FONT_SIZE, MAX_FONT_SIZE, maxIterations - iteration, maxIterations);
				x = interpolate(position.x, position.x + MAX_X, maxIterations - iteration, maxIterations);
				y = interpolate(position.y, position.y + MAX_Y, maxIterations - iteration, maxIterations);

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
		return MathUtils.interpolate(v1, v2, iteration, maxIterations, (x, max) -> 
			x * Math.sin((Math.PI * x) / (2 * max))
			// x * Math.sin((Math.PI * x) / (2 * max))
			// x * Math.tanh(40*(double)x/max)
			// x * Math.sin(Math.acos(-1+x/(double)max))
			// x * Math.sqrt((double)x / max)
			// x * x/max
		);
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

	public FadingText setText(String text) {
		this.text = text;
		setLines(text);
		state = GfxState.STARTUP;
		return this;
	}

	public Point getPosition() {
		return position;
	}

	public FadingText setPosition(Point position) {
		this.position = position;
		state = GfxState.STARTUP;
		return this;
	}

	private void setLines(String text) {

		lines = new LinkedList<>();
		for (String line : text.split("\n")) {
			lines.add(line);
		}

	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public FadingText setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
		return this;
	}

}
