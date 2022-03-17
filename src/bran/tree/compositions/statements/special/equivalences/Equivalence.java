package bran.tree.compositions.statements.special.equivalences;

import bran.exceptions.IllegalInverseExpressionException;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.regular.RangedSet;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.SpecialStatement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.Operator;
import bran.tree.compositions.expressions.operators.OperatorExpression;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.inequality.Inequality;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;

import java.util.*;

public abstract class Equivalence extends SpecialStatement {

	protected Expression left;
	protected Expression right;

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

	public abstract Set toSet();

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
			return VariableStatement.of(/*equal ^ */(getEquivalenceType() == EquationType.EQUAL
							|| getEquivalenceType() == InequalityType.GREATER_EQUAL || getEquivalenceType() == InequalityType.LESS_EQUAL));
		final OperatorExpression oneSide = new OperatorExpression(leftSimplified, Operator.SUB, rightSimplified);
		final Expression oneSideSimplified = oneSide.simplified(leftSimplified, rightSimplified);
		// if (!oneSide.equals(oneSideSimplified)) { TODO
		// 	// commutativeSearch(leftStatement, leftTerms, false);
		// 	// commutativeSearch(rightStatement, rightTerms, inverted);
		// 	List<OperatorExpression.Term> terms = new ArrayList<>();
		// 	OperatorExpression.commutativeSearch(oneSideSimplified, terms, false, Operator.ADD, Operator.SUB);
		// 	for (OperatorExpression.Term term : terms) {
		// 		if (term.inverted())
		// 			;
		// 		else
		// 			;
		// 	}
		// 	final Map<Boolean, List<OperatorExpression.Term>> sides
		// 			= terms.stream().collect(Collectors.groupingBy(OperatorExpression.Term::inverted, Collectors.toList()));
		//
		// 	leftSimplified = OperatorExpression.combine(sides.get(false), Operator.ADD);
		// 	rightSimplified = OperatorExpression.combine(sides.get(true), Operator.ADD);
		// }
		if (getEquivalenceType() instanceof EquationType equationType)
			return tryToEvaluate(new Equation(leftSimplified, equationType, rightSimplified));
		else if (getEquivalenceType() instanceof InequalityType inequalityType)
			return tryToEvaluate(new Inequality(leftSimplified, inequalityType, rightSimplified));
		else
			return Statement.empty();

		// RangedSet current = ranges.get(i);
		// for (int j = i + 1; j < size; j++) {
		// 	RangedSet other = ranges.get(j);
		// 	final int leftCompare = current.from().compareTo(other.from());
		// 	final int rightCompare = current.to().compareTo(other.to());
		// final int leftCompare = current.from().compareTo(other.from());
		// final int rightCompare = current.to().compareTo(other.to());
		// if (leftCompare < 0) {
		// 	if (rightCompare < 0) { // other is a subset of current
		// 		current = other; foundOverlap = true;
		// 	} else if (rightCompare > 0) {
		//
		// 	}
		// } else if (leftCompare > 0) {
		// 	if (rightCompare > 0) {
		// 		// final int rightTail = current.to().compareTo(other.from());
		// 		final int rightTail = current.from().compareTo(other.to());
		// 		if (rightTail < 0) {
		// 			current = new RangedSet(current.baseDomain(),
		// 									current.isFromInclusive(), current.from(),
		// 									other.isToInclusive(), other.to());
		// 		} else if (rightTail == 0) {
		// 			if (current.isFromInclusive() && other.isToInclusive())
		// 				current = new RangedSet()
		// 		}
		//
		// 	} else if (rightCompare < 0) { // current is a subset of other
		// 		foundOverlap = true;
		// 	}
		// }
		// if (foundOverlap)
		// 	insertIndex = Math.min(insertIndex, j);
		// }

	}

	protected Statement tryToEvaluate(final Equivalence equivalence) {
		try {
			return VariableStatement.of(equivalence.getTruth());
		} catch (IllegalInverseExpressionException | NullPointerException e) {
			return equivalence;
		}
	}

	@Override
	public List<Statement> getChildren() {
		List<Statement> variables = new ArrayList<>();
		// variables.addAll(left.getChildren());
		// variables.addAll(right.getVariables());
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		// List<VariableStatement> variables = new ArrayList<>();
		// variables.addAll(left.getVariables());
		// variables.addAll(right.getVariables());
		return null;
	}

}
