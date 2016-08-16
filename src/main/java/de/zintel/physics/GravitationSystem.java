/**
 * 
 */
package de.zintel.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;

import de.zintel.gfx.g2d.Field;
import de.zintel.gfx.g2d.Polar;
import de.zintel.gfx.g2d.Vector2D;
import de.zintel.sim.nbodies.Physics;
import de.zintel.utils.Pair;
import de.zintel.utils.SequenceCollection;

/**
 * @author Friedemann
 *
 */
public class GravitationSystem {

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
	public GravitationSystem addBody(final Body body) {
		massBodies.add(body);
		return this;
	}

	/**
	 * 
	 */
	public void gravitate() {

		Collection<Body> nextMassBodies = Collections.synchronizedCollection(new TreeSet<>());
		Collection<Body> nextParticles = Collections.synchronizedCollection(new TreeSet<>());

		massBodies.parallelStream().forEach(body -> {

			final SequenceCollection<Body> bodies = new SequenceCollection<Body>().cat(massBodies);

			if (physics.isParticleInfluence()) {
				bodies.cat(particles);
			}

			Pair<Vector2D, Vector2D> direction = calculateMovement(body, bodies);
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

			final SequenceCollection<Body> bodies = new SequenceCollection<Body>().cat(massBodies);

			if (physics.isParticleOnParticle()) {
				bodies.cat(particles);
			}

			Pair<Vector2D, Vector2D> direction = calculateMovement(body, bodies);
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
	private Collection<Body> calculateNewPosition(final Body body, Vector2D velocity, Vector2D acceleration) {

		double size = body.getSize();
		double offset = size;
		Vector2D position = new Vector2D(body.getPosition());
		acceleration.mult(physics.getAccelerationFactor());
		double decay = physics.getDecay();
		velocity.add(acceleration);
		position.add(velocity);

		// Reflexion vom Rand
		if (position.x - offset < field.minX || position.x + offset > field.maxX || position.y - offset < field.minY
				|| position.y + offset > field.maxY) {
			if (position.x - offset < field.minX) {
				velocity = new Vector2D(-velocity.x / decay, velocity.y);
				position.x = 2 * offset - (position.x - field.minX) - (decay - 1) * (offset - (position.x - field.minX)) / decay
						+ field.minX;
			} else if (position.x + offset > field.maxX) {
				velocity = new Vector2D(-velocity.x / decay, velocity.y);
				position.x = 2 * (field.maxX - offset) - position.x + (decay - 1) * (offset + position.x - field.maxX) / decay;
			}
			if (position.y - offset < field.minY) {
				velocity = new Vector2D(velocity.x, -velocity.y / decay);
				position.y = 2 * offset - (position.y - field.minY) - (decay - 1) * (offset - (position.y - field.minY)) / decay
						+ field.minY;
			} else if (position.y + offset > field.maxY) {
				velocity = new Vector2D(velocity.x, -velocity.y / decay);
				position.y = 2 * (field.maxY - offset) - position.y + (decay - 1) * (offset + position.y - field.maxY) / decay;
			}

		}

		Body newBody = new Body(body.getId(), size, body.getMass(), position, velocity, body);
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

	/**
	 * @param body
	 * @return
	 */
	private Collection<Body> createExplosionParticles(Body body) {

		int amountParticles = physics.getParticleMass() > (int) body.getMass() ? 1 : (int) body.getMass() / physics.getParticleMass();

		Collection<Body> particles = new ArrayList<>(amountParticles);

		double mass = body.getMass() / amountParticles;
		double size = calculateSize(mass);
		for (int i = 0; i < amountParticles; i++) {

			Vector2D position = createExplosionParticlePosition(body);

			Vector2D velocity;
			if (body.getVelocity().length() == 0) {
				velocity = new Vector2D();
			} else {
				velocity = createExplosionParticleVelocity(body);
			}

			Body particle = new Body("particle:" + body.getId() + ":" + i, size, mass, position, velocity, body);
			particle.setParticle(true);
			particles.add(particle);
		}

		return particles;

	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2D createExplosionParticleVelocity(Body body) {

		double deltaAngle = RANDOM.nextInt(91) - 45;
		Polar newVelocity = body.getVelocity().toPolar();
		newVelocity.angle += (deltaAngle * Math.PI) / 180;
		newVelocity.radius /= physics.getParticleVelocityReduction();

		return newVelocity.toCartesian();
	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2D createExplosionParticlePosition(Body body) {

		Vector2D position = new Vector2D(body.getPosition());
		Polar deltaPosition = new Polar((2 * RANDOM.nextDouble() + 0.5) * body.getSize(), RANDOM.nextDouble() * 2 * Math.PI);
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
		double size = calculateSize(mass);

		Body particle = new Body("meltingparticle:" + body.getId(), size, mass, createMeltingParticlePosition(body),
				new Vector2D(body.getVelocity()), body);
		particle.setParticle(true);
		particles.add(particle);

		return particles;

	}

	/**
	 * @param body
	 * @return
	 */
	private Vector2D createMeltingParticlePosition(Body body) {

		final int max = 4;
		final double xVal = RANDOM.nextDouble() * max - max / 2;
		final double yVal = 0.5 * xVal * xVal;
		double deltaAngle = Math.PI * yVal / max;
		if (RANDOM.nextInt(2) == 1) {
			deltaAngle = -deltaAngle;
		}

		Vector2D position = new Vector2D(body.getPosition());
		Polar deltaPosition = new Polar((2 * RANDOM.nextDouble() + 0.5) * body.getSize(), deltaAngle);
		position.add(deltaPosition.toCartesian());
		return position;
	}

	/**
	 * @param mass
	 * @return
	 */
	public static double calculateSize(double mass) {
		return Math.sqrt(mass / Math.PI);
	}

	/**
	 * @param body
	 * @return <Velocity,Acceleration>
	 */
	private Pair<Vector2D, Vector2D> calculateMovement(final Body body, final Collection<Body> bodies) {

		Vector2D gVector = new Vector2D();
		Vector2D cVector = new Vector2D();

		for (final Body neighbour : bodies) {

			if (neighbour == body) {
				continue;
			}

			double distance = Vector2D.distance(body.getPosition(), neighbour.getPosition());

			gVector.add(calculateGravitationVector(body, neighbour, distance));

			if (physics.isCollision()) {
				Vector2D collisionVector = calculateCollisionVector(body, neighbour, distance);
				cVector.add(collisionVector);
			}

		}

		Vector2D velocity = Vector2D.add(body.getVelocity(), cVector);

		if (physics.isUseMelting() && !body.isParticle() && (gVector.length() * body.getDensity()) / body.getMass() > 0.1) {
			if (RANDOM.nextInt(10) % 10 == 0) {
				body.setMelting(true);
				body.setMeltingIntensity((gVector.length() * body.getDensity()) / body.getMass());
			}
		}

		Vector2D force = new Vector2D(gVector);
		Vector2D massCounterVectorForce = new Vector2D();
		Vector2D massCounterVectorVelocity = new Vector2D();

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

		Vector2D acceleration = calculateAccelerationVector(body, force);

		return new Pair<Vector2D, Vector2D>(velocity, acceleration);

	}

	/**
	 * @param a
	 * @param b
	 * @param distance
	 * @return
	 */
	private Vector2D calculateGravitationVector(Body a, Body b, double distance) {

		if (!physics.isGravitation() || distance == 0) {
			return new Vector2D();
		}

		if (!physics.isParticleGravitation() && (a.isParticle() && b.isParticle())) {
			return new Vector2D();
		}

		double gForce = calculateGravitationalForce(a, b, distance);
		if (gForce < 0) {
			System.out.println("antigravitation between " + a.getId() + " and " + b.getId());
		}
		Vector2D distanceVector = calculateDistanceVector(a, b);

		// der Distanzvektor wird auf die richtige Länge gebracht
		return new Vector2D(Math.signum(gForce) * (gForce * distanceVector.x) / distance,
				Math.signum(gForce) * (gForce * distanceVector.y) / distance);

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
	private Vector2D calculateAccelerationVector(Body body, Vector2D force) {

		double forceAmount = force.length();
		if (forceAmount == 0.0) {
			return new Vector2D();
		}
		double acceleration = forceAmount / body.getMass();

		return new Vector2D((force.x * acceleration) / forceAmount, (force.y * acceleration) / forceAmount);
	}

	/**
	 * @param body
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2D calculateCollisionVector(Body body, Body neighbour, double distance) {

		if (distance > body.getSize() + neighbour.getSize()) {

			return new Vector2D();

		} else if (physics.isUseExplosion()) {

			if (body.isParticle()) {

				if (!neighbour.isParticle()) {

					if (!neighbour.isDestroyed()) {
						body.setDestroyed(true);
					}
					return new Vector2D();

				} else {

					if (!physics.isParticleCollision()) {
						return new Vector2D();
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
					return new Vector2D();
				}
			}
		}

		/*
		 * body -> neighbour
		 */
		Vector2D atobVelocity = calculatePartialVectorAtoB(body, neighbour, body.getVelocity(), distance);
		Vector2D btoaVelocity = calculatePartialVectorAtoB(neighbour, body, neighbour.getVelocity(), distance);

		Vector2D resultAtobVelocity = new Vector2D(calculateCollisionAmount(body, neighbour, atobVelocity.x, btoaVelocity.x),
				calculateCollisionAmount(body, neighbour, atobVelocity.y, btoaVelocity.y));

		Vector2D deltaVelocityVector = new Vector2D(resultAtobVelocity.x - atobVelocity.x, resultAtobVelocity.y - atobVelocity.y);
		if (deltaVelocityVector.length() < physics.getThresholdCollision()) {
			return new Vector2D();
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
	private Vector2D calculatePartialVectorAtoB(Body a, Body b, Vector2D vector, double distance) {

		Vector2D dVector = calculateDistanceVector(a, b);
		if (dVector.x == 0.0 && dVector.y == 0.0) {
			return new Vector2D();
		}

		if (vector.x == 0.0 && vector.y == 0.0) {
			return new Vector2D();
		}

		double dV2 = dVector.x * dVector.x + dVector.y * dVector.y;
		double skalarVDv = vector.x * dVector.x + vector.y * dVector.y;
		Vector2D vectorToB = new Vector2D((dVector.x * skalarVDv) / dV2, (dVector.y * skalarVDv) / dV2);

		if (Vector2D.add(dVector, vectorToB).length() <= distance) {
			return new Vector2D();
		}

		return vectorToB;

	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	private Vector2D calculateDistanceVector(Body a, Body b) {
		return new Vector2D(b.getPosition().x - a.getPosition().x, b.getPosition().y - a.getPosition().y);
	}

	/**
	 * @param a
	 * @param b
	 * @param vector
	 * @param distance
	 * @return
	 */
	private Vector2D calculateMassCounterVector(Body a, Body b, Vector2D vector) {

		Vector2D dVector = calculateDistanceVector(a, b);
		double distance = dVector.length();

		if (distance > a.getSize() + b.getSize()) {
			return new Vector2D();
		}
		Vector2D vectorAtoB = calculatePartialVectorAtoB(a, b, vector, distance);

		if (Vector2D.add(dVector, vectorAtoB).length() <= distance) {
			return new Vector2D();
		}

		return new Vector2D(-vectorAtoB.x, -vectorAtoB.y);

	}

	public Collection<Body> getBodies() {
		return new SequenceCollection<Body>().cat(particles).cat(massBodies);
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
