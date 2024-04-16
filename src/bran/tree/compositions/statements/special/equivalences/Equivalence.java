package bran.tree.compositions.statements.special.equivalences;

import bran.exceptions.IllegalInverseExpressionException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.StatementOperation;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.SpecialStatement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.inequality.Inequality;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;
import bran.tree.structure.MonoTypeFork;

import java.util.*;
import java.util.function.Function;

public abstract class Equivalence extends SpecialStatement implements MonoTypeFork<Boolean, Expression, EquivalenceType> {

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
		else if (equivalenceType instanceof EquivalenceTypeImpl equivalenceTypeImpl) {
			return new EquivalenceImpl(left, equivalenceTypeImpl, right);
		}
		return null;
	}

	public Expression getLeft() {
		return left;
	}

	@Override
	public EquivalenceType getOperator() {
		return getEquivalenceType();
	}

	public abstract EquivalenceType getEquivalenceType();

	public Expression getRight() {
		return right;
	}

	public Set toSet() {
		return Set.emptySet();
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
			return VariableStatement.of(getEquivalenceType().equal());
		/*
			try to isolate variable
			y+3 > x			x < y+3			y > x-3			3 > x-y
			xy < 3			x < 2/y			y < 2/x			3 > xy
			3sin(x) > y		x > asin(y)		y < sin(x)		3 > y/sin(x)
			in a collection of commutative bound equivalences:
				for each variable v:
					for each possible PAIR :
						isolate v, take difference d of other side of p and simplify s
							if s is a constant (or any sort of valid compareTo)
								mark p as simplifiable for v
				combine the marked p's. for "(pn, pm)" meaning they were marked as simplifiable for v,
					(p1, p2), (p2, p3) => (p1, p3); if a tool could perfectly simplify, this property would not be necessary
				simplify with Set logic
			pick the best simplification by v or the first one that could, if none then continue as before;
				it should be very rare that more than one variable can be simplified upon TODO is it possible?
		 */

		final ExpressionOperation oneSide = new ExpressionOperation(leftSimplified, ArithmeticOperator.SUB, rightSimplified);
		Expression oneSideSimplified;
		if (leftSimplified.equals(Constant.ZERO) || rightSimplified.equals(Constant.ZERO))
			oneSideSimplified = oneSide;
		else
			oneSideSimplified = oneSide.simplified(leftSimplified, rightSimplified);
		// final java.util.Set<Variable> oneSideVariables = oneSideSimplified.getVariables();
		// if (oneSideVariables.size() == 0)
		// 	return VariableStatement.of(getEquivalenceType().evaluate(oneSideSimplified, Constant.ZERO));
		// if (oneSideVariables.size() == 1) { // this, but for a community of Equivalences
		// 	oneSideVariables.iterator().next();
		// 	try {
		// 		/* x = */ final Expression inverse = oneSideSimplified.inverse(Constant.ZERO).simplified();
		// 		if (inverse.simplified() instanceof Constant invConst) {
		//
		// 		}
		// 	} catch (IllegalInverseExpressionException e) {
		//
		// 	}
		// }

		if (!oneSide.equals(oneSideSimplified)) { // TODO
			final java.util.Set<Variable> oneSideVariables = oneSideSimplified.getVariables();
			if (oneSideVariables.size() == 0)
				return VariableStatement.of(getEquivalenceType().evaluate(oneSideSimplified, Constant.ZERO));
			else if (oneSideVariables.size() == 1) {
				// 	oneSideVariables.iterator().next();
				final List<Expression> inverse = new ArrayList<>(oneSideSimplified.inverse(Constant.ZERO));
				final Variable theVar = oneSideVariables.iterator().next();
				final Function<Integer, Equivalence> equivSup = i -> Equivalence.of(theVar, getEquivalenceType(), inverse.get(i).simplified());

				equivSup.apply(0);
				Statement combinedStatements = equivSup.apply(0);
				for (int i = 1; i < inverse.size(); i++)
					combinedStatements = new StatementOperation(combinedStatements, LogicalOperator.AND, equivSup.apply(1));
				return combinedStatements;

			}
			Equivalence.of(oneSideSimplified, getEquivalenceType(), Constant.ZERO);
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
		}

		// if (getEquivalenceType() instanceof EquationType equationType)
		// 	return tryToEvaluate(new Equation(leftSimplified, equationType, rightSimplified));
		// else if (getEquivalenceType() instanceof InequalityType inequalityType)
		// 	return tryToEvaluate(new Inequality(leftSimplified, inequalityType, rightSimplified));
		// else
		// 	return Statement.empty();

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

		return Equivalence.of(leftSimplified, getEquivalenceType(), rightSimplified);

	}

	protected Statement tryToEvaluate(Equivalence equivalence) {
		try {
			return VariableStatement.of(equivalence.getTruth());
		} catch (IllegalInverseExpressionException | NullPointerException e) {
			return equivalence;
		}
	}

	@Override
	public List<Expression> getChildren() {
		return List.of(left, right);
	}

	// @Override
	// public List<? extends Composition> getChildren() {
	// 	List<Statement> children = new ArrayList<>();
	// 	// variables.addAll(left.getChildren());
	// 	// variables.addAll(right.getVariables());
	// 	return null;
	// }

	@Override
	public List<VariableStatement> getVariables() {
		// List<VariableStatement> variables = new ArrayList<>();
		// variables.addAll(left.getVariables());
		// variables.addAll(right.getVariables());
		// return variables;
		return List.of();
	}

}
