package bran.logic.statements.special;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.Operator;

import java.util.List;

public class ConditionalStatement extends SpecialStatement {

	private final OperationStatement conditional;

	public ConditionalStatement(final Statement hypothesis, final Statement conclusion) {
		conditional = new OperationStatement(hypothesis, Operator.IMPLIES, conclusion);
	}

	@Override
	protected boolean getTruth() {
		return conditional.getTruth();
	}

	@Override
	public String toString() {
		return "if " + conditional.getLeft() + " then " + conditional.getRight();
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		return null;
	}

	@Override
	public boolean equivalentTo(final Statement other) {
		return false;
	}

	@Override
	public List<Statement> getChildren() {
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

	// @Override
	// public Statement clone() {
	// 	return new ConditionalStatement(conditional.getLeft().clone(), conditional.getRight().clone());
	// }

}
