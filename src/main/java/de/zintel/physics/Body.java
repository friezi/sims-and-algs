/**
 * 
 */
package de.zintel.physics;

import java.util.HashMap;
import java.util.Map;

import de.zintel.gfx.g2d.Vector2D;
import de.zintel.sim.nbodies.BodyProperty;

/**
 * @author Friedemann
 *
 */
public class Body implements Comparable<Body> {

	private final String id;

	private double size;

	private double mass;

	private double density;

	private double volume;

	private Vector2D position;

	private Vector2D velocity;

	private boolean particle = false;

	private boolean destroyed = false;

	private boolean merged = false;

	private boolean melting = false;

	private double meltingIntensity = 0;

	private Map<String, BodyProperty> properties = new HashMap<>();

	public Body(String id, double size, double mass, Vector2D position) {
		this(id, size, mass, position, new Vector2D());
	}

	public Body(String id, double size, double mass, Vector2D position, Vector2D velocity, Body propertiesBody) {
		this(id, size, mass, position, velocity);
		this.copyProperties(propertiesBody);
	}

	public Body(String id, double size, double mass, Vector2D position, Vector2D velocity) {
		super();
		this.id = id;
		this.size = size;
		this.mass = mass;
		this.position = position;
		this.velocity = velocity;
		adjustVolume();
		adjustDensity();
	}

	private void adjustDensity() {
		density = mass / volume;
	}

	private void adjustVolume() {
		volume = Math.PI * size * size;
	}

	private void adjustSize() {
		size = Math.sqrt(mass / (Math.PI * density));
	}

	public double getSize() {
		return size;
	}

	public double getMass() {
		return mass;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public void setSize(double size) {
		this.size = size;
		adjustVolume();
		adjustDensity();
	}

	public void setMass(double mass) {
		this.mass = mass;
		adjustDensity();
	}

	public boolean isParticle() {
		return particle;
	}

	public void setParticle(boolean particle) {
		this.particle = particle;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public boolean isMerged() {
		return merged;
	}

	public void setMerged(boolean merged) {
		this.merged = merged;
	}

	@Override
	public int compareTo(Body o) {
		return id.compareTo(o.getId());
	}

	public BodyProperty getProperty(final String key) {
		return properties.get(key);
	}

	public void setProperty(String key, BodyProperty property) {
		properties.put(key, property);
	}

	public void copyProperties(Body body) {

		for (Map.Entry<String, BodyProperty> entry : body.getProperties().entrySet()) {

			final BodyProperty property = entry.getValue();
			final BodyProperty newPropertyInstance = property.newInstance();
			property.initialiseInstance(newPropertyInstance);
			setProperty(entry.getKey(), newPropertyInstance);
		}
	}

	public Map<String, BodyProperty> getProperties() {
		return properties;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
		adjustSize();
	}

	@Override
	public String toString() {
		return "Body [id=" + id + ", size=" + size + ", mass=" + mass + ", density=" + density + ", position=" + position + ", velocity="
				+ velocity + ", particle=" + particle + ", destroyed=" + destroyed + ", merged=" + merged + ", properties=" + properties
				+ "]";
	}

	public boolean isMelting() {
		return melting;
	}

	public void setMelting(boolean melting) {
		this.melting = melting;
	}

	public double getMeltingIntensity() {
		return meltingIntensity;
	}

	public void setMeltingIntensity(double meltingIntensity) {
		this.meltingIntensity = meltingIntensity;
	}

}
