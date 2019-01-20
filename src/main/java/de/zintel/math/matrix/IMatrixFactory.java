/**
 * 
 */
package de.zintel.math.matrix;

/**
 * @author friedemann.zintel
 *
 */
@FunctionalInterface
public interface IMatrixFactory<T, M extends AMatrix<T, M>> {
	M newMatrix(MatrixConstructionBundle<T, M> constructionBundle, T[][] field, int rows, int columns);
}
