/**
 * 
 */
package de.zintel.math.matrix;

import java.util.function.BiFunction;

import de.zintel.math.AMatrix;
import de.zintel.math.monoid.Monoid;

/**
 * @author friedemann.zintel
 *
 */
public class MatrixConstructionBundle<T, M extends AMatrix<T, M>> {

	public final IMatrixFactory<T, M> factory;

	public final BiFunction<Integer, Integer, T[][]> fieldconstructor;

	public final Monoid<T> fsum;

	public final Monoid<T> fmult;

	public MatrixConstructionBundle(IMatrixFactory<T, M> factory, BiFunction<Integer, Integer, T[][]> fieldconstructor, Monoid<T> fsum,
			Monoid<T> fmult) {
		this.factory = factory;
		this.fieldconstructor = fieldconstructor;
		this.fsum = fsum;
		this.fmult = fmult;
	}

	/**
	 * @param field
	 * @return
	 */
	public M newMatrix(final T[][] field, int rows, int columns) {
		return factory.newMatrix(this, field, rows, columns);
	}

}
