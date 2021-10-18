package bran.mathexprs;

import bran.logic.statements.StatementDisplayStyle;

import static bran.logic.statements.StatementDisplayStyle.statementStyle;

public enum EquationType implements EquivalenceType {
	EQUAL("EQUALS", "=", "=", "==", "!=", "=", "="),
	UNEQUAL("DOES NOT EQUAL", "\u2260", "\u2260", "!=", "!=");

	private final String[] symbols;

	EquationType(final String... symbols) {
		this.symbols = symbols;
	}

	public String toString() {
		return toString(statementStyle);
	}

	public String toString(StatementDisplayStyle displayStyle) {
		return switch(displayStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> symbols[statementStyle.index() + 1];
		};
	}

	@Override
	public EquivalenceType opposite() {
		return this == EQUAL ? UNEQUAL : EQUAL;
	}

	@Override
	public boolean evaluate(final Comparable left, final Comparable right) {
		return (this == EQUAL) == left.equals(right);
	}

}
