package bran.logic.statements.special;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.LogicalOperator;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ConditionalStatement extends SpecialStatement {

	private final OperationStatement conditional;

	public ConditionalStatement(final Statement hypothesis, final Statement conclusion) {
		conditional = new OperationStatement(hypothesis, LogicalOperator.IMPLIES, conclusion);
	}

	@Override
	protected boolean getTruth() {
		return conditional.getTruth();
	}

	@Override
	public String toFullString() {
		return "if " + conditional.getLeft() + " then " + conditional.getRight();
	}

	@Override
	public String toString() {
		return "if " + conditional.getLeft().toString() + " then " + conditional.getRight().toString();
	}

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public Statement simplified() {
		final Statement simplifiedConditional = conditional.simplified();
		if (simplifiedConditional instanceof OperationStatement operationStatement && operationStatement.getOperator() == LogicalOperator.IMPLIES)
			return new ConditionalStatement(operationStatement.getLeft(), operationStatement.getRight());
		else
			return simplifiedConditional;
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		conditional.getLeft().appendGodelNumbers(godelNumbers, variables);
		godelNumbers.push(GodelNumberSymbols.IF_THEN);
		conditional.getRight().appendGodelNumbers(godelNumbers, variables);
	}

	@Override
	public List<Statement> getChildren() {
		return Collections.singletonList(conditional);
	}

	@Override
	public List<VariableStatement> getVariables() {
		return conditional.getVariables();
	}

	@Override
	public Statement negation() {
		return conditional.getLeft().or(conditional.getRight().not());
	}

	// @Override
	// public Statement clone() {
	// 	return new ConditionalStatement(conditional.getLeft().clone(), conditional.getRight().clone());
	// }

}
