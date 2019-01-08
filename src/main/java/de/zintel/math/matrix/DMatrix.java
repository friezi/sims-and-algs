/**
 * 
 */
package de.zintel.math.matrix;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.zintel.math.AMatrix;
import de.zintel.math.VectorND;
import de.zintel.math.AMatrix.Order;
import de.zintel.math.monoid.MonoidMultDouble;
import de.zintel.math.monoid.MonoidSumDouble;

/**
 * @author friedemann.zintel
 *
 */
public class DMatrix extends AMatrix<Double, DMatrix> {

	private static final MatrixConstructionBundle<Double, DMatrix> CONSTRUCTION_BUNDLE = new MatrixConstructionBundle<>(
			(bundle, field, rows, columns) -> new DMatrix(bundle, field, rows, columns), (rows, columns) -> new Double[rows][columns],
			new MonoidSumDouble(), new MonoidMultDouble());

	private static final Function<VectorND, Integer> amounter = VectorND::getDim;

	private static final BiFunction<VectorND, Integer, Double> selector = (vector, column) -> vector.get(column);

	protected DMatrix(MatrixConstructionBundle<Double, DMatrix> constructionBundle, Double[][] field, int rows, int columns) {
		super(constructionBundle, field, rows, columns);
	}

	/**
	 * 
	 */
	public DMatrix(List<VectorND> vectors, Order order) {
		super(vectors, CONSTRUCTION_BUNDLE, amounter, selector, order);
	}

}
