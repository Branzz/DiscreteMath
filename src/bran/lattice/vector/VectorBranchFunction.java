package bran.lattice.vector;

import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

import java.util.Arrays;
import java.util.function.Function;

/**
 * function on one vector
 */
public enum VectorBranchFunction implements BranchOperator<Expression, Vector> {
	Sum(u -> {
		if (u.size() == 0)
			return Constant.ZERO;
		return Arrays.stream(u.elements).reduce(Expression.empty(), Expression::plus);
	}, 1),
	Magnitude(u -> Sum.of(u.squared()).sqrt(), 1),
	Normalized(u -> VectorWrap.of(u.div(Magnitude.of(u))), 1);

	private final Function<Vector, Expression> function;
	private final AssociativityPrecedenceLevel level;

	VectorBranchFunction(Function<Vector, Expression> function, int level) {
		this.function = function;
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	@Override
	public Expression operate(Vector arg) {
		return function.apply(arg);
	}

	Expression of(Vector arg) {
		return new FunctionVector(this, arg);
	}

	Expression of(Expression arg) {
		return new FunctionVector(this, VectorWrap.of(arg));
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
