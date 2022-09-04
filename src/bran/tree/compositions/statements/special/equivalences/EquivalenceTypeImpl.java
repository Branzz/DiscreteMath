package bran.tree.compositions.statements.special.equivalences;

import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.ExpressionDisplayStyle;
import bran.tree.compositions.sets.SetDisplayStyle;
import bran.tree.structure.mapper.Mapper;

import static bran.tree.compositions.sets.SetDisplayStyle.setStyle;
import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public enum EquivalenceTypeImpl implements EquivalenceType {
	// TODO Deprecate variations
	// TODO Implement order of operations compatibility with other types
	LESS		 ((l, r) -> l.compareTo(r) <  0, true,  false, "less than", "<", "<", "<", "<", "<"),
	LESS_EQUAL	 ((l, r) -> l.compareTo(r) <= 0, true,   true, "less than or equal to", "\u2264", "\u2264", "<=", "<="),
	GREATER		 ((l, r) -> l.compareTo(r) >  0, false, false, "greater than", ">", ">", ">", ">", ">"),
	GREATER_EQUAL((l, r) -> l.compareTo(r) >= 0, false,  true, "greater than or equal to", "\u2265", "\u2265", ">=", ">="),
	EQUAL  		 ((l, r) -> l.compareTo(r) == 0, false,  true, "equals", "=", "=", "==", "==", "=", "="),
	UNEQUAL		 ((l, r) -> l.compareTo(r) != 0, false, false, "does not equal", "\u2260", "\u2260", "!=", "!=");

	private final Comparison<Expression, Expression> comparison;
	private final boolean lesser;
	private final boolean equal;
	private final String[] symbols;
	private EquivalenceTypeImpl opposite;
	private EquivalenceTypeImpl flipped;

	static {
		LESS.opposite = GREATER_EQUAL;
		GREATER_EQUAL.opposite = LESS;
		LESS_EQUAL.opposite = GREATER;
		GREATER.opposite = LESS_EQUAL;
		EQUAL.opposite = UNEQUAL;
		UNEQUAL.opposite = EQUAL;
		LESS.flipped = GREATER;
		GREATER.flipped = LESS;
		LESS_EQUAL.flipped = GREATER_EQUAL;
		GREATER_EQUAL.flipped = LESS_EQUAL;
		UNEQUAL.flipped = UNEQUAL;
		EQUAL.flipped = EQUAL;
	}

	EquivalenceTypeImpl(Comparison<Expression, Expression> comparison, boolean lesser, boolean equal, final String... symbols) {
		this.comparison = comparison;
		this.lesser = lesser;
		this.equal = equal;
		this.symbols = symbols;
	}

	@Override
	public  boolean evaluate(Expression left, Expression right) {
		return comparison.apply(left, right);
	}

	private String getSymbol(int index) {
		try {
			return symbols[index];
		} catch (IndexOutOfBoundsException e) {
			return name();
		}
	}

	public EquivalenceTypeImpl opposite() {
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

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public Mapper inverse() {
		return opposite();
	}

	@Override
	public boolean lesser() {
		return lesser;
	}

	@Override
	public boolean greater() {
		return !lesser;
	}

	@Override
	public boolean equal() {
		return equal;
	}

	@Override
	public EquivalenceType flipped() {
		return flipped;
	}

}
