package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.SpecialStatement;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.tree.Equivalable;
import bran.tree.TreePart;

import java.util.List;

public class Inequality<T extends TreePart & Equivalable<T> & Comparable<T>> extends Equivalence {

	private final T left;
	private final InequalityType inequalityType;
	private final T right;

	public Inequality(final T left, final InequalityType inequalityType, final T right) {
		this.left = left;
		this.inequalityType = inequalityType;
		this.right = right;
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

	@Override
	public boolean equals(final Object s) {
		return true;
		// return s instanceof Inequality<T> i && ((left.equals(i.left) && right.equals(i.right) && inequalityType == i.inequalityType)
		// 									 || (right.equals(i.left) && left.equals(i.right) && inequalityType == inequalityType.opposite()));
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
	// 	return new Equation(left.clone(), right.clone());
	// }

}
