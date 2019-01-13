/**
 * 
 */
package de.zintel.physics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.zintel.math.Vector2DPlain;
import de.zintel.sim.nbodies.BodyProperty;

/**
 * @author Friedemann
 *
 */
public class Body implements Comparable<Body>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4862332522510864345L;

	private BodyParameters parameters;

	private Map<String, BodyProperty> properties = new HashMap<>();

	public Body(BodyParameters parameters) {
		super();
		this.parameters = parameters;
	}

	public Body(String id, double size, double mass, Vector2DPlain position) {
		this(id, size, mass, position, new Vector2DPlain());
	}

	public Body(String id, double size, double mass, Vector2DPlain position, Vector2DPlain velocity, Body propertiesBody) {
		this(id, size, mass, position, velocity);
		this.copyProperties(propertiesBody);
	}

	public Body(String id, double size, double mass, Vector2DPlain position, Vector2DPlain velocity) {
		this(new BodyParameters(id));
		parameters.size = size;
		parameters.mass = mass;
		parameters.position = position;
		parameters.velocity = velocity;
		adjustVolume();
		adjustDensity();
	}

	private void adjustDensity() {
		parameters.density = parameters.mass / parameters.volume;
	}

	private void adjustVolume() {
		parameters.volume = Math.PI * parameters.size * parameters.size;
	}

	private void adjustSize() {
		parameters.size = Math.sqrt(parameters.mass / (Math.PI * parameters.density));
	}

	public double getSize() {
		return parameters.size;
	}

	public double getMass() {
		return parameters.mass;
	}

	public Vector2DPlain getVelocity() {
		return parameters.velocity;
	}

	public void setVelocity(Vector2DPlain velocity) {
		parameters.velocity = velocity;
	}

	public Vector2DPlain getPosition() {
		return parameters.position;
	}

	public void setPosition(Vector2DPlain position) {
		parameters.position = position;
	}

	public String getId() {
		return parameters.id;
	}

	public void setSize(double size) {
		parameters.size = size;
		adjustVolume();
		adjustDensity();
	}

	public void setMass(double mass) {
		parameters.mass = mass;
		adjustDensity();
	}

	public boolean isParticle() {
		return parameters.particle;
	}

	public void setParticle(boolean particle) {
		parameters.particle = particle;
	}

	public boolean isDestroyed() {
		return parameters.destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		parameters.destroyed = destroyed;
	}

	public boolean isMerged() {
		return parameters.merged;
	}

	public void setMerged(boolean merged) {
		parameters.merged = merged;
	}

	@Override
	public int compareTo(Body o) {
		return parameters.id.compareTo(o.getId());
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
		return parameters.density;
	}

	public void setDensity(double density) {
		parameters.density = density;
		adjustSize();
	}

	@Override
	public String toString() {
		return "Body [id=" + parameters.id + ", size=" + parameters.size + ", mass=" + parameters.mass + ", density=" + parameters.density
				+ ", position=" + parameters.position + ", velocity=" + parameters.velocity + ", particle=" + parameters.particle
				+ ", destroyed=" + parameters.destroyed + ", merged=" + parameters.merged + ", properties=" + properties + "]";
	}

	public boolean isMelting() {
		return parameters.melting;
	}

	public void setMelting(boolean melting) {
		parameters.melting = melting;
	}

	public double getMeltingIntensity() {
		return parameters.meltingIntensity;
	}

	public void setMeltingIntensity(double meltingIntensity) {
		parameters.meltingIntensity = meltingIntensity;
	}

	public BodyParameters getParameters() {
		return parameters;
	}

	public void setParameters(BodyParameters parameters) {
		this.parameters = parameters;
	}

}
