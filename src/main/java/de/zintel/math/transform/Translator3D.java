/**
 * 
 */
package de.zintel.math.transform;

import java.util.function.Function;

import de.zintel.math.Vector3D;
import de.zintel.math.AVectorND;

/**
 * @author friedemann.zintel
 *
 */
public class Translator3D implements Function<Vector3D, Vector3D> {

	private final Vector3D translationvector;

	public Translator3D() {
		this(new Vector3D());
	}

	public Translator3D(Vector3D translationvector) {
		this.translationvector = translationvector;
	}

	@Override
	public Vector3D apply(Vector3D vector) {
		return AVectorND.add(vector, translationvector);
	}

	public void add(final Vector3D vector) {
		translationvector.add(vector);
	}

	public Translator3D getInvertedTranslator() {
		return new Translator3D(Vector3D.mult(-1, translationvector));
	}

}
