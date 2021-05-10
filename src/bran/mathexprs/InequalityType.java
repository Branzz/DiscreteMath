package bran.mathexprs;

import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;

import static bran.logic.statements.operators.DisplayStyle.displayStyle;

public enum InequalityType {
	LESS((l, r) -> l.compareTo(r) < 0, "less than", "<", "<", "<", "<", "<"),
	LESS_EQUAL((l, r) -> l.compareTo(r) <= 0, "less than or equal to", "\u2264", "\u2264", "<=", "<="),
	GREATER((l, r) -> l.compareTo(r) > 0, "greater than", ">", ">", ">", ">", ">"),
	GREATER_EQUAL((l, r) -> l.compareTo(r) >= 0, "greater than or equal to", "\u2265", "\u2265", ">=", ">=");

	private final Comparison comparison;
	private final String[] symbols;

	InequalityType(final Comparison comparison, final String... symbols) {
		this.comparison = comparison;
		this.symbols = symbols;
	}

	public boolean evaluate(Comparable left, Comparable right) {
		return comparison.evaluate(left, right);
	}

	@FunctionalInterface
	interface Comparison {
		boolean evaluate(Comparable left, Comparable right);
	}

	public String toString() {
		return switch (displayStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> symbols[displayStyle.index() + 1];
		};
	}

}
