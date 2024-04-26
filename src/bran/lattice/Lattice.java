package bran.lattice;

import bran.lattice.matrix.ExpressionMatrix;
import bran.lattice.vector.Vector;
import bran.lattice.vector.VectorSet;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Constant;

import java.util.List;
/**
 * IDK what to call this
 * S - Self Class
 */
public interface Lattice<S extends Lattice<S>> {

	// these can convert between each other
	SystemOfEquations toSystemOfEquations(List<Expression> variables);
	VectorSet toVectorSet();
	ExpressionMatrix toMatrix();

	/** @return number of rows */
	int height();
	/** @return number of columns */
	int width();

	Vector getColumn(int col);

	// /*static*/
	// S newIdentity(int size);
	// S newZero(int size);

	/** includes Zero element*/
	boolean hasTrivialSolution();
	boolean isIdentity();
	boolean isNonEmpty();
	boolean isReducedEchelon();
	boolean isHomogeneous();
	boolean isLinearlyIndependent();
	boolean isVectorSpace(Space space);
	boolean isSubspace(Space space);

	default int equationCount() {
		return height();
	}

	default int variableCount() {
		return width();
	}

	int rank(); // TODO matrix only?

	S solved();

	default boolean hasSolutions() {
		return isHomogeneous() || numberOfSolutions().equals(Constant.ZERO);
	}

	/** [0..inf) */
	Expression numberOfSolutions();

	int dimensions();

	<R extends Lattice<R>> boolean equivalent(Lattice<R> lattice);

}
