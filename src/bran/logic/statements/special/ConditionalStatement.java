package bran.logic.statements.special;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.operators.Operator;

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

	// @Override
	// public Statement clone() {
	// 	return new ConditionalStatement(conditional.getLeft().clone(), conditional.getRight().clone());
	// }

}
