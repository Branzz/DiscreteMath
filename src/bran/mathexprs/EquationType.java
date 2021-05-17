package bran.mathexprs;

import bran.logic.statements.StatementDisplayStyle;

import static bran.logic.statements.StatementDisplayStyle.statementStyle;

public enum EquationType {
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

}
