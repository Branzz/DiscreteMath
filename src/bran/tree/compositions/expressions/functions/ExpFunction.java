package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

public interface ExpFunction extends BranchOperator {

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

	@Override
	default AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(1);
	}

}
