/**
 * 
 */
package de.zintel.ci;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.zintel.gfx.g2d.Vector2D;
import de.zintel.math.Utils;

/**
 * @author Friedemann
 *
 */
public class Swarm {

	private final Vector2D center;

	private final double centerLength;

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

	private Collection<Boid> boids = new ArrayList<>();

	private final Object boidsMonitor = new Object();

	public Swarm(Vector2D center) {
		this.center = center;
		this.centerLength = center.length();
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
				final Vector2D attractionVector = calculateAttractor(boid);
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
	private Boid approachPoint(final Boid boid, final Vector2D attractionVector) {

		final Vector2D position = boid.getPosition();

		Vector2D newPosition;
		Vector2D resultVector;

		if (!(usePredator && boid.isPredator())) {

			double length = attractionVector.length();

			if (length > 0) {

				Vector2D normVector = Vector2D.normalize(attractionVector);
				double vFactor = Math.min(length, boidSpeed);
				Vector2D scaledAttractionVector = new Vector2D(normVector.x * vFactor, normVector.y * vFactor);

				resultVector = scaledAttractionVector;

			} else {

				resultVector = attractionVector;

			}

		} else {
			resultVector = attractionVector;
		}

		if (boid.getMotioner() != null) {
			resultVector.add(boid.getMotioner().nextMotionVector());
		}

		newPosition = new Vector2D(position).add(resultVector);
		Boid nextBoid = new Boid(newPosition, boid.getId()).setLeader(boid.isLeader()).setPredator(boid.isPredator())
				.setMotioner(boid.getMotioner());
		nextBoid.setDirection(resultVector);

		return nextBoid;

	}

	/**
	 * @param boid
	 * @return
	 */
	private Vector2D calculateAttractor(final Boid boid) {

		if (usePredator && boid.isPredator()) {
			return new Vector2D();
		}

		Vector2D tVector = new Vector2D();

		final Collection<Vector2D> vectors = new ArrayList<>(3);

		for (final Boid neighbour : boids) {

			if (neighbour == boid) {
				continue;
			}

			vectors.clear();

			double distance = Vector2D.distance(boid.getPosition(), neighbour.getPosition());

			if (useCohesion && !(useLeader && boid.isLeader())) {
				vectors.add(calculateCohesionVector(boid, neighbour, distance));
			}
			if (useSeparation) {
				vectors.add(calculateSeparationVector(boid, neighbour, distance));
			}
			if (useAlignment && !(useLeader && boid.isLeader())) {
				vectors.add(calculateAlignmentVector(boid, neighbour, distance));
			}

			vectors.add(calculateCenterVector(boid, neighbour, distance));

			for (final Vector2D vector : vectors) {
				tVector.add(vector);
			}
		}

		return tVector;

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2D calculateCohesionVector(final Boid boid, final Boid neighbour, final double distance) {

		if (distance < 1) {
			return new Vector2D();
		}

		double factor = publicDistance - distance;
		if (factor < 0) {
			factor = 0;
		}

		if (neighbour.isLeader()) {
			factor *= leaderAttraction;
		}

		double dX = neighbour.getPosition().x - boid.getPosition().x;
		double dY = neighbour.getPosition().y - boid.getPosition().y;

		return new Vector2D((factor * dX) / distance, (factor * dY) / distance);

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2D calculateSeparationVector(final Boid boid, final Boid neighbour, double distance) {

		int individualDist = usePredator && neighbour.isPredator() ? predatorDistance : personalDistance;

		if (distance > individualDist) {

			return new Vector2D();

		} else {

			double factor = Math.pow(individualDist - distance, influenceOfSeparation);

			double fX = boid.getPosition().x - neighbour.getPosition().x;
			double fY = boid.getPosition().y - neighbour.getPosition().y;

			if (distance < 1) {
				distance = 1;
			}

			return new Vector2D((factor * fX) / distance, (factor * fY) / distance);

		}

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2D calculateAlignmentVector(final Boid boid, final Boid neighbour, final double distance) {

		if (distance > 2 * personalDistance) {

			return new Vector2D();

		} else {

			if (usePredator && neighbour.isPredator()) {
				return new Vector2D();
			}

			double coeff = Math.pow(2, influenceOfAlignment) / Math.max(distance, 1.0);
			return new Vector2D(coeff * neighbour.getDirection().x, coeff * neighbour.getDirection().y);

		}

	}

	/**
	 * @param boid
	 * @param neighbour
	 * @param distance
	 * @return
	 */
	private Vector2D calculateCenterVector(final Boid boid, final Boid neighbour, double distance) {

		final Vector2D centerAttractorVector = Vector2D.substract(center, boid.getPosition());

		if (useLeader && boid.isLeader()) {
			// leaders werden zur Mitte streben
			return centerAttractorVector;
		}

		double centerFactor = 0;
		if (neighbour.isLeader() && publicDistance - distance < 0) {
			// boids ohne Anführer sollen zur Mitte streben
			double power = 2 * distance / centerLength;
			centerFactor = Math.pow(distance, power);
		}

		final Vector2D normalizedCenterAttractor = Vector2D.normalize(centerAttractorVector);

		return new Vector2D(centerFactor * normalizedCenterAttractor.x, centerFactor * normalizedCenterAttractor.y);

	}

	private Point calculateInertnessVector(Point direction) {

		if (Utils.distance(new Point(), direction) <= 10) {
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

}
