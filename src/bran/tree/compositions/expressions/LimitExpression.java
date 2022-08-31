package bran.tree.compositions.expressions;

import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.expressions.values.Variable;

import java.util.Collection;

public class LimitExpression extends Expression {

	// lim approaching -> approached of function
	Expression approaching;
	Expression approached;
	Expression function;

	public LimitExpression(final Expression approaching, final Expression approached, final Expression function) {
		this.approaching = approaching;
		this.approached = approached;
		this.function = function;
	}

	public Expression approaching() {
		return approaching;
	}

	public Expression approached() {
		return approached;
	}

	public Expression function() {
		return function;
	}

	@Override
	public java.util.Set<Variable> getVariables() {
		return Expression.combineVariableSets(approaching, approached, function);
	}

	@Override
	public Expression simplified() {
		return new LimitExpression(approaching.simplified(), approached.simplified(), function.simplified());
	}

	@Override
	public double evaluate() {
		replaceAll(approaching, approached);
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
		return this == other;
	}

	@Override
	public String toFullString() {
		return "lim " + approaching + " -> " + approached + " of " + function;
	}

}
