package de.zintel.physics;

import java.io.Serializable;

import de.zintel.math.Vector2DPlain;

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

	public Vector2DPlain position;

	public Vector2DPlain velocity;

	public boolean particle = false;

	public boolean destroyed = false;

	public boolean merged = false;

	public boolean melting = false;

	public long timestamp = 0;

	public long timeout = 0;

	public double meltingIntensity = 0;

	public BodyParameters(String id) {
		this.id = id;
	}
}