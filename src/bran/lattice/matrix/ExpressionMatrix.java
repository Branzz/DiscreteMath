package bran.lattice.matrix;

import bran.lattice.Lattice;
import bran.lattice.Space;
import bran.lattice.SystemOfEquations;
import bran.lattice.vector.Vector;
import bran.lattice.vector.VectorSet;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ExpressionMatrix extends OperableMatrix<Expression, ExpressionMatrix> implements Lattice<ExpressionMatrix> {

	final boolean isAugmented;

	// must override that constructor
	public ExpressionMatrix(List<List<Expression>> values) {
		this(values, false);
	}

	/**
	 * @param augmented if this is false then it's assumed to be homogeneous
	 */
	public ExpressionMatrix(List<List<Expression>> expressions, boolean augmented) {
		super(expressions);
		this.isAugmented = augmented;
	}

	public ExpressionMatrix(Vector v) {
		super(columnMatrix(v));
		this.isAugmented = false;
	}

	private static List<List<Expression>> columnMatrix(Vector v) {
		List<List<Expression>> expressions = new ArrayList<>(v.size());
		for (int i = 0; i < expressions.size(); i++) {
			expressions.add(List.of(v.get(i)));
		}
		return expressions;
	}

	public ExpressionMatrix rref() {
		return null;
	}

	public boolean isAugmented() {
		return isAugmented;
	}

	ExpressionMatrix asUnAugmented() throws CloneNotSupportedException {
		if (isAugmented) {
			return this;
		} else {
			List<List<Expression>> augmentedMatrix = new ArrayList<>(getColumns());
			for (int i = 0; i < getColumns(); i++) {
				augmentedMatrix.add(new ArrayList<>(getRows() - 1));
				for (int j = 0; j < getRows() - 1; j++)
					augmentedMatrix.get(i).add((Expression) this.get(i, j).clone());
			}
			return new ExpressionMatrix(augmentedMatrix, false);
		}
	}

	@Override
	public ExpressionMatrix map(Function<Expression, Expression> function) {
		return new ExpressionMatrix(super.map(function).values);
	}

	@Override
	public ExpressionMatrix operate(OperableMatrix<Expression, ExpressionMatrix> m, BiFunction<Expression, Expression, Expression> function) {
		return new ExpressionMatrix(super.operate(m, function).values);
	}

	@Override
	public SystemOfEquations toSystemOfEquations(List<Expression> variables) {
		// getRows();
		List<Equation> equations = new ArrayList<>();
		return new SystemOfEquations(equations.toArray(Equation[]::new));
	}

	@Override
	public VectorSet toVectorSet() {
		return new VectorSet(IntStream.range(0, width()).mapToObj(this::getColumn).toArray(Vector[]::new));
	}

	@Override
	public ExpressionMatrix toMatrix() {
		return this;
	}

	@Override
	public int height() {
		return getRows();
	}

	@Override
	public int width() {
		return getColumns();
	}

	@Override
	public Vector getColumn(int col) {
		return new Vector(values.stream().map(r -> r.get(col)).toArray(Expression[]::new));
	}

	@Override
	public boolean hasTrivialSolution() {
		return isHomogeneous();
	}

	@Override
	public boolean isIdentity() {
		if (height() != width())
			return false;
		for (int i = 0; i < height(); i++) {
			for (int j = 0; j < width(); j++) {
				if (!(this.get(i, j) instanceof Constant c
					&& c == ((i == j) ? Constant.ONE : Constant.ZERO)))
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean isNonEmpty() {
		return height() > 0;
	}

	@Override
	public boolean isReducedEchelon() {
		return false;
	}

	@Override
	public boolean isHomogeneous() {
		return !isAugmented() || getColumn(width() - 1).isZero();
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
		return dimensions() - (int) values.stream().filter(r -> !r.stream().allMatch(x -> x == Constant.ZERO)).count();
	}

	@Override
	public ExpressionMatrix solved() {
		return rref();
	}

	@Override
	public Expression numberOfSolutions() {
		return null;
	}

	@Override
	public int dimensions() {
		return width() - (isAugmented() ? 1 : 0);
	}

	@Override
	public boolean equivalent(Lattice lattice) {
		return false;
	}


}
