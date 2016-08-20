/**
 * 
 */
package de.zintel.sim.nbodies;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.color.CUtils.ColorGenerator;
import de.zintel.gfx.graphicsubsystem.IGraphicsSubsystem;
import de.zintel.physics.Body;
import de.zintel.sim.nbodies.sceneries.Scenery;

/**
 * @author Friedemann
 *
 */
public class DefaultRenderer implements IRenderer {

	private static final Random RANDOM = new Random();

	private static final double CVARIANCE_PROB = 0.1;

	private static final int CVARIANCE = 81;

	private static final Color[] firetailColors = new Color[] { Color.RED, Color.ORANGE, Color.ORANGE };

	private final IGraphicsSubsystem graphicsSubsystem;

	private final Scenery scenery;

	public DefaultRenderer(IGraphicsSubsystem graphicsSubsystem, Scenery scenery) {
		this.graphicsSubsystem = graphicsSubsystem;
		this.scenery = scenery;
	}

	@Override
	public void initGraphics() {

		graphicsSubsystem.init();

		if (scenery.getSceneryConfig().isStarfield()) {

			graphicsSubsystem.setBackground(new Color(0, 0, 15));
			graphicsSubsystem.setFullScreen();

		}

	}

	@Override
	public void display() {
		graphicsSubsystem.display();
	}

	@Override
	public void renderBody(Body body) {

		final SwingBodyProperty property = getProperty(body);

		final Color newColor = CUtils.makeRandomStarColor();
		final Color innercolor = (scenery.getSceneryConfig().isStarfield() ? makeChangedColor(newColor, newColor)
				: property.getBodyColor());
		final Color outercolor = property.getBodyColor();
		property.setCurrentBodyColor(innercolor);

		// sorgt bei entsprechender Hardwareunterstützung für changierenden
		// Körper
		final ColorGenerator colorGenerator = new ColorGenerator() {

			private boolean center = true;

			private final Color rimColor = new Color(outercolor.getRed(), outercolor.getGreen(), outercolor.getBlue(), 50);

			@Override
			public Color generateColor() {

				if (center) {
					center = false;
					return innercolor;
				} else {
					return rimColor;
				}

			}
		};

		double size = Math.ceil(body.getSize());
		graphicsSubsystem.drawFilledCircle((int) project(body.getPosition().x, graphicsSubsystem.getWidth()),
				(int) project(body.getPosition().y, graphicsSubsystem.getHeight()), (int) Math.max(scale(size), 1), colorGenerator);

	}

	private int modifyCValue(final int value, final int variance) {

		final int oValue = RANDOM.nextInt(variance) - (variance / 2);
		return Math.max(0, Math.min(value + oValue, 255));

	}

	private double project(double value, double dimension) {
		return dimension / 2 - (dimension / 2 - value) / scenery.getSceneryConfig().getDistance();
	}

	private double scale(double value) {
		return value / scenery.getSceneryConfig().getDistance();
	}

	private SwingBodyProperty newProperty() {

		SwingBodyProperty property = new SwingBodyProperty();

		Color color = (scenery.getSceneryConfig().isStarfield() ? CUtils.makeRandomStarColor() : CUtils.makeRandomColor());
		property.setBodyColor(color);
		property.setCurrentBodyColor(color);
		property.setCurrentCoronaColor(color);

		return property;

	}

	private SwingBodyProperty getProperty(Body body) {

		SwingBodyProperty property = (SwingBodyProperty) body.getProperty(SwingBodyProperty.CLASSNAME);
		if (property == null) {

			property = newProperty();
			body.setProperty(SwingBodyProperty.CLASSNAME, property);

		}

		return property;
	}

