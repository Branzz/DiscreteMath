package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.tree.Equivalable;
import bran.tree.TreePart;

import java.util.List;

import static bran.logic.statements.VariableStatement.*;

public class Equation<T extends TreePart & Equivalable<T>> extends Equivalence {

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

	@Override
	public boolean equals(final Object s) {
		return true;
		// return s instanceof Equation<T> e && ((left.equals(e.left) && right.equals(e.right)) || (right.equals(e.left) && left.equals(e.right)));
	}

	@Override
	public Statement simplified() {
		// return getTruth() ? TAUTOLOGY : CONTRADICTION;
		return left instanceof Statement l && right instanceof Statement r
				? new Equation<>(l.simplified(), r.simplified())
				: new Equation<>(left, right);
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

	// @Override // TODO
	// public Statement clone() {
	// 	return null;
	// 	// return new Equation(left.clone(), right.clone());
	// }

}
