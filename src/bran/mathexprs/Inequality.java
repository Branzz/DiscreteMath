package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.SpecialStatement;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;

public class Inequality<L extends Comparable<L>, R extends Comparable<R>> extends SpecialStatement {

	private final L left;
	private final InequalityType inequalityType;
	private final R right;

	public Inequality(final L left, final InequalityType inequalityType, final R right) {
		this.left = left;
		this.inequalityType = inequalityType;
		this.right = right;
	}

	@Override
	public void simplify() {

		// return VariableStatement.of(getTruth());
	}

	@Override
	protected boolean getTruth() {
		return inequalityType.evaluate(left, right);
	}

	// public boolean evaluate() {
	// 	return inequalityType.evaluate(left.evaluate(), right.evaluate());
	// }

	@Override
	public String toString() {
		return left + " " + inequalityType + " " + right;
	}

	// @Override
	// public Statement clone() {
	// 	return new Equation(left.clone(), right.clone());
	// }

}