	@Override
	public void renderCorona(Body body) {

		final SwingBodyProperty property = getProperty(body);
		Integer rateCoronaRays = property.getRateCoronaRays();
		if (rateCoronaRays == null) {

			rateCoronaRays = new Integer(2 * (RANDOM.nextInt(4) + 1));
			property.setRateCoronaRays(rateCoronaRays);
			property.setSpinned(RANDOM.nextBoolean());

		}
		final int rateRays = rateCoronaRays;

		final Boolean spinned = property.isSpinned();
		float axisRayDiv = (spinned ? 3f : 1f);
		float diagonalRayDiv = (spinned ? 1f : 3f);

		double size = Math.ceil(body.getSize());
		if (body.getSize() > 5) {

			int rings = Math.max(3, (int) (body.getSize() / 10));
			double coeff = 7.0 / rings;

			if (graphicsSubsystem.supportsColorChange()) {

				double factor = rings * coeff;
				final double scaledSize = scale(size + size * factor);

				// sorgt bei entsprechender Hardwareunterstützung für Strahlen
				final ColorGenerator colorGenerator = new ColorGenerator() {

					private int i = -1;

					private Color basecolor = makeChangedColor(property.getBodyColor(), property.getCurrentCoronaColor());

					@Override
					public Color generateColor() {

						float divisor;

						if (i == -1) {
							divisor = 2f;
						} else if (i % rateRays == 0)
							if (i % (4 * rateRays) == 0) {
								divisor = axisRayDiv;
							} else if (i % (2 * rateRays) == 0) {
								divisor = diagonalRayDiv;
							} else {
								divisor = 5f;
							}
						else {
							divisor = 10f;
						}

						final Color color = new Color((int) (basecolor.getRed() / divisor), (int) (basecolor.getGreen() / divisor),
								(int) (basecolor.getBlue() / divisor), (i == -1 ? 200 : 1));
						i++;
						return color;
					}
				};

				graphicsSubsystem.drawFilledCircle((int) project(body.getPosition().x, graphicsSubsystem.getWidth()),
						(int) project(body.getPosition().y, graphicsSubsystem.getHeight()), (int) scaledSize, colorGenerator);

			} else {

				for (int i = 1; i <= rings; i++) {

					// Faktor für die Corona
					double factor = i * coeff;
					final int alphacnt = i;
					final double scaledSize = scale(size + size * factor);

					// sorgt bei entsprechender Hardwareunterstützung für
					// Strahlen
					final ColorGenerator colorGenerator = new ColorGenerator() {

						private int i = -1;

						private Color basecolor = makeChangedColor(property.getBodyColor(), property.getCurrentCoronaColor());

						@Override
						public Color generateColor() {

							float divisor;

							if (i == -1) {
								divisor = 1f;
							} else if (i % rateRays == 0)
								if (i % (4 * rateRays) == 0) {
									divisor = axisRayDiv;
								} else if (i % (2 * rateRays) == 0) {
									divisor = diagonalRayDiv;
								} else {
									divisor = 1.8f;
								}
							else {
								divisor = 2f;
							}

							i++;
							return new Color((int) (basecolor.getRed() / divisor), (int) (basecolor.getGreen() / divisor),
									(int) (basecolor.getBlue() / divisor), 100 / alphacnt);
						}
					};
					graphicsSubsystem.drawFilledCircle((int) project(body.getPosition().x, graphicsSubsystem.getWidth()),
							(int) project(body.getPosition().y, graphicsSubsystem.getHeight()), (int) scaledSize, colorGenerator);
				}
			}
		}

		property.setCurrentCoronaColor(makeChangedColor(property.getBodyColor(), property.getCurrentCoronaColor()));
	}

	/**
	 * @param baseColor
	 * @param currentColor
	 * @return
	 */
	private Color makeChangedColor(final Color baseColor, final Color currentColor) {
		return RANDOM.nextDouble() <= CVARIANCE_PROB
				? new Color(modifyCValue(baseColor.getRed(), CVARIANCE), modifyCValue(baseColor.getGreen(), CVARIANCE),
						modifyCValue(baseColor.getBlue(), CVARIANCE))
				: (RANDOM.nextDouble() <= CVARIANCE_PROB ? Color.WHITE : currentColor);
	}

