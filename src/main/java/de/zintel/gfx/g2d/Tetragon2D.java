/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import de.zintel.gfx.EAntialiasing;
import de.zintel.gfx.texture.ITexture;
import de.zintel.math.MathUtils;
import de.zintel.math.MathUtils.StepProjection;
import de.zintel.utils.Processor;

/**
 * @author Friedemann
 *
 */
public class Tetragon2D implements IObject2D {

	private static class ColorRate {

		private final Color color;

		private final double rate;

		public ColorRate(Color color, double rate) {
			this.color = color;
			this.rate = rate;
		}

		public Color getColor() {
			return color;
		}

		public double getRate() {
			return rate;
		}

	}

	private final Point p11;

	private final Point p12;

	private final Point p21;

	private final Point p22;

	private final ITexture texture;

	private final StepProjection stepProjection;

	private final EAntialiasing antialiasing;

	private final Point t11;

	private final Point t12;

	private final Point t21;

	private final Point t22;

	private final int aaDim;

	public Tetragon2D(Pin2D pin11, Pin2D pin12, Pin2D pin21, Pin2D pin22, ITexture texture) {
		this(pin11, pin12, pin21, pin22, texture, (x, max) -> {
			return x;
		} , EAntialiasing.NONE);
	}

	public Tetragon2D(Pin2D pin11, Pin2D pin12, Pin2D pin21, Pin2D pin22, ITexture texture, EAntialiasing antialiasing) {
		this(pin11, pin12, pin21, pin22, texture, (x, max) -> {
			return x;
		} , antialiasing);
	}

	public Tetragon2D(Pin2D pin11, Pin2D pin12, Pin2D pin21, Pin2D pin22, ITexture texture, StepProjection stepProjection,
			EAntialiasing antialiasing) {
		super();
		this.p11 = pin11.point;
		this.p12 = pin12.point;
		this.p21 = pin21.point;
		this.p22 = pin22.point;
		this.texture = texture;
		this.stepProjection = stepProjection;
		this.antialiasing = antialiasing;

		if (antialiasing == EAntialiasing.NONE) {
			aaDim = 1;
		} else if (antialiasing == EAntialiasing.BILINEAR_1) {
			aaDim = 2;
		} else if (antialiasing == EAntialiasing.BILINEAR_2) {
			aaDim = 3;
		} else {
			aaDim = 1;
		}

		this.t11 = new Point(mkTxIdx(pin11), mkTyIdx(pin11));
		this.t12 = new Point(mkTxIdx(pin12), mkTyIdx(pin12));
		this.t21 = new Point(mkTxIdx(pin21), mkTyIdx(pin21));
		this.t22 = new Point(mkTxIdx(pin22), mkTyIdx(pin22));
	}

	private int mkTxIdx(final Pin2D pin) {
		return ITexture.mkTIdx(pin.txCrd.x, texture.getWidth());
	}

	private int mkTyIdx(final Pin2D pin) {
		return ITexture.mkTIdx(pin.txCrd.y, texture.getHeight());
	}

