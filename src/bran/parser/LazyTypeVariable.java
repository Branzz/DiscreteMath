package bran.parser;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;

class LazyTypeVariable implements Composition {

	private final String name;
	private boolean foundType;
	boolean isExpression; // Variable vs VariableStatement
	private Variable asVariable;
	private VariableStatement asVariableStatement;

	public LazyTypeVariable(String name) {
		this.name = name;
		foundType = false;
	}

	public boolean isExpression() {
		return foundType && isExpression;
	}

	public boolean isStatement() {
		return foundType && !isExpression;
	}

	public boolean isFound() {
		return foundType;
	}

	public Composition found(boolean isExpression) {
		foundType = true;
		this.isExpression = isExpression;
		if (isExpression)
			return asVariable = new Variable(name);
		else
			return asVariableStatement = new VariableStatement(name);
	}

	public Statement foundAsStatement() {
		if (foundType)
			return asVariableStatement;
		return (Statement) found(false);
	}

	public Expression foundAsExpression() {
		if (foundType)
			return asVariable;
		return (Expression) found(true);
	}

	public boolean foundType() {
		return foundType;
	}

	public Composition getAsComposition() {
		return foundType ? (isExpression ? getAsVariable() : getAsVariableStatement()) : null;
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

	@Override
	public String toString() {
		return name;
	}

	//TODO unimplement
	@Override public String toFullString() { return getAsComposition().toFullString(); }
	@Override public boolean equals(final Object s) { return getAsComposition().equals(s); }
	@Override public Composition simplified() { return getAsComposition().simplified(); }
	@Override public void replaceAll(final Composition original, final Composition replacement) { getAsComposition().replaceAll(original, replacement); }
	@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { getAsComposition().appendGodelNumbers(godelBuilder); }

}
