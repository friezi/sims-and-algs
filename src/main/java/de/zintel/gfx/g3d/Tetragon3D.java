/**
 * 
 */
package de.zintel.gfx.g3d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import de.zintel.gfx.EAntialiasing;
import de.zintel.gfx.color.CUtils;
import de.zintel.gfx.g2d.LinearPointInterpolater2D;
import de.zintel.gfx.g2d.Pin2D;
import de.zintel.gfx.g2d.IterationUnit2D;
import de.zintel.gfx.g2d.Tetragon2D;
import de.zintel.gfx.texture.ITexture;
import de.zintel.math.Utils;
import de.zintel.utils.Processor;

/**
 * @author Friedemann
 *
 */
public class Tetragon3D implements IObject3D {

	private final Pin3D p11;

	private final Pin3D p12;

	private final Pin3D p21;

	private final Pin3D p22;

	private final ITexture texture;

	private final Point t11;

	private final Point t12;

	private final Point t21;

	private final Point t22;

	private boolean scaleBrighteness = false;

	private boolean do3D = false;

	public Tetragon3D(Pin3D pin11, Pin3D pin12, Pin3D pin21, Pin3D pin22, ITexture texture) {
		super();
		this.p11 = pin11;
		this.p12 = pin12;
		this.p21 = pin21;
		this.p22 = pin22;
		this.texture = texture;
		this.t11 = new Point(mkTxIdx(pin11), mkTyIdx(pin11));
		this.t12 = new Point(mkTxIdx(pin12), mkTyIdx(pin12));
		this.t21 = new Point(mkTxIdx(pin21), mkTyIdx(pin21));
		this.t22 = new Point(mkTxIdx(pin22), mkTyIdx(pin22));
	}

	private int mkTxIdx(final Pin3D pin) {
		return ITexture.mkTIdx(pin.txCrd.x, texture.getWidth());
	}

	private int mkTyIdx(final Pin3D pin) {
		return ITexture.mkTIdx(pin.txCrd.y, texture.getHeight());
	}

	@Override
	public void draw(final Point3D point, Graphics graphics, final View3D view) {

		if (do3D) {
			interpolate3D(point, graphics, view);
		} else {
			interpolate2D(point, graphics, view);
		}
	}

