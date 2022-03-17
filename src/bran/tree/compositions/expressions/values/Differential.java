package bran.tree.compositions.expressions.values;

import bran.tree.compositions.expressions.Expression;

public class Differential extends Variable {

	// the variable it is a differential of
	final Variable difVariable;

	public Differential(final Variable difVariable, final int level) {
		super("d".repeat(level) + difVariable.getName(), true);
		this.difVariable = difVariable;
		this.level = level;
	}

	@Override
	public Expression derive() {
		return getDifferential();
	}

}
