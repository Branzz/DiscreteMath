package bran.tree.compositions.expressions.functions;

import bran.tree.compositions.statements.Statement;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.structure.mapper.Mapper;
import bran.tree.compositions.expressions.Expression;

public interface ExpFunction extends Mapper {

	double function(double... a);

	int getArgAmount();

	Expression derive(Expression... exp);
	Statement domain(Expression... expressions) throws IllegalArgumentAmountException;

	default Statement domainS(Expression... expressions) {
		try { return domain(expressions); }
		catch (IllegalArgumentAmountException e) { return null; }
	}

	void checkArguments(int length) throws IllegalArgumentAmountException;

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

}
