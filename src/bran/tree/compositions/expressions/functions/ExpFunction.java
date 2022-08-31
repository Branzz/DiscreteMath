package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.util.Arrays;

public interface ExpFunction extends AbstractFunction<Double> {

	Expression derive(Expression... exp);
	Statement domain(Expression... expressions) throws IllegalArgumentAmountException;

	double function(double... a);

	@Override
	default Double function(Double... a) {
		AbstractFunction.super.function(a);
		return function(Arrays.stream(a).mapToDouble(Double::valueOf).toArray());
	}

	default Statement domainS(Expression... expressions) {
		try { return domain(expressions); }
		catch (IllegalArgumentAmountException e) { return null; }
	}

	ExpFunction inverse();

	Expression inverse(int arg, Expression... expressions);

	/**
	 * "of Secure"; TODO for package use only
	 */
	default FunctionExpression ofS(Expression... other) {
		return new FunctionExpression(this, true, other);
	}

	default FunctionExpression of(Expression... other) throws IllegalArgumentAmountException {
		return new FunctionExpression(this, other);
	}

	@Override
	default AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(1);
	}

}