	private void interpolate3D(final Point3D point, Graphics graphics, final View3D view) {

		final int d1 = Math.max(Math.max(Math.abs(p11.point.x - p12.point.x), Math.abs(p11.point.y - p12.point.y)),
				Math.abs(p11.point.z - p12.point.z));
		final int d2 = Math.max(Math.max(Math.abs(p21.point.x - p22.point.x), Math.abs(p21.point.y - p22.point.y)),
				Math.abs(p21.point.z - p22.point.z));
		final Point3D sP1, sP2, eP1, eP2;
		final Point sT1, sT2, eT1, eT2;
		if (d1 >= d2) {
			sP1 = p11.point;
			sP2 = p12.point;
			eP1 = p21.point;
			eP2 = p22.point;
			sT1 = t11;
			sT2 = t12;
			eT1 = t21;
			eT2 = t22;
		} else {
			sP1 = p21.point;
			sP2 = p22.point;
			eP1 = p11.point;
			eP2 = p12.point;
			sT1 = t21;
			sT2 = t22;
			eT1 = t11;
			eT2 = t12;
		}

		// Startrand interpolieren
		new Processor<StepUnit3D>(new LinearPointInterpolater3D(sP1.add(point), sP2.add(point), true), new Consumer<StepUnit3D>() {

			Collection<Point3D> ePoints = new ArrayList<>(2);

			// Endrand interpolieren
			Processor<StepUnit3D> eITP = new Processor<StepUnit3D>(new LinearPointInterpolater3D(eP1.add(point), eP2.add(point), true),
					stepUnit -> {
				ePoints.add(stepUnit.getPoint());
			}) {
				{
					if (this.hasNext()) {
						this.next();
					}
				}
			};

			@Override
			public void accept(StepUnit3D sStepUnit) {

				Point3D sPoint = sStepUnit.getPoint();
				int sStep = sStepUnit.getStep();
				int sStepMax = sStepUnit.getStepMax();

				StepUnit3D eStepUnit = eITP.getCurrent();
				if (Utils.interpolateLinear(0, eStepUnit.getStepMax(), sStep, sStepMax) > eStepUnit.getStep() && eITP.hasNext()) {
					ePoints.clear();
					eITP.next();
					eStepUnit = eITP.getCurrent();
				}

				int eStep = eStepUnit.getStep();
				int eStepMax = eStepUnit.getStepMax();

				// Linie interpolieren
				for (Point3D ePoint : ePoints) {
					new Processor<StepUnit3D>(new LinearPointInterpolater3D(sPoint, ePoint, true), new Consumer<StepUnit3D>() {

						private Point lastpoint = null;
						private int lastTx;
						private int lastTy;

						@Override
						public void accept(StepUnit3D stepUnit) {

							int step = stepUnit.getStep();
							int stepMax = stepUnit.getStepMax();
							Point3D linepoint = stepUnit.getPoint();

							int tx = Utils.interpolateLinear(Utils.interpolateLinear(sT1.x, sT2.x, sStep, sStepMax),
									Utils.interpolateLinear(eT1.x, eT2.x, eStep, eStepMax), step, stepMax);
							int ty = Utils.interpolateLinear(Utils.interpolateLinear(sT1.y, sT2.y, sStep, sStepMax),
									Utils.interpolateLinear(eT1.y, eT2.y, eStep, eStepMax), step, stepMax);
							Color color = texture.getColor(tx, ty);

							int red = (scaleBrighteness ? Utils.interpolateLinear(CUtils.minDark(color.getRed()),
									CUtils.maxBright(color.getRed()), step, stepMax) : color.getRed());
							int green = (scaleBrighteness ? Utils.interpolateLinear(CUtils.minDark(color.getGreen()),
									CUtils.maxBright(color.getGreen()), step, stepMax) : color.getGreen());
							int blue = (scaleBrighteness ? Utils.interpolateLinear(CUtils.minDark(color.getBlue()),
									CUtils.maxBright(color.getBlue()), step, stepMax) : color.getBlue());

							Point planePoint = view.getProjector().project(linepoint.add(view.getNullpoint()));
							if (lastpoint == null
									|| (Math.abs(planePoint.x - lastpoint.x) <= 1 && Math.abs(planePoint.y - lastpoint.y) <= 1)) {

								graphics.setColor(new Color(red, green, blue));
								graphics.drawLine(planePoint.x, planePoint.y, planePoint.x, planePoint.y);

							} else {

								new Processor<IterationUnit2D>(new LinearPointInterpolater2D(lastpoint, planePoint, true),
										new Consumer<IterationUnit2D>() {

									@Override
									public void accept(IterationUnit2D stepUnit2D) {

										int step2D = stepUnit2D.getIteration();
										if (step2D == 0) {
											// der ist bereits
											// gezeichnet
											return;
										}

										int stepMax2D = stepUnit2D.getMaxIterations();
										Point iPoint = stepUnit2D.getPoint();
										graphics.setColor(texture.getColor(Utils.interpolateLinear(lastTx, tx, step2D, stepMax2D),
												Utils.interpolateLinear(lastTy, ty, step2D, stepMax2D)));
										graphics.drawLine(iPoint.x, iPoint.y, iPoint.x, iPoint.y);

									}
								}).iterate();
							}

							lastpoint = planePoint;
							lastTx = tx;
							lastTy = ty;
						}
					}).iterate();
				}
			}
		}).iterate();
	}

	private void interpolate2D(final Point3D point, Graphics graphics, final View3D view) {

		new Tetragon2D(new Pin2D(view.getProjector().project(p11.point.add(view.getNullpoint()).add(point)), p11.txCrd),
				new Pin2D(view.getProjector().project(p12.point.add(view.getNullpoint()).add(point)), p12.txCrd),
				new Pin2D(view.getProjector().project(p21.point.add(view.getNullpoint()).add(point)), p21.txCrd),
				new Pin2D(view.getProjector().project(p22.point.add(view.getNullpoint()).add(point)), p22.txCrd), texture,
				EAntialiasing.BILINEAR_2).draw(new Point(0, 0), graphics);

	}

	public boolean isScaleBrighteness() {
		return scaleBrighteness;
	}

	public void setScaleBrighteness(boolean scaleBrighteness) {
		this.scaleBrighteness = scaleBrighteness;
	}

}
