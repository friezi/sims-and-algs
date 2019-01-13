/**
 * 
 */
package de.zintel.ci;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.zintel.math.MathUtils;
import de.zintel.math.Vector2DPlain;

/**
 * @author Friedemann
 *
 */
public class Swarm {

	private final Vector2DPlain center;

	private int boidSpeed = 8;

	private int influenceOfSeparation = 6;

	private int influenceOfAlignment = 1;

	private int publicDistance = 50;

	private int personalDistance = 25;

	private int predatorDistance = 60;

	private int leaderAttraction = 600;

	private boolean useCohesion = true;

	private boolean useSeparation = true;

	private boolean useAlignment = true;

	private boolean useLeader = false;

	private boolean usePredator = false;

	private boolean usePanic = true;

	private Collection<Boid> boids = new ArrayList<>();

	private final Object boidsMonitor = new Object();

	public Swarm(Vector2DPlain center) {
		this.center = center;
	}

	public void addBoid(final Boid boid) {
		synchronized (boidsMonitor) {
			boids.add(boid);
		}
	}

	public void removeBoid(final Boid boid) {
		synchronized (boidsMonitor) {
			boids.remove(boid);
		}
	}

	public Collection<Boid> getBoids() {
		return boids;
	}

	/**
	 * 
	 */
	public void swarm() {

		synchronized (boidsMonitor) {
			Collection<Boid> nextBoids = Collections.synchronizedCollection(new ArrayList<>(boids.size()));

			boids.parallelStream().forEach(boid -> {
				final Vector2DPlain attractionVector = calculateAttractor(boid);
				Boid nextBoid = approachPoint(boid, attractionVector);
				nextBoids.add(nextBoid);
			});

			boids = nextBoids;
		}

	}

	/**
	 * @param boid
	 * @param attractionVector
	 * @return
	 */
	private Boid approachPoint(final Boid boid, final Vector2DPlain attractionVector) {

		final Vector2DPlain position = boid.getPosition();

		Vector2DPlain newPosition;
		Vector2DPlain newDirection;

		if (boid.isConvergeAttractor()) {
			if (!(usePredator && boid.getType() == BoidType.PREDATOR)) {

				double length = attractionVector.length();

				if (length > 0) {

					Vector2DPlain normVector = Vector2DPlain.normalize(attractionVector);
					double vFactor = Math.min(length, boidSpeed);
					Vector2DPlain scaledAttractionVector = Vector2DPlain.mult(vFactor, normVector);

					newDirection = scaledAttractionVector;

				} else {

					newDirection = attractionVector;

				}

			} else {
				newDirection = attractionVector;
			}
		} else {
			newDirection = new Vector2DPlain();
		}

		if (boid.getMotioner() != null) {
			newDirection.add(boid.getMotioner().nextMotionVector());
		}

		newPosition = new Vector2DPlain(position).add(newDirection);
		Boid newBoid = new Boid(newPosition, boid.getId()).setType(boid.getType()).setMotioner(boid.getMotioner())
				.setConvergeAttractor(boid.isConvergeAttractor()).setPanic(boid.getPanic());
		newBoid.setPreviousDirection(boid.getDirection());
		newBoid.setDirection(newDirection);

		return newBoid;

	}

