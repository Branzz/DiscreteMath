package bran.mathexprs;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.SpecialStatement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.operators.Operator;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.tree.Composition;

import java.util.List;

public abstract class Equivalence extends SpecialStatement {

	protected final Expression left;
	protected final Expression right;

	public Equivalence(final Expression left, final Expression right) {
		this.left = left;
		this.right = right;
	}

	public static Equivalence of(final Expression left, final EquivalenceType equivalenceType, final Expression right) {
		if (equivalenceType instanceof InequalityType inequalityType)
			return new Inequality(left, inequalityType, right);
		else if (equivalenceType instanceof EquationType equationType)
			return new Equation(left, equationType, right);
		return null;
	}

	public Expression getLeft() {
		return left;
	}

	public abstract EquivalenceType getEquivalenceType();

	public Expression getRight() {
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
		Expression leftSimplified = left.simplified();
		Expression rightSimplified = right.simplified();

		if (leftSimplified instanceof Constant && rightSimplified instanceof Constant)
			return VariableStatement.of(getEquivalenceType().evaluate(leftSimplified, rightSimplified));

		// if (leftSimplified instanceof Expression lExp && rightSimplified instanceof Expression rExp) {
		// 	final OperatorExpression.FactorParts factorParts = OperatorExpression.factor(lExp, rExp);
		// 	if (factorParts != null) {
		// 		leftSimplified = factorParts.leftPart();
		// 		rightSimplified = factorParts.rightPart();
		// 	}
		// }
		//TODO actually simplify an equivalence
		boolean equal = leftSimplified.equals(rightSimplified);
		// boolean notEqual = false;
		// if (leftSimplified instanceof Statement leftStatement && rightSimplified instanceof Statement rightStatement)
		// 	notEqual = leftStatement.equalsNot(rightStatement);
		if (equal /*|| notEqual*/)
			return VariableStatement.of(equal ^ (getEquivalenceType() == EquationType.EQUAL
							|| getEquivalenceType() == InequalityType.GREATER_EQUAL || getEquivalenceType() == InequalityType.LESS_EQUAL));
		final OperatorExpression oneSide = new OperatorExpression(leftSimplified, Operator.SUB, rightSimplified);
		final Expression oneSideSimplified = oneSide.simplified(leftSimplified, rightSimplified);
		if (!oneSide.equals(oneSideSimplified)) {
			leftSimplified = oneSideSimplified;
			rightSimplified = Constant.ZERO;
		}
		if (getEquivalenceType() instanceof EquationType equationType)
			return new Equation(leftSimplified, equationType, rightSimplified);
		else if (getEquivalenceType() instanceof InequalityType inequalityType)
			return new Inequality(leftSimplified, inequalityType, rightSimplified);
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
