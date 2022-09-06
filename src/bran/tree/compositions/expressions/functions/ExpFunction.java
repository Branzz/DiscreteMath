package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.parser.abst.CompilerOp;
import bran.parser.matching.Tokenable;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.special.equivalences.EquivalenceImpl;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;

import java.lang.reflect.Constructor;
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

	@CompilerOp
	static ExpFunction[] getTokens() {
		return MultiArgFunction.values();
	}

	// @Override
	// default Class<? extends Tokenable> constructedForkClass() { // TODO what if constructor throws? there's also more than 1 here.
	// 	return FunctionExpression.class;
	// }
	//
	// @Override
	// default <R extends Tokenable> Constructor<R> constructedForkConstructor(Class<R> rClass) {
	// 	return (Constructor<R>) Arrays.stream(FunctionExpression.class.getConstructors())
	// 											.filter(c -> c.getParameterCount() == 2)
	// 											.findFirst()
	// 											.orElseThrow(() -> new RuntimeException("FunctionExpression class constructors changed"));
	// }

}
