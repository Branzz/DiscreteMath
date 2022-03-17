package bran.tree.compositions.statements.operators;

import bran.tree.compositions.statements.LineStatement;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.StatementOperatorType;
import bran.tree.structure.mapper.BranchOperator;
import bran.tree.structure.mapper.Associativity;

import static bran.tree.compositions.statements.StatementOperatorType.REVERSE;
import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public enum LineOperator implements BranchOperator {
	CONSTANT(b -> b,	REVERSE, "self", "self", "self", "self", "self", "self"),
	NOT(b -> !b,		REVERSE, "\u00ac", "~", "!", "~", "complement", "\\", "not");
	/* All true, all false */
	public static final int order = 1;

	private final String[] symbols;
	private final StatementOperatorType operatorType;
	private final LineOperable lineOperable;

	LineOperator(final LineOperable lineOperable, final StatementOperatorType operatorType, final String... symbols) {
		this.lineOperable = lineOperable;
		this.operatorType = operatorType;
		this.symbols = symbols;
	}

	public String[] getSymbols() {
		return symbols;
	}

	public String toString() {
		return switch (statementStyle) {
			case NAME -> name() + " ";
			case LOWERCASE_NAME -> name().toLowerCase() + " ";
			default -> symbols[statementStyle.index()];
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

	public LineStatement of(final Statement statement) {
		return new LineStatement(statement, this);
	}

	@FunctionalInterface
	interface LineOperable {
		boolean operate(boolean value);
	}

}
