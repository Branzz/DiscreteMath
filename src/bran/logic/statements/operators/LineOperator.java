package bran.logic.statements.operators;

import bran.logic.statements.LineStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.StatementOperatorType;
import bran.tree.BranchOperator;
import bran.tree.Associativity;
import bran.tree.Equivalable;

import javax.sound.sampled.Line;

import static bran.logic.statements.StatementOperatorType.REVERSE;
import static bran.logic.statements.StatementDisplayStyle.statementStyle;

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
