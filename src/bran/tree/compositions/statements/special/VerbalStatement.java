package bran.tree.compositions.statements.special;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.ArrayList;
import java.util.List;

public class VerbalStatement extends Statement {

	private final String statement;
	private final boolean truth;

	public VerbalStatement(final String statement) {
		this(statement, true);
	}

	public VerbalStatement(final String statement, final boolean truth) {
		this.statement = statement;
		this.truth = truth;
	}

	@Override
	protected boolean getTruth() {
		return truth;
	}

	@Override
	public List<Composition> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public List<VariableStatement> getVariables() {
		return new ArrayList<>();
	}

	@Override
	public String toFullString() {
		return statement;
	}

	@Override
	public boolean equals(final Object o) {
		return o == this || (o instanceof VerbalStatement && ((VerbalStatement) o).statement.equals(statement));
	}

	@Override
	public Statement simplified() {
		return new VerbalStatement(statement, truth);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
	}

}
