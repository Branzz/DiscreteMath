package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.SpecialStatement;
import bran.mathexprs.treeparts.Constant;
import bran.tree.Composition;

import java.util.List;

public abstract class Equivalence<T extends Composition> extends SpecialStatement {

	protected final T left;
	protected final T right;

	public Equivalence(final T left, final T right) {
		this.left = left;
		this.right = right;
	}

	public static <T extends Composition> Equivalence<T> of(final T left, final EquivalenceType equivalenceType, final T right) {
		if (equivalenceType instanceof InequalityType inequalityType)
			return new Inequality<>(left, inequalityType, right);
		else if (equivalenceType instanceof EquationType equationType)
			return new Equation<>(left, equationType, right);
		return null;
	}

	public T getLeft() {
		return left;
	}

	public abstract EquivalenceType getEquivalenceType();

	public T getRight() {
		return right;
	}

	@Override
	protected boolean getTruth() { // TODO Equation: if the simplify is the same expression
		return getEquivalenceType().evaluate(left, right);
	}

	@Override
	public String toFullString() {
		return "(" + left + ' ' + getEquivalenceType() + ' ' + right + ')';
	}

	@Override
	public String toString() {
		return left.toString() + " " + getEquivalenceType() + " " + right.toString();
	}

	@Override
	public Statement simplified() {
		final Composition leftSimplified = left.simplified();
		final Composition rightSimplified = right.simplified();
		if (leftSimplified instanceof Constant && rightSimplified instanceof Constant)
			return VariableStatement.of(getEquivalenceType().evaluate(leftSimplified, rightSimplified));
		boolean equal = leftSimplified.equals(rightSimplified);
		boolean notEqual = false;
		if (leftSimplified instanceof Statement leftStatement && rightSimplified instanceof Statement rightStatement)
			notEqual = leftStatement.equalsNot(rightStatement);
		if (equal || notEqual)
			return VariableStatement.of(equal ^ (getEquivalenceType() == EquationType.EQUAL
							|| getEquivalenceType() == InequalityType.GREATER_EQUAL || getEquivalenceType() == InequalityType.LESS_EQUAL));
		if (getEquivalenceType() instanceof EquationType equationType)
			return new Equation<>(leftSimplified, equationType, rightSimplified);
		else if (getEquivalenceType() instanceof InequalityType inequalityType)
			return new Inequality<>(leftSimplified, inequalityType, rightSimplified);
		else
			return Statement.empty();
	}

	@Override
	public List<Statement> getChildren() {
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

}
