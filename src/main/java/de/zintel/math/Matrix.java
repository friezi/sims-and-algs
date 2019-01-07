/**
 * 
 */
package de.zintel.math;

import java.util.List;

/**
 * @author friedemann.zintel
 *
 */
public class Matrix {

	public static enum Order {
		ROWS, COLUMNS
	}

	private final double matrix[][];

	private final int rows;

	private final int columns;

	/**
	 * 
	 */
	public Matrix(List<VectorND> vectors, Order order) {

		Integer vdim = null;
		for (VectorND vector : vectors) {
			if (vdim == null) {
				vdim = vector.getDim();
			} else {
				if (vector.getDim() != vdim) {
					throw new RuntimeException("vectors differ in dimension!");
				}
			}
		}

		if (order == Order.ROWS) {

			rows = vectors.size();
			columns = vdim;
			matrix = new double[rows][columns];
			for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					matrix[row][column] = vectors.get(row).get(column);
				}
			}
		} else {
			throw new RuntimeException("order " + Order.COLUMNS + " not yet supported!");
		}
	}

	private Matrix(double[][] matrix, int rows, int columns) {

		this.matrix = matrix;
		this.rows = rows;
		this.columns = columns;

	}

	public double get(int i, int j) {
		return matrix[i][j];
	}

	public Matrix transpose() {

		final double[][] tmatrix = new double[columns][rows];
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				tmatrix[column][row] = matrix[row][column];
			}
		}

		return new Matrix(tmatrix, columns, rows);

	}

	public static Matrix mmult(Matrix m1, Matrix m2) {

		assertProp(m1.getColumns() == m2.getRows());
		
		final double[][] nmatrix = new double[m1.getRows()][m2.getColumns()];
		for (int m = 0; m < m1.getRows(); m++) {
			for (int n = 0; n < m2.getColumns(); n++) {

				double sum = 0;
				for (int k = 0; k < m1.getColumns(); k++) {
					sum += m1.get(m, k) * m2.get(k, n);
				}

				nmatrix[m][n] = sum;
			}
		}

		return new Matrix(nmatrix, m1.getRows(), m2.getColumns());

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

		for (int row = 0; row < rows; row++) {
			boolean first = true;
			for (int column = 0; column < columns; column++) {
				if (!first) {
					sbuffer.append("\t\t\t|\t\t\t");
				} else {
					first = false;
				}
				sbuffer.append(matrix[row][column]);
			}

			sbuffer.append("\n");
		}

		return sbuffer.toString();
	}

}
