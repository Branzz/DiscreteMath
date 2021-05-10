package bran.logic.statements.operators;

import bran.logic.statements.OperatorType;
import bran.logic.tree.BranchOperator;
import bran.logic.tree.Associativity;

import static bran.logic.statements.OperatorType.REVERSE;
import static bran.logic.statements.operators.DisplayStyle.displayStyle;

public enum LineOperator implements BranchOperator {
	CONSTANT(b -> b,	REVERSE, "self", "self", "self", "self", "self", "self"),
	NOT(b -> !b,		REVERSE, "\u00ac", "~", "!", "~", "complement", "\\", "not");
	/* All true, all false */
	public static final int order = 1;

	private final String[] symbols;
	private final OperatorType operatorType;
	private final LineOperable lineOperable;

	LineOperator(final LineOperable lineOperable, final OperatorType operatorType, final String... symbols) {
		this.lineOperable = lineOperable;
		this.operatorType = operatorType;
		this.symbols = symbols;
	}

	public String[] getSymbols() {
		return symbols;
	}

	public String toString() {
		return switch (displayStyle) {
			case NAME -> name() + " ";
			case LOWERCASE_NAME -> name().toLowerCase() + " ";
			default -> symbols[displayStyle.index()];
		};
	}

	public boolean operate(boolean value) {
		return lineOperable.operate(value);
	}

	@Override
	public int getOrder() {
		return operatorType.precedence();
	}

	@Override
	public int maxOrder() {
		return order;
	}

	@Override
	public int minOrder() {
		return order;
	}

	@Override
	public Associativity getDirection() {
		return operatorType.associativity();
	}

	@FunctionalInterface
	interface LineOperable {
		boolean operate(boolean value);
	}

}