	/**
	 * @param boid
	 * @return
	 */
	private Vector2DPlain calculateAttractor(final Boid boid) {

		if (usePredator && boid.getType() == BoidType.PREDATOR) {
			return new Vector2DPlain();
		}

		Vector2DPlain tVector = new Vector2DPlain();

		final Vector2DPlain panic = boid.getPanic();
		if (panic.length() > 1) {
			panic.mult(9 / 10);
		} else {
			boid.setPanic(new Vector2DPlain());
		}

		for (final Boid neighbour : boids) {

			if (neighbour == boid) {
				continue;
			}

			double distance = Vector2DPlain.distance(boid.getPosition(), neighbour.getPosition());

			if (useCohesion && !(useLeader && boid.getType() == BoidType.LEADER)) {
				tVector.add(calculateCohesionVector(boid, neighbour, distance));
			}
			if (useSeparation && !(usePredator && boid.getType() == BoidType.PREDATOR)) {
				tVector.add(calculateSeparationVector(boid, neighbour, distance));
			}
			if (useAlignment && !(useLeader && boid.getType() == BoidType.LEADER)
					&& !(usePredator && boid.getType() == BoidType.PREDATOR)) {
				tVector.add(calculateAlignmentVector(boid, neighbour, distance));
			}

			tVector.add(calculateCenterVector(boid));

		}

		tVector.add(boid.getPanic());

		return tVector;

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateCohesionVector(final Boid boid, final Boid neighbour, final double distance) {

		if (distance < 1) {
			return new Vector2DPlain();
		}

		double factor = publicDistance - distance;
		if (factor < 0) {
			factor = 0;
		}

		if (neighbour.getType() == BoidType.LEADER) {
			factor *= leaderAttraction;
		}

		return Vector2DPlain.mult(factor / distance, Vector2DPlain.substract(neighbour.getPosition(), boid.getPosition()));

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateSeparationVector(final Boid boid, final Boid neighbour, double distance) {

		int individualDist = usePredator && neighbour.getType() == BoidType.PREDATOR ? predatorDistance : personalDistance;

		if (distance > individualDist) {

			return new Vector2DPlain();

		} else {

			double factor = Math.pow(individualDist - distance, influenceOfSeparation);

			if (distance < 1) {
				distance = 1;
			}

			final Vector2DPlain diffVector = Vector2DPlain.substract(boid.getPosition(), neighbour.getPosition());
			final Vector2DPlain separationVector = Vector2DPlain.mult(factor / distance, diffVector);

			if (usePanic) {
				if (usePredator && neighbour.getType() == BoidType.PREDATOR) {

					boid.setPanic(Vector2DPlain.add(boid.getPanic(), separationVector));

				} else {

					final Vector2DPlain panic = neighbour.getPanic();
					final double intensity = panic.length();
					if (intensity > 0) {
						boid.setPanic(Vector2DPlain.mult(intensity, Vector2DPlain.normalize(diffVector)));
					}
				}
			}

			return separationVector;

		}

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateAlignmentVector(final Boid boid, final Boid neighbour, final double distance) {

		if (distance > 2 * personalDistance) {

			return new Vector2DPlain();

		} else {

			if (usePredator && neighbour.getType() == BoidType.PREDATOR) {
				return new Vector2DPlain();
			}

			double coeff = Math.pow(2, influenceOfAlignment) / Math.max(distance, 1.0);
			return Vector2DPlain.mult(coeff, neighbour.getDirection());

		}

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2DPlain calculateCenterVector(final Boid boid) {
		return Vector2DPlain.mult((boid.getType() == BoidType.LEADER ? 3 : 10), Vector2DPlain.substract(center, boid.getPosition()));
	}

	private Point calculateInertnessVector(Point direction) {

		if (MathUtils.distance(new Point(), direction) <= 10) {
			return new Point(-direction.x, -direction.y);
		}

		return new Point();

	}

	public boolean isUseCohesion() {
		return useCohesion;
	}

	public Swarm setUseCohesion(boolean useCohesion) {
		this.useCohesion = useCohesion;
		return this;
	}

	public boolean isUseSeparation() {
		return useSeparation;
	}

	public Swarm setUseSeparation(boolean useSeparation) {
		this.useSeparation = useSeparation;
		return this;
	}

	public boolean isUseAlignment() {
		return useAlignment;
	}

	public Swarm setUseAlignment(boolean useAlignment) {
		this.useAlignment = useAlignment;
		return this;
	}

	public boolean isUseLeader() {
		return useLeader;
	}

	public Swarm setUseLeader(boolean useLeader) {
		this.useLeader = useLeader;
		return this;
	}

	public boolean isUsePredator() {
		return usePredator;
	}

	public Swarm setUsePredator(boolean usePredator) {
		this.usePredator = usePredator;
		return this;
	}

	public int getBoidSpeed() {
		return boidSpeed;
	}

	public Swarm setBoidSpeed(int boidSpeed) {
		this.boidSpeed = boidSpeed;
		return this;
	}

	public int getInfluenceOfSeparation() {
		return influenceOfSeparation;
	}

	public Swarm setInfluenceOfSeparation(int influenceOfSeparation) {
		this.influenceOfSeparation = influenceOfSeparation;
		return this;
	}

	public int getPublicDistance() {
		return publicDistance;
	}

	public Swarm setPublicDistance(int publicDistance) {
		this.publicDistance = publicDistance;
		return this;
	}

	public int getPersonalDistance() {
		return personalDistance;
	}

	public Swarm setPersonalDistance(int personalDistance) {
		this.personalDistance = personalDistance;
		return this;
	}

	public int getPredatorDistance() {
		return predatorDistance;
	}

	public Swarm setPredatorDistance(int predatorDistance) {
		this.predatorDistance = predatorDistance;
		return this;
	}

	public int getLeaderAttraction() {
		return leaderAttraction;
	}

	public Swarm setLeaderAttraction(int leaderAttraction) {
		this.leaderAttraction = leaderAttraction;
		return this;
	}

	public int getInfluenceOfAlignment() {
		return influenceOfAlignment;
	}

	public Swarm setInfluenceOfAlignment(int influenceOfAlignment) {
		this.influenceOfAlignment = influenceOfAlignment;
		return this;
	}

	public boolean isUsePanic() {
		return usePanic;
	}

	public Swarm setUsePanic(boolean usePanic) {
		this.usePanic = usePanic;
		return this;
	}

}
