/**
 * 
 */
package de.zintel.math.matrix;

import java.util.List;

import de.zintel.math.AMatrix;
import de.zintel.math.AVectorND;
import de.zintel.math.monoid.MonoidMultDouble;
import de.zintel.math.monoid.MonoidSumDouble;

/**
 * @author friedemann.zintel
 *
 */
public class DMatrix<T extends AVectorND<T>> extends AMatrix<Double, DMatrix<T>> {

	protected DMatrix(MatrixConstructionBundle<Double, DMatrix<T>> constructionBundle, Double[][] field, int rows, int columns) {
		super(constructionBundle, field, rows, columns);
	}

	/**
	 * 
	 */
	public DMatrix(List<AVectorND<T>> vectors, Order order) {
		super(vectors,
				new MatrixConstructionBundle<>((bundle, field, rows, columns) -> new DMatrix<T>(bundle, field, rows, columns),
						(rows, columns) -> new Double[rows][columns], new MonoidSumDouble(), new MonoidMultDouble()),
				AVectorND::getDim, (vector, column) -> vector.get(column), order);
	}

}
