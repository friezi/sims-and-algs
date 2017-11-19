package de.zintel.physics;

import java.io.Serializable;

import de.zintel.math.Vector2D;

public class BodyParameters implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9049243125444100650L;

	public final String id;

	public double size;

	public double mass;

	public double density;

	public double volume;

	public Vector2D position;

	public Vector2D velocity;

	public boolean particle = false;

	public boolean destroyed = false;

	public boolean merged = false;

	public boolean melting = false;

	public double meltingIntensity = 0;

	public BodyParameters(String id) {
		this.id = id;
	}
}