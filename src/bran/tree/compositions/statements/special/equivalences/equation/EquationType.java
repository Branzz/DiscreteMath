package bran.tree.compositions.statements.special.equivalences.equation;

import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.StatementDisplayStyle;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.structure.mapper.Mapper;

import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public enum EquationType implements EquivalenceType {
	EQUAL  ((l, r) -> l.compareTo(r) == 0, "EQUALS", "=", "=", "==", "!=", "=", "="),
	UNEQUAL((l, r) -> l.compareTo(r) != 0, "DOES NOT EQUAL", "\u2260", "\u2260", "!=", "!=");

	private final Comparison<Expression, Expression> comparison;
	private final String[] symbols;

	EquationType(Comparison<Expression, Expression> comparison, final String... symbols) {
		this.comparison = comparison;
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
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public Mapper inverse() {
		return opposite();
	}

	@Override
	public EquationType opposite() {
		return this == EQUAL ? UNEQUAL : EQUAL;
	}

	@Override
	public boolean evaluate(Expression left, Expression right) {
		return comparison.apply(left, right);
	}

	// @Override
	// public <R, L extends Comparable<R>> boolean evaluate(final Comparable<L> left, final Comparable<R> right) {
	// 	return (this == EQUAL) == (right.compareTo(left) == 0);
	// }
	//
	@Override
	public boolean lesser() {
		return this == UNEQUAL;
	}

	@Override
	public boolean greater() {
		return this == UNEQUAL;
	}

	@Override
	public boolean equal() {
		return this == EQUAL;
	}

	@Override
	public EquationType flipped() {
		return this;
	}

}
