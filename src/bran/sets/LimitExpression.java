package bran.sets;

import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.Function;

import java.util.Collection;
import java.util.List;

public class LimitExpression extends Expression {

	Expression approaches; // lim a ->
	Variable approached; // x
	Expression function; // f(x) =
	Expression limit;    // L

	@Override
	public List<Variable> getVariables() {
		return null;
	}

	@Override
	public Expression simplified() {
		return null;
	}

	@Override
	public double evaluate() {
		return 0;
	}

	@Override
	public Expression derive() {
		return null;
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return false;
	}

	// @Override
	// public Expression clone() {
	// 	return null;
	// }

	@Override
	public boolean equals(final Object other) {
		return false;
	}

}
