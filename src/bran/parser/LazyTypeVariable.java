package bran.parser;

import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.statements.VariableStatement;

class LazyTypeVariable {

	private final String name;
	private boolean foundType;
	boolean isExpression; // Variable vs VariableStatement
	private Variable asVariable;
	private VariableStatement asVariableStatement;

	public LazyTypeVariable(String name) {
		this.name = name;
		foundType = false;
	}

	public void found(boolean isExpression) {
		foundType = true;
		this.isExpression = isExpression;
		if (isExpression)
			asVariable = new Variable(name);
		else
			asVariableStatement = new VariableStatement(name);
	}

	public boolean foundType() {
		return foundType;
	}

	public Variable getAsVariable() {
		if (!foundType || !isExpression)
			return null; // it returns null anyway, but this could be replaced by exception
		else
			return asVariable;
	}

	public VariableStatement getAsVariableStatement() {
		if (!foundType || isExpression)
			return null;
		else
			return asVariableStatement;
	}

}
