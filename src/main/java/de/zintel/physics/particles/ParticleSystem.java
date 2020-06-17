/**
 * 
 */
package de.zintel.physics.particles;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * @author friedo
 *
 * @param <P>
 *            type of particle
 * @param <A>
 *            type of attribute
 *
 */
public abstract class ParticleSystem<P extends Particle<A>, A> {

	private final Function<P, A> attributeInitializer;

	private Set<P> particles;

	public ParticleSystem(Function<P, A> attributeInitializer, Set<P> particles) {
		this.attributeInitializer = attributeInitializer;
		this.particles = particles;
	}

	protected void initParticle(final P particle) {
		particle.setAttribute(attributeInitializer.apply(particle));
	}

	public abstract void calculate(Dimension dimension);

	public Collection<P> getParticles() {
		return particles;
	}

	protected void setParticles(Set<P> particles) {
		this.particles = particles;
	}

}
