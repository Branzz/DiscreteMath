package bran.lattice;

import bran.lattice.matrix.ExpressionMatrix;
import bran.lattice.vector.Vector;
import bran.lattice.vector.VectorSet;
import bran.tree.compositions.expressions.Expression;

import java.util.List;

/**
 * a cache of 1 to 2 of
 *   a System of Equations, set of vectors, or matrix of expressions
 *   which do not use this inner cache
 *
 *  @param <P> - outer parent class
 */
public class GeneralLattice<P extends Lattice<P>> implements Lattice<GeneralLattice<P>> {

	Lattice<P> real;

	SystemOfEquations system;
	VectorSet vectors;
	ExpressionMatrix matrix;

	public GeneralLattice(SystemOfEquations system) {
		this.system = system;
	}

	public GeneralLattice(VectorSet vectors) {
		this.vectors = vectors;
	}

	public GeneralLattice(ExpressionMatrix matrix) {
		this.matrix = matrix;
	}

	@Override public SystemOfEquations toSystemOfEquations(List<Expression> variables) { return null; }
	@Override public VectorSet toVectorSet() { return null; }
	@Override public ExpressionMatrix toMatrix() { return null; }

	@Override
	public int height() {
		return 0;
	}

	@Override
	public int width() {
		return 0;
	}

	@Override
	public Vector getColumn(int col) {
		return null;
	}

	@Override
	public boolean hasTrivialSolution() {
		return false;
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

	@Override
	public boolean isNonEmpty() {
		return false;
	}

	@Override
	public boolean isReducedEchelon() {
		return false;
	}

	@Override
	public boolean isHomogeneous() {
		return false;
	}

	@Override
	public boolean isLinearlyIndependent() {
		return false;
	}

	@Override
	public boolean isVectorSpace(Space space) {
		return false;
	}

	@Override
	public boolean isSubspace(Space space) {
		return false;
	}

	@Override
	public int rank() {
		return 0;
	}

	@Override
	public GeneralLattice solved() {
		return null;
	}

	@Override
	public Expression numberOfSolutions() {
		return null;
	}

	@Override
	public int dimensions() {
		return 0;
	}

	@Override
	public <R extends Lattice<R>> boolean equivalent(Lattice<R> lattice) {
		return false;
	}

}