	@Override
	public void draw(Point point, Graphics graphics) {

		final int d1 = Math.max(Math.abs(p11.x - p12.x), Math.abs(p11.y - p12.y));
		final int d2 = Math.max(Math.abs(p21.x - p22.x), Math.abs(p21.y - p22.y));
		final Point sP1, sP2, eP1, eP2;
		final Point sT1, sT2, eT1, eT2;
		if (d1 >= d2) {
			sP1 = p11;
			sP2 = p12;
			eP1 = p21;
			eP2 = p22;
			sT1 = t11;
			sT2 = t12;
			eT1 = t21;
			eT2 = t22;
		} else {
			sP1 = p21;
			sP2 = p22;
			eP1 = p11;
			eP2 = p12;
			sT1 = t21;
			sT2 = t22;
			eT1 = t11;
			eT2 = t12;
		}

		// Startrand interpolieren
		new Processor<IterationUnit2D>(new AlternateLinearPointInterpolater2D(sP1, sP2, true), new Consumer<IterationUnit2D>() {

			Collection<Point> ePoints = new ArrayList<>(2);

			// Endrand interpolieren
			Processor<IterationUnit2D> eITP = new Processor<IterationUnit2D>(new AlternateLinearPointInterpolater2D(eP1, eP2, true), null);

			@Override
			public void accept(IterationUnit2D sItUnit) {

				Point sPoint = sItUnit.getPoint();
				int sStep = sItUnit.getIteration();
				int sStepMax = sItUnit.getMaxIterations();

				final double sTx = MathUtils.interpolateReal(sT1.x, sT2.x, sStep, sStepMax, stepProjection);
				final double sTy = MathUtils.interpolateReal(sT1.y, sT2.y, sStep, sStepMax, stepProjection);

				/*
				 * hier wird der Endrand interpoliert, der immer kleiner gleich
				 * dem Startrand ist. Nur, wenn sich (aufgrund der Skalierung)
				 * ein neuer Step für den Endrand ergeben würde, wird der
				 * nächste Step berechnet.
				 */
				IterationUnit2D eItUnit = eITP.getCurrent();
				if ((eItUnit == null || MathUtils.interpolateLinear(0, eItUnit.getMaxIterations(), sStep, sStepMax) > eItUnit.getIteration())
						&& eITP.hasNext()) {
					ePoints.clear();
					while ((eItUnit == null
							|| MathUtils.interpolateLinear(0, eItUnit.getMaxIterations(), sStep, sStepMax) > eItUnit.getIteration())
							&& eITP.hasNext()) {
						eITP.next();
						eItUnit = eITP.getCurrent();
						ePoints.add(eItUnit.getPoint());
					}
				}

				int eIt = eItUnit.getIteration();
				int eItMax = eItUnit.getMaxIterations();

				final double eTx = MathUtils.interpolateReal(eT1.x, eT2.x, eIt, eItMax, stepProjection);
				final double eTy = MathUtils.interpolateReal(eT1.y, eT2.y, eIt, eItMax, stepProjection);

				// Linie interpolieren
				for (Point ePoint : ePoints) {
					new Processor<IterationUnit2D>(new AlternateLinearPointInterpolater2D(sPoint, ePoint, true),
							new Consumer<IterationUnit2D>() {

						double previousTx = -1;
						double previousTy = -1;

						@Override
						public void accept(IterationUnit2D itUnit) {

							int it = itUnit.getIteration();
							int itMax = itUnit.getMaxIterations();
							Point linepoint = itUnit.getPoint();

							double tx = MathUtils.interpolateReal(sTx, eTx, it, itMax, stepProjection);
							double ty = MathUtils.interpolateReal(sTy, eTy, it, itMax, stepProjection);

							Collection<ColorRate> colorRates = new ArrayList<ColorRate>(3);

							int total = aaDim;
//
//							if (previousTx >= 0 && previousTx < tx - 1 || previousTy >= 0 && previousTy < ty - 1) {
//
//								if (previousTx >= 0 && previousTx < tx - 1) {
//									total += (tx - previousTx) + 1;
//								}
//
//								if (previousTy >= 0 && previousTy < ty - 1) {
//									total += (ty - previousTy) + 1;
//								}
//
//								double rate = 1.0 / total;
//								if (previousTx >= 0 && previousTx < tx - 1) {
//									for (int i = (int) previousTx + 1; i <= tx; i++) {
//										colorRates.add(new ColorRate(texture.getColor(i, (int) ty), rate));
//									}
//								}
//								if (previousTy >= 0 && previousTy < ty - 1) {
//									for (int i = (int) previousTy + 1; i <= ty; i++) {
//										colorRates.add(new ColorRate(texture.getColor((int) tx, i), rate));
//									}
//								}
//							}

							double dTx = tx - Math.floor(tx);
							double dTy = ty - Math.floor(ty);
							double colorPortion = 1;
							if (antialiasing == EAntialiasing.BILINEAR_1) {
								colorPortion = (1 - dTx) + (1 - dTy);
							} else if (antialiasing == EAntialiasing.BILINEAR_2) {
								colorPortion = (1 - dTx) + (1 - dTy) + ((1 - dTx) + (1 - dTy)) / 2;
							}
							colorRates.add(new ColorRate(texture.getColor((int) tx, (int) ty), colorPortion / total));

							if (antialiasing == EAntialiasing.BILINEAR_1 || antialiasing == EAntialiasing.BILINEAR_2) {
								if (dTx > 0 && tx < texture.getWidth() - 1) {
									colorRates.add(new ColorRate(texture.getColor((int) tx + 1, (int) ty), dTx / total));
								}
								if (dTy > 0 && ty < texture.getHeight() - 1) {
									colorRates.add(new ColorRate(texture.getColor((int) tx, (int) ty + 1), dTy / total));
								}
							}
							if (antialiasing == EAntialiasing.BILINEAR_2) {
								if (dTx > 0 && tx < texture.getWidth() - 1 && dTy > 0 && ty < texture.getHeight() - 1) {
									colorRates.add(new ColorRate(texture.getColor((int) tx + 1, (int) ty + 1), (dTx + dTy) / (2 * total)));
								}
							}

							double red, green, blue;
							red = green = blue = 0;
							for (ColorRate colorRate : colorRates) {

								red += colorRate.getColor().getRed() * colorRate.getRate();
								green += colorRate.getColor().getGreen() * colorRate.getRate();
								blue += colorRate.getColor().getBlue() * colorRate.getRate();

							}

							// System.out.println("red" + red + " green " +
							// green + " blue " + blue);
							graphics.setColor(new Color((int) red, (int) green, (int) blue));
							graphics.drawLine(linepoint.x + point.x, linepoint.y + point.y, linepoint.x + point.x, linepoint.y + point.y);

							previousTx = tx;
							previousTy = ty;

						}
					}).iterate();
				}
			}
		}).iterate();
	}

}
