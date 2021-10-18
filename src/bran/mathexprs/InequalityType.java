package bran.mathexprs;

import bran.sets.SetDisplayStyle;

import static bran.logic.statements.StatementDisplayStyle.statementStyle;
import static bran.sets.SetDisplayStyle.setStyle;

public enum InequalityType implements EquivalenceType { // TODO Implement order of operations compatibility with other types
	LESS((l, r) -> l.compareTo(r) < 0, "less than", "<", "<", "<", "<", "<"),
	LESS_EQUAL((l, r) -> l.compareTo(r) <= 0, "less than or equal to", "\u2264", "\u2264", "<=", "<="),
	GREATER((l, r) -> l.compareTo(r) > 0,  "greater than", ">", ">", ">", ">", ">"),
	GREATER_EQUAL((l, r) -> l.compareTo(r) >= 0, "greater than or equal to", "\u2265", "\u2265", ">=", ">=");

	private final Comparison comparison;
	private final String[] symbols;
	private InequalityType opposite;

	static {
		LESS.opposite = GREATER_EQUAL;
		LESS_EQUAL.opposite = GREATER;
		GREATER_EQUAL.opposite = LESS;
		GREATER.opposite = LESS_EQUAL;
	}

	InequalityType(final Comparison comparison, final String... symbols) {
		this.comparison = comparison;
		this.symbols = symbols;
	}

	@Override
	public boolean evaluate(Comparable left, Comparable right) {
		return comparison.evaluate(left, right);
	}

	private String getSymbol(int index) {
		try {
			return symbols[index];
		} catch (IndexOutOfBoundsException e) {
			return name();
		}
	}

	public InequalityType opposite() {
		return opposite;
	}

	public String toString() {
		return switch (statementStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> getSymbol(statementStyle.index() + 1);
		};
	}

	public String toString(boolean sets) {
		if (!sets)
			return toString();
		return switch (setStyle) {
			case NAME -> name();
			case LOWERCASE_NAME -> name().toLowerCase();
			default -> getSymbol(setStyle.index() + 1);
		};
	}

	public String toString(ExpressionDisplayStyle displayStyle) {
		return switch (displayStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> getSymbol(statementStyle.index() + 1);
		};
	}

	public String toString(SetDisplayStyle displayStyle) {
		return switch (displayStyle) {
			case NAME -> symbols[0];
			case LOWERCASE_NAME -> symbols[0].toLowerCase();
			default -> getSymbol(statementStyle.index() + 1);
		};
	}

}
