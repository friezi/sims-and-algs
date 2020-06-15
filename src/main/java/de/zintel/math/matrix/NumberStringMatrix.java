package de.zintel.math.matrix;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.zintel.math.monoid.MonoidTextNumberMult;
import de.zintel.math.monoid.MonoidTextNumberSum;

public class NumberStringMatrix extends AMatrix<String, NumberStringMatrix> {

	private static final MatrixConstructionBundle<String, NumberStringMatrix> CONSTRUCTION_BUNDLE = new MatrixConstructionBundle<>(
			(bundle, field, rows, columns) -> new NumberStringMatrix(bundle, field, rows, columns),
			(rows, columns) -> new String[rows][columns], new MonoidTextNumberSum(), new MonoidTextNumberMult());

	private static final Function<List<String>, Integer> amounter = List::size;

	private static final BiFunction<List<String>, Integer, String> selector = (vector, column) -> vector.get(column);

	protected NumberStringMatrix(MatrixConstructionBundle<String, NumberStringMatrix> constructionBundle, String[][] field, int rows,
			int columns) {
		super(constructionBundle, field, rows, columns);
	}

	/**
	 * 
	 */
	public NumberStringMatrix(List<List<String>> vectors, Order order) {
		super(vectors, CONSTRUCTION_BUNDLE, amounter, selector, order);
	}

}
