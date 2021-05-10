package bran.mathexprs;

import static bran.logic.statements.operators.DisplayStyle.displayStyle;
public enum EquationType {
	EQUAL("EQUALS", "=", "=", "==", "!=", "=", "="),
	UNEQUAL("DOES NOT EQUAL", "\u2260", "\u2260", "!=", "!=");

	private final String[] symbols;
	EquationType(final String... symbols) {
		this.symbols = symbols;
	}

	public String toString() {
		return switch (displayStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> symbols[displayStyle.index() + 1];
		};
	}

}
