package bran.mathexprs;

import bran.logic.statements.special.SpecialStatement;
import bran.tree.Equivalable;
import bran.tree.TreePart;

public class Equation<T extends TreePart & Equivalable<T>> extends SpecialStatement {

	private final T left;
	private final EquationType equationType;
	private final T right;

	public Equation(T left, EquationType equationType, T right) {
		this.left = left;
		this.equationType = equationType;
		this.right = right;
	}

	public Equation(T left, T right) {
		this(left, EquationType.EQUAL, right);
	}

	@Override
	public void simplify() {
		super.simplify();
	}

	@Override
	protected boolean getTruth() {
		if (equationType == EquationType.EQUAL == left.equivalentTo(right))
			return true; // TODO if the simplify is the same expression
		// else try {
		// 	return evaluate();
		// } catch (IllegalArgumentAmountException ignored) {
		// }
		return false;
	}

	// public boolean evaluate() throws IllegalArgumentAmountException {
	// 	return equationType == EquationType.EQUAL == (left.evaluate() == right.evaluate());

	// }

	@Override
	public String toString() {
		return "(" + left + ' ' + equationType + ' ' + right + ')';
	}

	// @Override // TODO
	// public Statement clone() {
	// 	return null;
	// 	// return new Equation(left.clone(), right.clone());
	// }

}
