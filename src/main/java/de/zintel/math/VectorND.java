/**
 * 
 */
package de.zintel.math;

import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class VectorND extends AVectorND<VectorND> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6604676797217529689L;

	private final IVectorFactory<VectorND> factory;

	/**
	 * @param dim
	 */
	public VectorND(int dim, IVectorFactory<VectorND> factory) {
		super(dim);
		this.factory = factory;
	}

	public VectorND(AVectorND<VectorND> vector) {
		this(vector.getValues(), vector);
	}

	public VectorND(List<Double> values, IVectorFactory<VectorND> factory) {
		super(values.size(), values);
		this.factory = factory;
	}

	@Override
	public VectorND newVector(VectorND vector) {
		return new VectorND(vector);
	}

	@Override
	public VectorND newVector() {
		return new VectorND(getDim(), factory);
	}

	@Override
	public VectorND newVector(List<Double> values) {
		return new VectorND(values, factory);
	}

}
