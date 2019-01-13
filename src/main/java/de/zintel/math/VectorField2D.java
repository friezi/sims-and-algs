/**
 * 
 */
package de.zintel.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class VectorField2D<M extends AVectorND<M>> implements IVectorField<Vector2D, M> {

	private final int vectorDimension;

	private final int width;

	private final int height;

	private final M[][] field;

	public VectorField2D(int vectorDimension, M[][] field) {
		this.vectorDimension = vectorDimension;
		this.width = field.length;
		this.height = field[0].length;

		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.IVectorField#getDimensions()
	 */
	@Override
	public List<Integer> getDimensions() {
		return Arrays.asList(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.IVectorField#getDimensionsCodomain()
	 */
	@Override
	public int getDimensionsCodomain() {
		return vectorDimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.math.IVectorField#getValue(de.zintel.math.VectorND)
	 */
	@Override
	public M getValue(Vector2D pos) {

		int x = pos.get(0).intValue();
		int y = pos.get(1).intValue();
		x = (x >= 0 ? (x < width ? x : width - 1) : 0);
		y = (y >= 0 ? (y < height ? y : height - 1) : 0);

		return field[x][y];
	}

	@Override
	public void setValue(Vector2D pos, M value) {
		field[pos.get(0).intValue()][pos.get(1).intValue()] = value;
	}

	@Override
	public List<M> asList() {

		final List<M> vectors = new ArrayList<>(getDimensions().stream().reduce(1, (v1, v2) -> v1 * v2));
		for (M[] array : field) {
			vectors.addAll(Arrays.asList(array));
		}

		return vectors;
	}

}
