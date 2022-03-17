package bran.tree.compositions.expressions;

import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.expressions.values.Variable;

import java.util.Collection;

public class LimitExpression extends Expression {

	Expression approaches; // lim a ->
	Expression approached; // x
	Expression function;   // f(x)

	public LimitExpression(final Expression approaches, final Expression approached, final Expression function) {
		this.approaches = approaches;
		this.approached = approached;
		this.function = function;
	}

	@Override
	public java.util.Set<Variable> getVariables() {
		return null;
	}

	@Override
	public Expression simplified() {
		return new LimitExpression(approaches.simplified(), approached.simplified(), function.simplified());
	}

	@Override
	public double evaluate() {
		replaceAll(approaches, approached);
		return function.evaluate();
	}

	@Override
	public Expression derive() {
		return null;
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return false;
	}

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		if (function.equals(original))
			function = (Expression) replacement;
		else
			function.replaceAll(original, replacement);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {

	}

	// @Override
	// public Expression clone() {
	// 	return null;
	// }

	@Override
	public boolean equals(final Object other) {
		return false;
	}

	@Override
	public String toFullString() {
		return "Lim " + approaches + " -> " + approached + " of " + function;
	}

}
