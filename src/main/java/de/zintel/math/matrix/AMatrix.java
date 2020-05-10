/**
 * 
 */
package de.zintel.math.matrix;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * very general definition of a matrix.
 * 
 * @author friedemann.zintel
 *
 */
public abstract class AMatrix<T, M extends AMatrix<T, M>> {

	public static enum Order {
		ROWS, COLUMNS
	}

	protected MatrixConstructionBundle<T, M> constructionBundle;

	protected T field[][];

	protected int rows;

	protected int columns;

	protected AMatrix(MatrixConstructionBundle<T, M> constructionBundle, T[][] field, int rows, int columns) {
		this.constructionBundle = constructionBundle;
		this.field = field;
		this.rows = rows;
		this.columns = columns;
	}

	/**
	 * 
	 */
	public <V> AMatrix(List<V> vectors, MatrixConstructionBundle<T, M> constructionBundle, Function<V, Integer> amounter,
			BiFunction<V, Integer, T> selector, Order order) {

		this.constructionBundle = constructionBundle;

		Integer vdim = null;
		for (V vector : vectors) {

			if (vdim == null) {
				vdim = amounter.apply(vector);
			} else {
				if (amounter.apply(vector) != vdim) {
					throw new RuntimeException("vectors differ in dimension!");
				}
			}
		}

		if (order == Order.ROWS) {

			rows = vectors.size();
			columns = vdim;
			field = constructionBundle.fieldconstructor.apply(rows, columns);
			for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					field[row][column] = selector.apply(vectors.get(row), column);
				}
			}
		} else {
			throw new RuntimeException("order " + Order.COLUMNS + " not yet supported!");
		}
	}

	public T get(int i, int j) {
		return field[i][j];
	}

	public M copy() {

		final T[][] tfield = constructionBundle.fieldconstructor.apply(columns, rows);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				tfield[row][column] = field[row][column];
			}
		}

		return constructionBundle.newMatrix(tfield, rows, columns);

	}

	public M transpose() {

		final T[][] tfield = constructionBundle.fieldconstructor.apply(columns, rows);
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				tfield[column][row] = field[row][column];
			}
		}

		return constructionBundle.newMatrix(tfield, columns, rows);

	}

	public static <T, M extends AMatrix<T, M>> M mmult(M m1, M m2) {
		return mmult(m1, m2, m1.constructionBundle, m1.constructionBundle.fmult);
	}

	/**
	 * very abstract Matrix-multiplication, where the actuak mukltiplication and
	 * summation are abstract. Combination of three different Matrix-types is
	 * possible: M1*M2->M3.
	 * 
	 * @param m1
	 * @param m2
	 * @param constructionBundle
	 *            for constructing M3
	 * @param fmult
	 *            for applying M1*M2->M3
	 * @return M3
	 */
	public static <T1, M1 extends AMatrix<T1, M1>, T2, M2 extends AMatrix<T2, M2>, T3, M3 extends AMatrix<T3, M3>> M3 mmult(M1 m1, M2 m2,
			MatrixConstructionBundle<T3, M3> constructionBundle, BiFunction<T1, T2, T3> fmult) {

		assertProp(m1.getColumns() == m2.getRows());

		final T3[][] nfield = constructionBundle.fieldconstructor.apply(m1.getRows(), m2.getColumns());
		for (int m = 0; m < m1.getRows(); m++) {
			for (int n = 0; n < m2.getColumns(); n++) {

				T3 sum = constructionBundle.fsum.neutral();
				for (int k = 0; k < m1.getColumns(); k++) {
					sum = constructionBundle.fsum.apply(sum, fmult.apply(m1.get(m, k), m2.get(k, n)));
				}

				nfield[m][n] = sum;
			}
		}

		return constructionBundle.newMatrix(nfield, m1.getRows(), m2.getColumns());

	}

	private static void assertProp(final boolean value) {
		if (!value) {
			throw new IllegalArgumentException("not matching");
		}
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	@Override
	public String toString() {

		final StringBuilder sbuffer = new StringBuilder();

		final int[] columnwidths = new int[columns];
		for (int column = 0; column < columns; column++) {

			int max = 0;
			for (int row = 0; row < rows; row++) {
				max = Math.max(max, field[row][column].toString().length());
			}

			columnwidths[column] = max;
		}

		for (int row = 0; row < rows; row++) {
			boolean first = true;
			int previousLength = 0;
			for (int column = 0; column < columns; column++) {
				if (!first) {
					sbuffer.append(generateSpace(columnwidths[column - 1] - previousLength + 1) + "| ");
				} else {
					first = false;
				}
				final String value = field[row][column].toString();
				previousLength = value.length();
				sbuffer.append(value);
			}

			sbuffer.append("\n");
		}

		return sbuffer.toString();
	}

	private String generateSpace(final int size) {

		StringBuilder sbuffer = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sbuffer.append(" ");
		}

		return sbuffer.toString();
	}

}
