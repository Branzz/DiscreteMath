package bran.tree.compositions.statements.operators;

import bran.tree.compositions.statements.UnaryStatement;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public enum UnaryStatementOperator implements BranchOperator {
	CONSTANT(b -> b,  1, "self", "self", "self", "self", "self", "self"),
	NOT     (b -> !b, 1, "\u00ac", "~", "!", "~", "complement", "\\", "not");
	/* All true, all false */

	private final String[] symbols;
	private final AssociativityPrecedenceLevel level;
	private final LineOperable lineOperable;

	UnaryStatementOperator(final LineOperable lineOperable, int level, final String... symbols) {
		this.lineOperable = lineOperable;
		this.level = AssociativityPrecedenceLevel.of(level);
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

	public boolean operate(boolean value) { // TODO abstract
		return lineOperable.operate(value);
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return level;
	}

	public UnaryStatement of(final Statement statement) {
		return new UnaryStatement(this, statement);
	}

	@FunctionalInterface
	interface LineOperable {
		boolean operate(boolean value);
	}

}
