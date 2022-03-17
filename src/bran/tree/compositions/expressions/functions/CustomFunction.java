package bran.tree.compositions.expressions.functions;

import bran.tree.compositions.expressions.functions.appliers.DomainSupplier;
import bran.tree.compositions.expressions.functions.appliers.FunctionDerivable;
import bran.tree.compositions.expressions.functions.appliers.Functional;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.Expression;
import bran.exceptions.IllegalArgumentAmountException;

public record CustomFunction(int arguments, ExpFunction inverse, Functional functional, FunctionDerivable functionDerivable, DomainSupplier domainSupplier,
							 String... symbols) implements ExpFunction {

	// Example
	// CustomFunction INC = new CustomFunction(1, a -> a[0] + 1, (r, e) -> e[0], "inc", "increment");
	// public static final CustomFunction DERIVATIVE;
	//
	// static {
	// 	Variable h = new Variable("h");
	// 	DERIVATIVE = new CustomFunction(1, a -> new LimitExpression(h, Constant.ZERO, a[0], null), null, "der", "d/dx");
	// }

	@Override
	public double function(double... a) {
		return functional.function(a);
	}

	@Override
	public Expression derive(Expression... exp) {
		return functionDerivable.derive(exp);
	}

	@Override
	public Statement domain(final Expression... expressions) {
		return domainSupplier.get(expressions);
	}

	@Override
	public void checkArguments(final int length) throws IllegalArgumentAmountException {
		if (length != arguments)
			throw new IllegalArgumentAmountException(String.format("wrong number of arguments. given %d but needed %d", length, arguments));
	}

	@Override
	public ExpFunction inverse() {
		return inverse;
	}

	@Override
	public String toString() {
		return symbols.length > 0 ? symbols[0] : ("Function #" + hashCode());
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

}
