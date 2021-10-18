package bran.logic.statements.special;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
	protected boolean isConstant() {
		return false;
	}

	@Override
	protected boolean getTruth() {
		return truth;
	}

	@Override
	public List<Statement> getChildren() {
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
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		godelNumbers.push(GodelNumberSymbols.SYNTAX_ERROR);
	}

}
