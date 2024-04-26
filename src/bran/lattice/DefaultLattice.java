package bran.lattice;

import bran.lattice.matrix.ExpressionMatrix;
import bran.lattice.vector.Vector;
import bran.lattice.vector.VectorSet;
import bran.tree.compositions.expressions.Expression;

import java.util.List;

public interface DefaultLattice<S extends Lattice<S>> extends Lattice<S> {

	Lattice inner();

	// these can convert between each other
	SystemOfEquations toSystemOfEquations(List<Expression> variables);
	VectorSet toVectorSet();
	ExpressionMatrix toMatrix();

	default int height() {
		return inner().height();
	}

	default int width() {
		return inner().width();
	}

	default Vector getColumn(int col) {
		return inner().getColumn(col);
	}

	default boolean hasTrivialSolution() {
		return inner().hasTrivialSolution();
	}

	default boolean isIdentity() {
		return inner().isIdentity();
	}

	default boolean isNonEmpty() {
		return inner().isNonEmpty();
	}

	default boolean isReducedEchelon() {
		return inner().isReducedEchelon();
	}

	default boolean isHomogeneous() {
		return inner().isHomogeneous();
	}

	default boolean isLinearlyIndependent() {
		return inner().isLinearlyIndependent();
	}

	default boolean isVectorSpace(Space space) {
		return inner().isVectorSpace(space);
	}

	default boolean isSubspace(Space space) {
		return inner().isSubspace(space);
	}

	default int equationCount() {
		return height();
	}

	default int variableCount() {
		return width();
	}

	default int rank() {
		return inner().rank();
	}

	S solved();

	default Expression numberOfSolutions() {
		return inner().numberOfSolutions();
	}

	default int dimensions() {
		return inner().dimensions();
	}

}
