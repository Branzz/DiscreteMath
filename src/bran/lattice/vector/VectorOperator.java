package bran.lattice.vector;

import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.structure.GenericOperator;
import bran.tree.structure.Operable;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;

import static bran.lattice.vector.VectorBranchFunction.Sum;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.ACOS;

/**
 * operation between 2 vectors
 */
public enum VectorOperator implements GenericOperator<Expression, Vector, VectorOperator, VectorOperation> {
		// implements ForkOperator<Expression, Vector, Vector> {

	DotProduct((u, v) -> {
		if (u.size() != v.size())
			return Constant.NAN;
		return Sum.of(u.times(v));
	}, ArithmeticOperator.MUL.level()),
	Angle((u, v) -> ACOS.ofS(DotProduct.of(u, v).div(u.magnitude().times(v.magnitude()))),
	   AssociativityPrecedenceLevel.of(1)),
	Distance((u, v) -> {
		if (u.size() != v.size())
			return Constant.NAN;
		return Sum.of(u.elements[0].minus(v.elements[0]).squared()).sqrt();
	}, AssociativityPrecedenceLevel.of(1));

	private final Operable<Expression, Vector, Vector, VectorOperator> operable;
	private final AssociativityPrecedenceLevel level;

	VectorOperator(Operable<Expression, Vector, Vector, VectorOperator> operable, AssociativityPrecedenceLevel level) {
		this.operable = operable;
		this.level = level;
	}

	public VectorOperation of(Vector left, Vector right) {
		return new VectorOperation(left, this, right);
	}

	@Override
	public Expression operate(Vector vector, Vector vector2) {
		return operable.operate(vector, vector2);
	}

	@Override
	public String[] getSymbols() {
		return new String[0];
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return level;
	}

}
