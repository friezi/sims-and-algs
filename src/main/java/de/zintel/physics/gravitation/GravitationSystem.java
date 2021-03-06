/**
 * 
 */
package de.zintel.physics.gravitation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;

import de.zintel.gfx.g2d.Field;
import de.zintel.math.MathUtils;
import de.zintel.math.Polar2D;
import de.zintel.math.Vector2DPlain;
import de.zintel.physics.Body;
import de.zintel.utils.CompositeCollection;
import de.zintel.utils.Pair;

/**
 * @author Friedemann
 *
 */
public class GravitationSystem implements IBodyProducer {

	private long timestamp = 0;

	private Field field;

	private Physics physics;

	private Collection<Body> massBodies = new TreeSet<>();

	private Collection<Body> particles = new TreeSet<>();

	private final Random RANDOM = new Random();

	public GravitationSystem(Field field, Physics physics) {
		this.field = field;
		this.physics = physics;
	}

	/**
	 * @param body
	 * @return
	 */
	public IBodyProducer addBody(final Body body) {
		massBodies.add(body);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#gravitate()
	 */
	@Override
	public void calculate() {
		gravitate();
	}

	/**
	 * 
	 */
	public void gravitate() {

		timestamp++;

		Collection<Body> nextMassBodies = Collections.synchronizedCollection(new TreeSet<>());
		Collection<Body> nextParticles = Collections.synchronizedCollection(new TreeSet<>());

		massBodies.parallelStream().forEach(body -> {

			final CompositeCollection<Body> bodies = new CompositeCollection<Body>().cat(massBodies);

			if (physics.isParticleInfluence()) {
				bodies.cat(particles);
			}

			Pair<Vector2DPlain, Vector2DPlain> direction = calculateMovement(body, bodies);
			final Collection<Body> nextBodies = calculateNewPosition(body, direction.getFirst(), direction.getSecond());

			for (final Body nextBody : nextBodies) {
				if (nextBody.isParticle()) {
					nextParticles.add(nextBody);
				} else {
					nextMassBodies.add(nextBody);
				}
			}

		});

		particles.parallelStream().forEach(body -> {

			final CompositeCollection<Body> bodies = new CompositeCollection<Body>().cat(massBodies);

			if (physics.isParticleOnParticle()) {
				bodies.cat(particles);
			}

			Pair<Vector2DPlain, Vector2DPlain> direction = calculateMovement(body, bodies);
			final Collection<Body> nextBodies = calculateNewPosition(body, direction.getFirst(), direction.getSecond());

			for (final Body nextBody : nextBodies) {
				if (nextBody.isParticle()) {
					nextParticles.add(nextBody);
				} else {
					nextMassBodies.add(nextBody);
				}
			}

		});

		massBodies = nextMassBodies;
		particles = nextParticles;
	}

	/**
	 * @param body
	 * @param target
	 * @param velocity
	 * @return
	 */
	private Collection<Body> calculateNewPosition(final Body body, Vector2DPlain velocity, Vector2DPlain acceleration) {

		Vector2DPlain position = new Vector2DPlain(body.getPosition());
		acceleration.mult(physics.getAccelerationFactor());
		double decay = physics.getDecay();
		velocity.add(acceleration);
		position.add(velocity);

		double offset = body.getSize();
		// Reflexion vom Rand
		if (position.x - offset < field.minX) {
			velocity = new Vector2DPlain(-velocity.x / decay, velocity.y);
			position.x = 2 * offset - (position.x - field.minX) - (decay - 1) * (offset - (position.x - field.minX)) / decay + field.minX;
		} else if (position.x + offset > field.maxX) {
			velocity = new Vector2DPlain(-velocity.x / decay, velocity.y);
			position.x = 2 * (field.maxX - offset) - position.x + (decay - 1) * (offset + position.x - field.maxX) / decay;
		}
		if (position.y - offset < field.minY) {
			velocity = new Vector2DPlain(velocity.x, -velocity.y / decay);
			position.y = 2 * offset - (position.y - field.minY) - (decay - 1) * (offset - (position.y - field.minY)) / decay + field.minY;
		} else if (position.y + offset > field.maxY) {
			velocity = new Vector2DPlain(velocity.x, -velocity.y / decay);
			position.y = 2 * (field.maxY - offset) - position.y + (decay - 1) * (offset + position.y - field.maxY) / decay;
		}

		Body newBody = new Body(body.getId(), body.getSize(), body.getMass(), position, velocity, body);
		newBody.getParameters().timestamp = body.getParameters().timestamp;
		newBody.getParameters().timeout = body.getParameters().timeout;
		newBody.setParticle(body.isParticle());
		newBody.setMelting(body.isMelting());
		newBody.setMeltingIntensity(body.getMeltingIntensity());

		if (body.isDestroyed()) {

			if (body.isParticle()) {
				return Collections.emptySet();
			} else {
				return createExplosionParticles(newBody);
			}

		} else {

			Collection<Body> newBodies = new ArrayList<>(1);
			newBodies.add(newBody);

			if (newBody.isMelting()) {
				// not yet working

				final Collection<Body> meltingParticles = createMeltingParticles(newBody);
				for (Body particle : meltingParticles) {
					newBody.setMass(newBody.getMass() - particle.getMass());
				}
				newBodies.addAll(meltingParticles);

				body.setMelting(false);
			}

			return newBodies;

		}
	}

	public boolean hasTimedOut(final Body body, final double gravitationAmount) {
		return body.getParameters().timestamp + body.getParameters().timeout * (1 + Math.pow(gravitationAmount / 100, 2)) < timestamp;
	}

	/**
	 * @param body
	 * @return
	 */
	private Collection<Body> createExplosionParticles(Body body) {

		int amountParticles = physics.getParticleMass() > (int) body.getMass() ? 1 : (int) body.getMass() / physics.getParticleMass();

		Collection<Body> particles = new ArrayList<>(amountParticles);

		double mass = body.getMass() / amountParticles;
		double size = Physics.calculateSize(mass);
		for (int i = 0; i < amountParticles; i++) {

			Vector2DPlain position = createExplosionParticlePosition(body);

			Vector2DPlain velocity;
			if (body.getVelocity().length() == 0) {
				velocity = new Vector2DPlain();
			} else {
				velocity = createExplosionParticleVelocity(body);
			}

			Body particle = new Body("particle:" + body.getId() + ":" + i, size, mass, position, velocity, body);
			particle.setParticle(true);
			particle.getParameters().timestamp = timestamp;
			particle.getParameters().timeout = generateParticleTimeout();
			particles.add(particle);
		}

		return particles;

	}

	private long generateParticleTimeout() {
		return (MathUtils.RANDOM.nextInt(5) * 3 + 20) * 30;
	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2DPlain createExplosionParticleVelocity(Body body) {

		double deltaAngle = RANDOM.nextInt(91) - 45;
		Polar2D newVelocity = body.getVelocity().toPolar();
		newVelocity.angle += (deltaAngle * Math.PI) / 180;
		newVelocity.radius /= physics.getParticleVelocityReduction();

		return newVelocity.toCartesian();
	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2DPlain createExplosionParticlePosition(Body body) {

		Vector2DPlain position = new Vector2DPlain(body.getPosition());
		Polar2D deltaPosition = new Polar2D((2 * RANDOM.nextDouble() + 0.5) * body.getSize(), RANDOM.nextDouble() * 2 * Math.PI);
		position.add(deltaPosition.toCartesian());
		return position;
	}

	/**
	 * @param body
	 * @return
	 */
	private Collection<Body> createMeltingParticles(Body body) {

		if (physics.getParticleMass() > body.getMass()) {
			return Collections.emptyList();
		}

		Collection<Body> particles = new ArrayList<>();

		final double mass = physics.getParticleMass();
		double size = Physics.calculateSize(mass);

		Body particle = new Body("meltingparticle:" + body.getId(), size, mass, createMeltingParticlePosition(body),
				new Vector2DPlain(body.getVelocity()), body);
		particle.setParticle(true);
		particle.getParameters().timestamp = timestamp;
		particle.getParameters().timeout = generateParticleTimeout();
		particles.add(particle);

		return particles;

	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2DPlain createMeltingParticlePosition(Body body) {

		final int max = 4;
		final double xVal = RANDOM.nextDouble() * max - max / 2;
		final double yVal = 0.5 * xVal * xVal;
		double deltaAngle = Math.PI * yVal / max;
		if (RANDOM.nextInt(2) == 1) {
			deltaAngle = -deltaAngle;
		}

		Vector2DPlain position = new Vector2DPlain(body.getPosition());
		Polar2D deltaPosition = new Polar2D((2 * RANDOM.nextDouble() + 0.5) * body.getSize(), deltaAngle);
		position.add(deltaPosition.toCartesian());
		return position;
	}

	/**
	 * @param body
	 * @return <Velocity,Acceleration>
	 */
	private Pair<Vector2DPlain, Vector2DPlain> calculateMovement(final Body body, final Collection<Body> bodies) {

		Vector2DPlain gVector = new Vector2DPlain();
		Vector2DPlain cVector = new Vector2DPlain();

		for (final Body neighbour : bodies) {

			if (neighbour == body) {
				continue;
			}

			double distance = Vector2DPlain.distance(body.getPosition(), neighbour.getPosition());

			gVector.add(calculateGravitationVector(body, neighbour, distance));

			if (physics.isCollision()) {
				Vector2DPlain collisionVector = calculateCollisionVector(body, neighbour, distance);
				cVector.add(collisionVector);
			}

		}

		if (body.isParticle() && hasTimedOut(body, gVector.length())) {
			body.setDestroyed(true);
			return new Pair<Vector2DPlain, Vector2DPlain>(new Vector2DPlain(), new Vector2DPlain());
		}

		Vector2DPlain velocity = Vector2DPlain.add(body.getVelocity(), cVector);

		if (physics.isUseMelting() && !body.isParticle() && (gVector.length() * body.getDensity()) / body.getMass() > 0.1) {
			if (RANDOM.nextInt(10) % 10 == 0) {
				body.setMelting(true);
				body.setMeltingIntensity((gVector.length() * body.getDensity()) / body.getMass());
			}
		}

		Vector2DPlain force = new Vector2DPlain(gVector);
		Vector2DPlain massCounterVectorForce = new Vector2DPlain();
		Vector2DPlain massCounterVectorVelocity = new Vector2DPlain();

		for (final Body neighbour : bodies) {

			if (neighbour == body) {
				continue;
			}

			if (physics.isCollision() && !neighbour.isParticle()) {
				massCounterVectorForce.add(calculateMassCounterVector(body, neighbour, force));
				massCounterVectorVelocity.add(calculateMassCounterVector(body, neighbour, velocity));
			}

		}

		if (physics.isCollision()) {
			force.add(massCounterVectorForce);
			velocity.add(massCounterVectorVelocity);
		}

		Vector2DPlain acceleration = calculateAccelerationVector(body, force);

		return new Pair<Vector2DPlain, Vector2DPlain>(velocity, acceleration);

	}

	/**
	 * @param a
	 * @param b
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateGravitationVector(Body a, Body b, double distance) {

		if (!physics.isGravitation() || distance == 0) {
			return new Vector2DPlain();
		}

		if (!physics.isParticleGravitation() && (a.isParticle() && b.isParticle())) {
			return new Vector2DPlain();
		}

		double gForce = calculateGravitationalForce(a, b, distance);
		if (gForce < 0) {
			System.out.println("antigravitation between " + a.getId() + " and " + b.getId());
		}
		Vector2DPlain distanceVector = Vector2DPlain.substract(b.getPosition(), a.getPosition());

		// der Distanzvektor wird auf die richtige Länge gebracht
		return Vector2DPlain.mult(Math.signum(gForce) * gForce / distance, distanceVector);

	}

	/**
	 * @param a
	 * @param b
	 * @param distance
	 * @return
	 */
	private double calculateGravitationalForce(Body a, Body b, double distance) {

		/*
		 * bei Körpern, die ineinander verschmelzen, nimmt ihre Gravitaions zu
		 * einander ab.
		 */

		if (distance == 0) {
			return 0;
		}

		double mergingFactor = 1;
		if (distance < a.getSize() + b.getSize()) {
			mergingFactor = distance / (a.getSize() + b.getSize());
		}

		return (mergingFactor * a.getMass() * b.getMass()) / (distance * distance);
	}

	/**
	 * @param body
	 * @param force
	 * @return
	 */
	private Vector2DPlain calculateAccelerationVector(Body body, Vector2DPlain force) {
		return Vector2DPlain.mult(1 / body.getMass(), force);
	}

	/**
	 * @param body
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateCollisionVector(Body body, Body neighbour, double distance) {

		if (distance > body.getSize() + neighbour.getSize()) {

			return new Vector2DPlain();

		} else if (physics.isUseExplosion()) {

			if (body.isParticle()) {

				if (!neighbour.isParticle()) {

					if (!neighbour.isDestroyed()) {
						body.setDestroyed(true);
					}
					return new Vector2DPlain();

				} else {

					if (!physics.isParticleCollision()) {
						return new Vector2DPlain();
					}

				}

			} else if (neighbour.isParticle()) {

				if (!neighbour.isMerged()) {

					double density = body.getDensity();
					body.setMass(body.getMass() + neighbour.getMass());
					body.setDensity(density);
					neighbour.setMerged(true);

				}

				if (!physics.isParticleCollision()) {
					return new Vector2DPlain();
				}
			}
		}

		/*
		 * body -> neighbour
		 */
		Vector2DPlain atobVelocity = calculatePartialVectorAtoB(body, neighbour, body.getVelocity());
		Vector2DPlain btoaVelocity = calculatePartialVectorAtoB(neighbour, body, neighbour.getVelocity());

		Vector2DPlain resultAtobVelocity = new Vector2DPlain(calculateCollisionAmount(body, neighbour, atobVelocity.x, btoaVelocity.x),
				calculateCollisionAmount(body, neighbour, atobVelocity.y, btoaVelocity.y));

		Vector2DPlain deltaVelocityVector = Vector2DPlain.substract(resultAtobVelocity, atobVelocity);
		if (deltaVelocityVector.length() < physics.getThresholdCollision()) {
			return new Vector2DPlain();
		}

		if (physics.isUseExplosion() && (deltaVelocityVector.length() / body.getDensity() > physics.getExplosionThreshold())) {
			body.setDestroyed(true);
		}

		return deltaVelocityVector;

	}

	/**
	 * @param a
	 * @param b
	 * @param va
	 * @param vb
	 * @return
	 */
	private double calculateCollisionAmount(Body a, Body b, double va, double vb) {
		return (a.getMass() * va + b.getMass() * vb - b.getMass() * (va - vb) * physics.getRestitutionCoefficient())
				/ (a.getMass() + b.getMass());
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	private Vector2DPlain calculatePartialVectorAtoB(Body a, Body b, Vector2DPlain vector) {

		if (vector.isNullVector()) {
			return new Vector2DPlain();
		}

		Vector2DPlain dVector = Vector2DPlain.substract(b.getPosition(), a.getPosition());
		if (dVector.isNullVector()) {
			return new Vector2DPlain();
		}

		final double lambda = Vector2DPlain.mult(vector, dVector) / Vector2DPlain.mult(dVector, dVector);
		if (lambda < 0) {
			// a bewegt sich nicht hin zu sondern weg von b
			return new Vector2DPlain();
		}
		Vector2DPlain vectorToB = Vector2DPlain.mult(lambda, dVector);

		return vectorToB;

	}

	/**
	 * @param a
	 * @param b
	 * @param vector
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateMassCounterVector(Body a, Body b, Vector2DPlain vector) {

		if (Vector2DPlain.substract(b.getPosition(), a.getPosition()).length() > a.getSize() + b.getSize()) {
			return new Vector2DPlain();
		}

		return Vector2DPlain.mult(-1, calculatePartialVectorAtoB(a, b, vector));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#getBodies()
	 */
	@Override
	public Collection<Body> getBodies() {
		return new CompositeCollection<Body>().cat(particles).cat(massBodies);
	}

	@Override
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public void shutdown() {

	}
}
