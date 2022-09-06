package bran.tree.compositions.expressions.functions.rec;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.AbstractFunction;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.util.function.Function;

/**
 * Recursive Function
 * take function as argument of this function (eg derivative)
 */
public enum RecFunction implements AbstractFunction<Expression> {
	SIMPLIFY(1, es -> es[0].simplified(), "simplify"),
	INVERSE(1, es -> es[0].inverse().get(0), "inverse"),
	// INVERSE(1, es -> es[0].inverse().get(0), "inverse"),
	DERIVE(1, es -> es[0].derive(), "derive", "derivative");

	private final int argAmount;
	private final Function<Expression[], Expression> function;
	private final String[] symbols;

	RecFunction(int argAmount, Function<Expression[], Expression> function, String... symbols) {
		this.argAmount = argAmount;
		this.function = function;
		this.symbols = symbols;
	}

	@Override
	public Expression function(Expression... expressions) {
		AbstractFunction.super.function(expressions);
		return function.apply(expressions);
	}

	@Override
	public int getArgAmount() {
		return argAmount;
	}

	@Override
	public void checkArguments(int length) throws IllegalArgumentAmountException {
		checkArguments(length, argAmount);
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(1);
	}

	public Expression of(Expression... input) {
		return new RecFunctionExpression(this, input);
	}

	@Override
	public Function<Expression[], Expression> operator() {
		return function;
	}
}

