/**
 * 
 */
package de.zintel.gfx.g2d;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import de.zintel.gfx.texture.ITexture;
import de.zintel.math.MathUtils;
import de.zintel.math.MathUtils.StepProjection;
import de.zintel.utils.Processor;

/**
 * @author Friedemann
 *
 */
public class Tetragon2D implements IObject2D {

	private final Point p11;

	private final Point p12;

	private final Point p21;

	private final Point p22;

	private final ITexture texture;

	private final StepProjection stepProjection;

	private final Point t11;

	private final Point t12;

	private final Point t21;

	private final Point t22;

	public Tetragon2D(Pin2D pin11, Pin2D pin12, Pin2D pin21, Pin2D pin22, ITexture texture) {
		this(pin11, pin12, pin21, pin22, texture, (x, max) -> {
			return x;
		});
	}

	public Tetragon2D(Pin2D pin11, Pin2D pin12, Pin2D pin21, Pin2D pin22, ITexture texture, StepProjection stepProjection) {
		super();
		this.p11 = pin11.point;
		this.p12 = pin12.point;
		this.p21 = pin21.point;
		this.p22 = pin22.point;
		this.texture = texture;
		this.stepProjection = stepProjection;

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

			/*
			 * Um Moiree-Effekte zu vermeiden, werden z. T. mehr als ein Punkt
			 * pro Interpolationsschritt generiert.
			 */
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
						&& eITP.inProcess()) {
					ePoints.clear();
					while ((eItUnit == null || MathUtils.interpolateLinear(0, eItUnit.getMaxIterations(), sStep, sStepMax) > eItUnit.getIteration())
							&& eITP.inProcess()) {
						eITP.progress();
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
					new Processor<IterationUnit2D>(new AlternateLinearPointInterpolater2D(sPoint, ePoint, true), new Consumer<IterationUnit2D>() {

						@Override
						public void accept(IterationUnit2D itUnit) {

							int it = itUnit.getIteration();
							int itMax = itUnit.getMaxIterations();
							Point linepoint = itUnit.getPoint();

							double tx = MathUtils.interpolateReal(sTx, eTx, it, itMax, stepProjection);
							double ty = MathUtils.interpolateReal(sTy, eTy, it, itMax, stepProjection);

							graphics.setColor(texture.getColor(tx, ty));
							graphics.drawLine(linepoint.x + point.x, linepoint.y + point.y, linepoint.x + point.x, linepoint.y + point.y);

						}
					}).process();
				}
			}
		}).process();
	}

}