	@Override
	public void renderVelocity(Body body) {
		graphicsSubsystem.drawLine((int) project(body.getPosition().x, graphicsSubsystem.getWidth()),
				(int) project(body.getPosition().y, graphicsSubsystem.getHeight()),
				(int) project(body.getPosition().x + 10 * body.getVelocity().x, graphicsSubsystem.getWidth()),
				(int) project(body.getPosition().y + 10 * body.getVelocity().y, graphicsSubsystem.getHeight()), Color.RED);

	}

	@Override
	public void renderFiretail(Body body) {

		Color color = firetailColors[RANDOM.nextInt(firetailColors.length)];
		final float cCoeff = 0.8f;
		color = new Color((int) (cCoeff * color.getRed()), (int) (cCoeff * color.getGreen()), (int) (cCoeff * color.getBlue()));

		int a = 255;
		double alphaDecayFactor = 1d;
		double colorBrigthnessFactor = 0;
		int max = 6;
		int startx = (int) project(body.getPosition().x, graphicsSubsystem.getWidth());
		int starty = (int) project(body.getPosition().y, graphicsSubsystem.getHeight());

		final Physics physics = scenery.getSceneryConfig().getPhysics();
		if (physics.getDestroyParticlesSpeedThreshold() > 0 && body.getVelocity().length() >= physics.getDestroyParticlesSpeedThreshold()) {
			// // Dividend: Hälfte der Differenz wird von velocity abgezogen, um
			// // den Effekt noch zu verzögern
			// alphaDecayFactor = physics.getDestroyParticlesSpeedThreshold()
			// / ((3 * body.getVelocity().length() -
			// physics.getDestroyParticlesSpeedThreshold()) / 2);
			alphaDecayFactor = physics.getDestroyParticlesSpeedThreshold() / body.getVelocity().length();
			colorBrigthnessFactor = (1.5 * body.getVelocity().length()) / physics.getDestroyParticlesSpeedThreshold();
		}

		for (int i = -1; i < max; i++) {

			if (i == -1) {

				final int endx = (int) project(body.getPosition().x - 15 * body.getVelocity().x / body.getVelocity().length(),
						graphicsSubsystem.getWidth());
				final int endy = (int) project(body.getPosition().y - 15 * body.getVelocity().y / body.getVelocity().length(),
						graphicsSubsystem.getHeight());
				final Color iColor = Color.WHITE;
				final int alpha = (int) (a * alphaDecayFactor);
				graphicsSubsystem.drawLine(startx, starty, endx, endy,
						new Color(iColor.getRed(), iColor.getGreen(), iColor.getBlue(), alpha));

				startx = endx;
				starty = endy;

			} else {

				final int endx = (int) project(body.getPosition().x - 10 * body.getVelocity().x / (max - i), graphicsSubsystem.getWidth());
				final int endy = (int) project(body.getPosition().y - 10 * body.getVelocity().y / (max - i), graphicsSubsystem.getHeight());
				final int red = brighten(color.getRed(), colorBrigthnessFactor);
				final int green = brighten(color.getGreen(), colorBrigthnessFactor);
				final int blue = brighten(color.getBlue(), colorBrigthnessFactor);
				final int alpha = (int) ((a * alphaDecayFactor) / (i + 1));
				graphicsSubsystem.drawLine(startx, starty, endx, endy, new Color(red, green, blue, alpha));

				startx = endx;
				starty = endy;

			}
		}

	}

	private int brighten(final int cValue, double colorBrigthnessFactor) {

		if (colorBrigthnessFactor < 1) {
			return cValue;
		} else {
			return (int) (cValue + (colorBrigthnessFactor - 1) * ((255 - cValue) / colorBrigthnessFactor));
		}

	}

	@Override
	public void initBodyProperties(Collection<Body> bodies) {
		for (Body body : bodies) {

			if (body.getProperty(SwingBodyProperty.CLASSNAME) == null) {
				body.setProperty(SwingBodyProperty.CLASSNAME, newProperty());
			}
		}
	}

}
