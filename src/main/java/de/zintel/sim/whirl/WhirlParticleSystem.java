/**
 * 
 */
package de.zintel.sim.whirl;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector3D;
import de.zintel.physics.particles.ParticleSystem;

/**
 * @author friedo
 *
 */
public class WhirlParticleSystem<A> extends ParticleSystem<WhirlParticle<A>, A> {

	private double deltaxmin;

	private double deltaxmax = 200;

	private double rotationTransitionLeft = -3;

	private double rotationTransitionRight = 4;

	private Vector3D rotcenter;

	private double particlesminy;

	private double particlesmaxy;

	private final double finalCircleRadius;

	private int frequency = 1;

	public WhirlParticleSystem(Function<WhirlParticle<A>, A> attributeInitializer, Set<WhirlParticle<A>> particles, Vector3D rotcenter,
			double deltaxmin, double deltaxmax, double particlesminy, double particlesmaxy, double finalCircleRadius) {
		super(attributeInitializer, particles);
		this.rotcenter = rotcenter;
		this.deltaxmin = deltaxmin;
		this.deltaxmax = deltaxmax;
		this.particlesminy = particlesminy;
		this.particlesmaxy = particlesmaxy;
		this.finalCircleRadius = finalCircleRadius;
	}

	@Override
	public void calculate(Dimension dimension) {

		final double width = dimension.getWidth() + deltaxmax;

		Set<WhirlParticle<A>> newparticles = new LinkedHashSet<>(getParticles());

		final Iterator<WhirlParticle<A>> iterator = newparticles.iterator();
		while (iterator.hasNext()) {

			final WhirlParticle<A> particle = iterator.next();
			final Vector3D point = particle.getPosition();

			final Function<Double, Double> rottrans = x -> MathUtils
					.sigmoid(MathUtils.scalel(0, width, rotationTransitionLeft, rotationTransitionRight, point.x()));

			// particles velocity in x direction should increase by progressing in x direction
			point.setX(point.x() + MathUtils.morph(v -> particle.velocity, v -> particle.velocity + 10, rottrans, point.x()));

			if (point.x() > width) {

				iterator.remove();
				continue;

			}

			// the radius should decrease by progressing in x direction
			final double cradius = MathUtils.morph(x -> Math.abs(rotcenter.y() - particle.initialPosition.y()), x -> finalCircleRadius, rottrans,
					point.x());

			// the angular velocity should increase by progressing in x direction
			particle.angle += MathUtils.morph(x -> 0.000005, x -> 40D, rottrans, point.x());

			// rotation on x axis
			point.setZ(Math.sin(theta(particle.angle)) * cradius + rotcenter.z());
			point.setY(rotcenter.y() + (particle.initialPosition.y() < rotcenter.y() ? -1 : 1) * Math.cos(theta(particle.angle)) * cradius);

		}

		// frequency of particle generation
		if (validByFrequency(frequency)) {
			final WhirlParticle<A> particle = new WhirlParticle<A>(
					new Vector3D(deltaxmin, MathUtils.makeRandom((int) particlesminy, (int) particlesmaxy), rotcenter.z()),
					MathUtils.makeRandom(2, 7));
			initParticle(particle);
			newparticles.add(particle);
		}

		setParticles(newparticles);
	}

	/** degree -> radian
	 * @param degree
	 * @return
	 */
	private double theta(final double degree) {
		return MathUtils.scalel(0, 360, 0, 2 * Math.PI, ((int) degree) % 360);
	}

	private boolean validByFrequency(final int frequency) {

		if (frequency > 0) {
			return MathUtils.RANDOM.nextInt(frequency) == frequency - 1;
		} else if (frequency < 0) {
			return MathUtils.RANDOM.nextInt(-frequency) != -frequency - 1;
		} else {
			return false;
		}

	}

	public double getDeltaxmin() {
		return deltaxmin;
	}

	public void setDeltaxmin(double deltaxmin) {
		this.deltaxmin = deltaxmin;
	}

	public double getDeltaxmax() {
		return deltaxmax;
	}

	public void setDeltaxmax(double deltaxmax) {
		this.deltaxmax = deltaxmax;
	}

	public double getRotationTransitionLeft() {
		return rotationTransitionLeft;
	}

	public void setRotationTransitionLeft(double rotationTransitionLeft) {
		this.rotationTransitionLeft = rotationTransitionLeft;
	}

	public double getRotationTransitionRight() {
		return rotationTransitionRight;
	}

	public void setRotationTransitionRight(double rotationTransitionRight) {
		this.rotationTransitionRight = rotationTransitionRight;
	}

	public Vector3D getRotcenter() {
		return rotcenter;
	}

	public void setRotcenter(Vector3D rotcenter) {
		this.rotcenter = rotcenter;
	}

	public double getParticlesminy() {
		return particlesminy;
	}

	public void setParticlesminy(double particlesminy) {
		this.particlesminy = particlesminy;
	}

	public double getParticlesmaxy() {
		return particlesmaxy;
	}

	public void setParticlesmaxy(double particlesmaxy) {
		this.particlesmaxy = particlesmaxy;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public double getFinalCircleRadius() {
		return finalCircleRadius;
	}

}
