package bran.tree.compositions.statements.operators;

import bran.tree.compositions.statements.LineStatement;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public enum LineOperator implements BranchOperator {
	CONSTANT(b -> b,  1, "self", "self", "self", "self", "self", "self"),
	NOT     (b -> !b, 1, "\u00ac", "~", "!", "~", "complement", "\\", "not");
	/* All true, all false */

	private final String[] symbols;
	private final AssociativityPrecedenceLevel level;
	private final LineOperable lineOperable;

	LineOperator(final LineOperable lineOperable, int level, final String... symbols) {
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

	public boolean operate(boolean value) {
		return lineOperable.operate(value);
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return level;
	}

	public LineStatement of(final Statement statement) {
		return new LineStatement(this, statement);
	}

	@FunctionalInterface
	interface LineOperable {
		boolean operate(boolean value);
	}

}
