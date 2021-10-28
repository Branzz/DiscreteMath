package bran.mathexprs.treeparts;

import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.Collection;
import java.util.Stack;

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
	public void replaceAll(final Expression approaches, final Expression approached) {
		if (function.equals(approaches))
			function = approached;
		else
			function.replaceAll(approaches, approached);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {

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
