package bran.mathexprs.treeparts.operators;

import bran.logic.statements.VariableStatement;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Fork;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.FunctionExpression;

import java.util.*;

import static bran.mathexprs.treeparts.operators.Operator.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.*;

public class OperatorExpression extends Expression implements Fork<Expression, Operator, Expression> {

	private Expression left;
	private final Operator operator;
	private Expression right;

	/**
	 * for x^p, if x doesn't exist and p is 0, then x^p DOES exist (==1) See: {@link java.lang.StrictMath#pow pow}
	 */
	public OperatorExpression(final Expression left, final Operator operator, final Expression right) {
		super(operator == POW ? operator.domain(left, right)
					  		  :  left.getDomainConditions().and(right.getDomainConditions()).and(operator.domain(left, right)));
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public Expression getLeft() {
		return left;
	}

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public Expression getRight() {
		return right;
	}

	@Override
	public double evaluate() {
		return operator.operate(left.evaluate(), right.evaluate());
	}

	@Override
	public Expression derive() {
		return operator.derive(left, right);
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return left.respect(respectsTo) || right.respect(respectsTo);
	}

	@Override
	public void replaceAll(final Expression approaches, final Expression approached) {
		if (left.equals(approaches))
			left = approached;
		else
			left.replaceAll(approaches, approached);
		if (right.equals(approaches))
			right = approached;
		else
			right.replaceAll(approaches, approached);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		godelNumbers.push(GodelNumberSymbols.LEFT);
		left.appendGodelNumbers(godelNumbers, variables);
		// godelNumbers.push(GodelNumberSymbols.RIGHT);
		godelNumbers.push(switch (operator) {
			case MUL -> GodelNumberSymbols.TIMES;
			case ADD -> GodelNumberSymbols.PLUS;
			default -> GodelNumberSymbols.SYNTAX_ERROR;
		});
		// godelNumbers.push(GodelNumberSymbols.LEFT);
		right.appendGodelNumbers(godelNumbers, variables);
		godelNumbers.push(GodelNumberSymbols.RIGHT);
		boolean leftParens;
		boolean rightParens;
			if ((left.equals(Constant.ZERO) && operator == SUB) || (left.equals(Constant.NEG_ONE) && operator == MUL)) {
				if (right instanceof OperatorExpression && ExpressionOperatorType.AS.precedence() <= operator.getOrder())
					leftParens = true;
//				return '-' + rightString;
			}
			boolean leftGiven = true;
			boolean rightGiven = true;
			if (left instanceof OperatorExpression leftOperator) {
				if (leftOperator.getOperator().getOrder() < operator.getOrder()) {
					leftParens = true;
				} else
					leftGiven = false;
			}
			if (right instanceof OperatorExpression rightOperator) {
				if ((rightOperator.getOperator().getOrder() < operator.getOrder()
						&& !(rightOperator.hideMultiply()))
						|| (rightOperator.getOperator().getOrder() == operator.getOrder()
						&& !rightOperator.getOperator().isCommutative())) {
					rightParens = true;
				} else
					rightGiven = false;
			}
//			if (leftGiven && rightGiven && operator == MUL
//					&& !(right instanceof Constant rightConstant && (left instanceof FunctionExpression || left instanceof Constant || rightConstant.evaluate() < 0))
//					&& !(left instanceof Variable && right instanceof Variable))
//				// && !(left instanceof Variable leftVariable && right instanceof Variable rightVariable && leftVariable.equals(rightVariable)))
//				return leftString + rightString;
			//return leftString + " " + operator + " " + rightString;


	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> variables = new HashSet<>();
		variables.addAll(left.getVariables());
		variables.addAll(right.getVariables());
		return variables;
	}

	// @Override
	// public Expression clone() {
	// 	return new OperatorExpression(left.clone(), operator, right.clone());
	// }

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof OperatorExpression opExp &&
				operator == opExp.getOperator() && left.equals(opExp.getLeft()) && right.equals(opExp.getRight()));
	}

	@Override
	public OperatorExpression reciprocal() {
		if (operator == DIV)
			return right.div(left);
		else
			return super.reciprocal();
	}

	@Override
	public String toFullString() {
		if ((left.equals(Constant.ZERO) && operator == SUB) || (left.equals(Constant.NEG_ONE) && operator == MUL))
			return "-" + right; // negative is a visual illusion
		else if (operator == MUL && !(right instanceof Constant rightConstant && (left instanceof Constant || rightConstant.evaluate() < 0)))
			return "(" + left + right + ')';
		return "(" + left + " " + operator + " " + right + ')';
	}

	@Override
	public String toString() {
		String leftString = left.toString();
		String rightString = right.toString();
		if ((left.equals(Constant.ZERO) && operator == SUB) || (left.equals(Constant.NEG_ONE) && operator == MUL)) {
			if (right instanceof OperatorExpression
				&& ExpressionOperatorType.AS.precedence() <= operator.getOrder())
				rightString = parens(rightString);
			return '-' + rightString;
		}
		boolean leftGiven = true;
		boolean rightGiven = true;
		if (left instanceof OperatorExpression leftOperator) {
			if (leftOperator.getOperator().getOrder() < operator.getOrder()) {
					leftString = parens(leftString);
			} else
				leftGiven = false;
		}
		if (right instanceof OperatorExpression rightOperator) {
			if ((rightOperator.getOperator().getOrder() < operator.getOrder()
				&& !(rightOperator.hideMultiply()))
				|| (rightOperator.getOperator().getOrder() == operator.getOrder()
					&& !rightOperator.getOperator().isCommutative())) {
				rightString = parens(rightString);
			} else
				rightGiven = false;
		}
		if (leftGiven && rightGiven && operator == MUL
			&& !(right instanceof Constant rightConstant && (left instanceof FunctionExpression || left instanceof Constant || rightConstant.evaluate() < 0))
			&& !(left instanceof Variable && right instanceof Variable))
			// && !(left instanceof Variable leftVariable && right instanceof Variable rightVariable && leftVariable.equals(rightVariable)))
			return leftString + rightString;
		return leftString + " " + operator + " " + rightString;
	}

	boolean hideMultiply() {
		return !(left instanceof OperatorExpression leftOperator && (leftOperator.getOperator().getOrder() >= operator.getOrder()))
		&& !(right instanceof OperatorExpression rightOperator && (rightOperator.getOperator().getOrder() >= operator.getOrder() || !rightOperator.hideMultiply()))
		&& (operator.getOrder() == MUL.getOrder() && !(right instanceof Constant rightConstant && (left instanceof Constant || rightConstant.evaluate() < 0)));
	}

	/*
	 * notes:
	 *
	 * no super calls; this is handled recursively.
	 * two powers multiplied/divided by each other -- common bases
	 *
	 * detect a multiplication between two rations and a division between two functions of multiplication and also adding
	 */
	@Override
	public Expression simplified() {
		return simplifiedNoDomain().limitDomain(domainConditions); // TODO Set new domain???
	}

	private Expression simplifiedNoDomain() {
		return simplifiedNoDomain(left.simplified(), right.simplified()); // TODO maybe simplifiedNoDomain()'s (does simplification limit domain?)
	}

	public Expression simplified(Expression leftSimplified, Expression rightSimplified) {
		return simplifiedNoDomain(leftSimplified, rightSimplified).limitDomain(domainConditions);
	}

	/**
	 * to pass in the parameters manually if they were guaranteed to already have been simplified
	 */
	private Expression simplifiedNoDomain(Expression leftSimplified, Expression rightSimplified) {
		if (leftSimplified instanceof Constant && rightSimplified instanceof Constant rightConst
			&& !((operator == DIV || operator == MUL) && rightConst.equals(Constant.ZERO)))
			return Constant.of(operator.operate(leftSimplified.evaluate(), rightSimplified.evaluate()));
		switch (operator) {
			case POW:
				if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.equals(Constant.ZERO))
						return Constant.ONE;
					else if (rightConstant.equals(Constant.ONE))
						return leftSimplified;
				}
				else if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return Constant.ZERO;
					else if (leftConstant.equals(Constant.ONE))
						return rightSimplified;
				}
				else if (rightSimplified instanceof FunctionExpression rightFunction)
					if (rightFunction.getFunction() == LOG) {
						if (leftSimplified.equals(rightFunction.getChildren()[0]))
							return rightFunction.getChildren()[1];
					}
					else if (rightFunction.getFunction() == LN)
						if (Constant.E.equals(rightFunction.getChildren()[0]))
							return rightFunction.getChildren()[1];
				break;
			case MUL: // a^b * a = a^(b + 1)
				if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return Constant.ZERO;
					else if (leftConstant.equals(Constant.ONE))
						return rightSimplified;
					else if (leftConstant.equals(Constant.NEG_ONE))
						return rightSimplified.negate();
					else if (rightSimplified instanceof OperatorExpression rightOperator)
						if (rightOperator.getLeft() instanceof Constant rightLeftConstant) {
							if (rightOperator.getOperator() == MUL)
								return Constant.of(leftConstant.evaluate() * rightLeftConstant.evaluate()).times(rightOperator.getRight());
							else if (rightOperator.getOperator() == DIV)
								return Constant.of(leftConstant.evaluate() * rightLeftConstant.evaluate()).div(rightOperator.getRight());
						} else if (rightOperator.getRight() instanceof Constant rightRightConstant) {
							if (rightOperator.getOperator() == MUL)
								return rightOperator.getLeft().times(Constant.of(leftConstant.evaluate() * rightRightConstant.evaluate()));
							else if (rightOperator.getOperator() == DIV)
								return rightOperator.getLeft().times(Constant.of(leftConstant.evaluate() / rightRightConstant.evaluate()));
						}
				} else if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.equals(Constant.ZERO))
						return Constant.ZERO;
					else if (rightConstant.equals(Constant.ONE))
						return leftSimplified;
					else if (rightConstant.equals(Constant.NEG_ONE))
						return leftSimplified.negate();
					else if (leftSimplified instanceof OperatorExpression leftOperator)
						if (leftOperator.getLeft() instanceof Constant leftLeftConstant) {
							if (leftOperator.getOperator() == MUL)
								return Constant.of(leftLeftConstant.evaluate() * rightConstant.evaluate()).times(leftOperator.getRight());
							else if (leftOperator.getOperator() == DIV)
									return Constant.of(leftLeftConstant.evaluate() * rightConstant.evaluate()).div(leftOperator.getRight());
						} else if (leftOperator.getRight() instanceof Constant leftRightConstant) {
							if (leftOperator.getOperator() == MUL)
								return leftOperator.getLeft().times(Constant.of(rightConstant.evaluate() * leftRightConstant.evaluate()));
							else if (leftOperator.getOperator() == DIV)
								return leftOperator.getLeft().times(Constant.of(rightConstant.evaluate() / leftRightConstant.evaluate()));
						}
				} else if (leftSimplified instanceof OperatorExpression leftOperator) {
					if (leftOperator.getOperator() == POW) {
						if (rightSimplified instanceof OperatorExpression rightOperator) {
							if (rightOperator.getOperator() == POW)
								if (leftOperator.getLeft().equals(rightOperator.getLeft()))
									return leftOperator.getLeft().pow(leftOperator.getRight().plus(rightOperator.getRight()).simplified());
						} else if (leftOperator.getLeft().equals(rightSimplified))
							return leftOperator.getLeft().pow(leftOperator.getRight().plus(Constant.ONE).simplified());
					} else if (leftOperator.getOperator() == DIV) {
						return (leftOperator.getLeft().times(rightSimplified).simplified(leftOperator.getLeft(), rightSimplified)).div(leftOperator.getRight());
					}
				} else if (rightSimplified instanceof OperatorExpression rightOperator) {
					if (rightOperator.getOperator() == POW) {
						if (rightOperator.getLeft().equals(leftSimplified))
							return rightOperator.getLeft().pow(rightOperator.getRight().plus(Constant.ONE).simplified());
					} else if (rightOperator.getOperator() == DIV)
						return (leftSimplified.times(rightOperator.getLeft()).simplified(leftSimplified, rightOperator.getLeft())).div(rightOperator.getRight());
				}
				if (leftSimplified.equals(rightSimplified))
					return leftSimplified.squared();
				if (rightSimplified instanceof Constant && !(leftSimplified instanceof Constant))
					return rightSimplified.times(leftSimplified);
				break;
			case DIV:
				FactorParts divFactorParts = factor(leftSimplified, rightSimplified);
				if (divFactorParts != null)
					return divFactorParts.leftPart.div(divFactorParts.rightPart).simplified();
				else if (leftSimplified.equals(rightSimplified))
					return Constant.ONE;
				else if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return Constant.ZERO;
					else if (rightSimplified instanceof OperatorExpression rightOperator)
						if (rightOperator.getLeft() instanceof Constant rightLeftConstant) {
							if (rightOperator.getOperator() == MUL)
								return Constant.of(leftConstant.evaluate() / rightLeftConstant.evaluate()).div(rightOperator.getRight());
							else if (rightOperator.getOperator() == DIV)
								return Constant.of(leftConstant.evaluate() / rightLeftConstant.evaluate()).times(rightOperator.getRight());
						} else if (rightOperator.getRight() instanceof Constant rightRightConstant) {
							if (rightOperator.getOperator() == MUL)
								return Constant.of(leftConstant.evaluate() / rightRightConstant.evaluate()).div(rightOperator.getLeft());
							else if (rightOperator.getOperator() == DIV)
								return Constant.of(leftConstant.evaluate() * rightRightConstant.evaluate()).div(rightOperator.getLeft());
						}
				} else if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.equals(Constant.ONE))
						return leftSimplified;
					else if (leftSimplified instanceof OperatorExpression leftOperator)
						if (leftOperator.getLeft() instanceof Constant leftLeftConstant) {
							if (leftOperator.getOperator() == MUL)
								return Constant.of(leftLeftConstant.evaluate() / rightConstant.evaluate()).times(leftOperator.getRight());
							else if (leftOperator.getOperator() == DIV)
								return Constant.of(leftLeftConstant.evaluate() / rightConstant.evaluate()).div(leftOperator.getRight());
						} else if (leftOperator.getRight() instanceof Constant leftRightConstant) {
							if (leftOperator.getOperator() == MUL)
								return leftOperator.getLeft().times(Constant.of(leftRightConstant.evaluate() / rightConstant.evaluate()));
							else if (leftOperator.getOperator() == DIV)
								return leftOperator.getLeft().div(Constant.of(leftRightConstant.evaluate() * rightConstant.evaluate()));
						}
				} else if (leftSimplified instanceof OperatorExpression leftOperator) {
					if (rightSimplified instanceof OperatorExpression rightOperator) {
						if (leftOperator.getOperator() == POW) {
							if (rightOperator.getOperator() == POW)
								if (leftOperator.getLeft().equals(rightOperator.getLeft()))
									return leftOperator.getLeft().pow(leftOperator.getRight().minus(rightOperator.getRight()).simplified());
						} else if (leftOperator.getOperator() == DIV) {
							if (rightOperator.getOperator() == DIV) { // (a / b) / (c / d)
								return (leftOperator.getLeft().times(rightOperator.getRight()).simplified())
											   .div(leftOperator.getRight().times(rightOperator.getLeft()).simplified());
							} else { // (a / b) / c
								return leftOperator.getLeft().div(leftOperator.getRight().times(rightOperator).simplified());
							}
						}
					} else {
						if (leftOperator.getLeft().equals(rightSimplified))
							if (leftOperator.getOperator() == POW)
								return leftOperator.getLeft().pow(leftOperator.getRight().minus(Constant.ONE).simplified());
						if (leftOperator.getOperator() == DIV) // (a / b) / c
							return leftOperator.getLeft().div(leftOperator.getRight().times(rightSimplified).simplified());
					}
				} else if (rightSimplified instanceof OperatorExpression rightOperator) {
					if (rightOperator.getOperator() == POW) {
						if (rightOperator.getLeft().equals(leftSimplified))
							return rightOperator.getLeft().pow(rightOperator.getRight().minus(Constant.ONE).simplified());
						// if (rightOperator.getRight() instanceof Constant rightPower && rightPower.evaluate() < 0) // Option: Negative exponent denominator
						// 	return leftSimplified.times(rightOperator.pow(Constant.of(-rightPower.evaluate())));
					} else if (rightOperator.getOperator() == DIV) // a / (b / c)
						return (leftSimplified.times(rightOperator.getRight()).simplified()).div(rightOperator.getLeft());
				}
				break;
			case MOD:
				if (leftSimplified instanceof Constant leftConstant)
					if (leftConstant.equals(Constant.ZERO))
						return Constant.ZERO;
				break;
			case ADD: // (a +- b) +- a | a +- (a +- b)
				// if left operator + - (right anything?) or if right operator + -
				FactorParts addFactorParts = factor(leftSimplified, rightSimplified);
				if (addFactorParts != null)
					return addFactorParts.factor.times(addFactorParts.leftPart.plus(addFactorParts.rightPart)).simplified();
				if (leftSimplified instanceof FunctionExpression leftFunction && rightSimplified instanceof FunctionExpression rightFunction) {
					if (( leftFunction.getFunction() == LN ||  leftFunction.getFunction() == LOG &&  leftFunction.getChildren()[0].equals(Constant.E))
					 && (rightFunction.getFunction() == LN || rightFunction.getFunction() == LOG && rightFunction.getChildren()[0].equals(Constant.E)))
						return LN.ofS(left.times(right).simplified());
					else if (leftFunction.getFunction() == LOG && rightFunction.getFunction() == LOG && leftFunction.getChildren()[0].equals(rightFunction.getChildren()[1]))
						return LOG.ofS(leftFunction.getChildren()[0], left.times(right).simplified());
				} // TODO extract exponent power
				else if (leftSimplified instanceof OperatorExpression leftOperator && rightSimplified instanceof OperatorExpression rightOperator) {
					if (leftOperator.getOperator() == DIV && rightOperator.getOperator() == DIV && leftOperator.getRight().equals(rightOperator.getRight()))
							return leftOperator.plus(rightOperator).simplified().div(leftOperator.getRight());
					if (leftOperator.getOperator() == POW && rightOperator.getOperator() == POW
						&&  leftOperator.getLeft() instanceof FunctionExpression leftOpFunction
						&& rightOperator.getLeft() instanceof FunctionExpression rightOpFunction
						&& (leftOpFunction.getFunction() == SIN && rightOpFunction.getFunction() == COS
							|| leftOpFunction.getFunction() == COS && rightOpFunction.getFunction() == SIN)
						&& leftOperator.getRight().equals(Constant.TWO) && rightOperator.getRight().equals(Constant.TWO)
						&& leftOperator.getRight().equals(rightOperator.getRight()))
						return Constant.ONE; // sin^2 + cos^2 = 1
				}
				else if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.evaluate() < 0) // may not work with edge values
						return leftSimplified.minus(new Constant(-rightConstant.evaluate()));
					else if (rightConstant.evaluate() == 0.0)
						return leftSimplified;
				} else if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.evaluate() == 0.0)
						return rightSimplified;
				}
				if (leftSimplified.equals(rightSimplified))
					return Constant.TWO.times(leftSimplified);
				break;
			case SUB: // TODO and ADD: not deep enough
				FactorParts subFactorParts = factor(leftSimplified, rightSimplified);
				if (subFactorParts != null) {
					return subFactorParts.factor.times(subFactorParts.leftPart.minus(subFactorParts.rightPart)).simplified();
				}
				if (leftSimplified instanceof FunctionExpression leftFunction && rightSimplified instanceof FunctionExpression rightFunction) {
					if ((leftFunction.getFunction() == LN || leftFunction.getFunction() == LOG && leftFunction.getChildren()[0].equals(Constant.E))
						&& (rightFunction.getFunction() == LN || rightFunction.getFunction() == LOG && rightFunction.getChildren()[0].equals(Constant.E)))
						return LN.ofS(left.div(right).simplified());
					else if (leftFunction.getFunction() == LOG && rightFunction.getFunction() == LOG && leftFunction.getChildren()[0].equals(rightFunction.getChildren()[1]))
						return LOG.ofS(leftFunction.getChildren()[0], left.div(right).simplified());
				}
				else if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.evaluate() < 0) // may not work with edge values
						return leftSimplified.plus(new Constant(-rightConstant.evaluate()));
					else if (rightConstant.evaluate() == 0.0)
						return leftSimplified;
				}
				if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return new OperatorExpression(Constant.NEG_ONE, MUL, rightSimplified);
				}
				if (leftSimplified.equals(rightSimplified))
					return Constant.ZERO;
				break;
			default:
		}
		return new OperatorExpression(leftSimplified, operator, rightSimplified);
	}

	private static record FactorParts(Expression factor, Expression leftPart, Expression rightPart) { }

	private FactorParts factor0(Expression leftSimplified, Expression rightSimplified) {
		if (leftSimplified instanceof OperatorExpression leftOperator) { // tree search for all mults
			if (leftOperator.getOperator() == MUL) {
				if (rightSimplified instanceof OperatorExpression rightOperator && rightOperator.getOperator() == MUL) {
					if (leftOperator.getLeft().equals(rightOperator.getLeft()))
						return new FactorParts(leftOperator.getLeft(), leftOperator.getRight(), rightOperator.getRight());
					else if (leftOperator.getLeft().equals(rightOperator.getRight()))
						return new FactorParts(leftOperator.getLeft(), leftOperator.getRight(), rightOperator.getLeft());
					else if (leftOperator.getRight().equals(rightOperator.getLeft()))
						return new FactorParts(leftOperator.getRight(), leftOperator.getLeft(), rightOperator.getRight());
					else if (leftOperator.getRight().equals(rightOperator.getRight()))
						return new FactorParts(leftOperator.getRight(), leftOperator.getLeft(), rightOperator.getLeft());
				} else {
					if (leftOperator.getLeft().equals(rightSimplified))
						return new FactorParts(leftOperator.getLeft(), leftOperator.getRight(), Constant.ONE);
					else if (leftOperator.getRight().equals(rightSimplified))
						return new FactorParts(leftOperator.getRight(), Constant.ONE, leftOperator.getLeft());
				}
			}
		} else if (rightSimplified instanceof OperatorExpression rightOperator && rightOperator.getOperator() == MUL) {
			if (rightOperator.getLeft().equals(leftSimplified))
				return new FactorParts(rightOperator.getLeft(), rightOperator.getRight(), Constant.ONE);
			else if (rightOperator.getRight().equals(leftSimplified))
				return new FactorParts(rightOperator.getRight(), Constant.ONE, rightOperator.getLeft());
		}
		return null;
	}

	private Expression commutativeDeepSearchAddSub(Expression expression) { // TODO
		Collection<Expression> terms = new ArrayList<>();
		commutativeDeepSearchAddSub(expression, terms);

		// try to factor with all terms?
		return expression;
	}

	private void commutativeDeepSearchAddSub(Expression expression, Collection<Expression> terms) {
		if (expression instanceof OperatorExpression operatorExpression) {
			if ((operatorExpression.getOperator() == ADD || operatorExpression.getOperator() == SUB)) {
				commutativeDeepSearchAddSub(operatorExpression.getLeft(), terms);
				commutativeDeepSearchAddSub(operatorExpression.getRight(), terms);
			}
		} else
			terms.add(expression);

	}

	public FactorParts factor(Expression leftExp, Expression rightExp) {
		return new FactorSystem(leftExp, rightExp).factor();
	}

	private static class FactorSystem { // MUTABLE
		Collection<Factor> factors;
		Collection<Factor> leftFactors;
		Collection<Factor> rightFactors;

		public FactorSystem(Expression leftExp, Expression rightExp) {
			this.factors = new ArrayList<>();
			this.leftFactors = new ArrayList<>();
			seekFactors(leftExp, false, leftFactors);
			this.rightFactors = new ArrayList<>();
			seekFactors(rightExp, false, rightFactors);
		}

		private Factor leftFactor;
		private Factor rightFactor;

		private Iterator<Factor> leftIter;
		private Iterator<Factor> rightIter;

		public FactorParts factor() {
			boolean factorable = false;
			if (leftFactors.size() + rightFactors.size() < 3)
				return null;
			for (leftIter = leftFactors.iterator(); leftIter.hasNext(); ) {
				this.leftFactor = leftIter.next();
				for (rightIter = rightFactors.iterator(); rightIter.hasNext(); ) {
					this.rightFactor = rightIter.next();
					if (leftFactor.base.equals(rightFactor.base)) {
						boolean foundFactor;
						// if (leftFactor.constantPower && rightFactor.constantPower)
						// 	if (leftFactor.powerValue < rightFactor.powerValue) {
						// 		foundFactor = factorOutLeft(rightFactor.powerValue - leftFactor.powerValue);
						// 	} else {
						// 		foundFactor = factorOutRight(leftFactor.powerValue - rightFactor.powerValue);
						// 	}
							// if (leftFactor.inverse) {
							// 		if (rightFactor.inverse)
							// 			if (leftFactor.powerValue > rightFactor.powerValue) {
							// 				rightFactor.inverse = false;
							// 				factorOutLeft(leftFactor.powerValue - rightFactor.powerValue);
							// 			} else {
							// 				leftFactor.inverse = false;
							// 				factorOutRight(rightFactor.powerValue - leftFactor.powerValue);
							// 			}
							// 		else {
							// 			factorOutRight(rightFactor.powerValue + leftFactor.powerValue);
							// 		}
							// 	} else {
							// 		if (rightFactor.inverse) {
							// 			factorOutLeft(leftFactor.powerValue + rightFactor.powerValue);
							// 		} else {
							// 			if (leftFactor.powerValue > rightFactor.powerValue) {
							// 				factorOutRight(leftFactor.powerValue - rightFactor.powerValue);
							// 			} else {
							// 				factorOutLeft(rightFactor.powerValue - leftFactor.powerValue);
							// 			}
							// 		}
							// 	}
						// else { // simplification check
							if ((leftFactor.inverse == rightFactor.inverse) && leftFactor.power.equals(rightFactor.power)) {
								factors.add(leftFactor);
								leftIter.remove();
								rightIter.remove();

								foundFactor = true;
							}
							else if (leftFactor.inverse) {
								// l, l + r
								if (rightFactor.inverse) {

											// CROSS MULTIPLICATION //
									// Expression power = leftFactor.power.plus(rightFactor.power);
									// Expression simplifiedPower = power.simplified();
									// if (!power.equals(simplifiedPower)) {
									// 	final Expression rightPower = rightFactor.power;
									// 	rightFactor.power = leftFactor.power;
									// 	leftFactor.power = rightPower;
									// 	rightFactor.inverse = false;
									// 	leftFactor.inverse = false;
									// 	factors.add(new Factor(leftFactor.base, simplifiedPower, true));
									// 	foundFactor = true;
									// } else {
										if (leftFactor.power instanceof Constant leftConstantPower && rightFactor.power instanceof Constant rightConstantPower)
											if (leftConstantPower.compareTo(rightConstantPower) < 0)
												foundFactor = factorOutLeftSimplified(rightConstantPower.minus(leftConstantPower));
											else
												foundFactor = factorOutRightSimplified(leftConstantPower.minus(rightConstantPower));
										else
											// if (leftFactor.constantPower) { // r, r - l
											// 	foundFactor = factorOutRightSimplified(rightFactor.power.minus(leftFactor.power));
											// } else { // l, l - r
											foundFactor = factorOutLeftSimplified(rightFactor.power.minus(leftFactor.power));
										// }
									// }
								} else
									foundFactor = factorOutLeftSimplified(leftFactor.power.plus(rightFactor.power));

							} else { // r, l + r
								if (rightFactor.inverse)
									foundFactor = factorOutRightSimplified(leftFactor.power.plus(rightFactor.power));
								else { // l + r, l ; r OR r, r - l OR l, l - r
									if (leftFactor.power instanceof Constant leftConstantPower && rightFactor.power instanceof Constant rightConstantPower)
										if (leftConstantPower.compareTo(rightConstantPower) < 0)
											foundFactor = factorOutLeftSimplified(rightConstantPower.minus(leftConstantPower));
										else
											foundFactor = factorOutRightSimplified(leftConstantPower.minus(rightConstantPower));
									else {
										if (leftFactor.power instanceof Constant)
											foundFactor = factorOutLeftSimplified(rightFactor.power.minus(leftFactor.power));
										else
											foundFactor = factorOutRightSimplified(leftFactor.power.minus(rightFactor.power));
									}
									// if (leftFactor.constantPower)
									// 	; // l, r - l
									// else
									// 	; // r, l - r
								}
							}
						// }
						if (foundFactor)
							factorable = true;
					}
				}
			}
			return factorable ? new FactorParts(combineInvertibleFactors(factors), combineInvertibleFactors(leftFactors), combineInvertibleFactors(rightFactors)) : null;
		}

		/**
		 * @param power - new power to check if simplified is better
		 */
		private boolean factorOutLeftSimplified(final Expression power) {
			Expression simplifiedPower = power.simplified();
			if (!power.equals(simplifiedPower)) {
				rightFactor.power = simplifiedPower;
				return factorOutLeft();
			}
			return false;
		}

		private boolean factorOutRightSimplified(final Expression power) {
			Expression simplifiedPower = power.simplified();
			if (!power.equals(simplifiedPower)) {
				leftFactor.power = simplifiedPower;
				return factorOutRight();
			}
			return false;
		}

		/**
		 // * @param powerValue - new power for remaining factor
		 */
		private boolean factorOutLeft() {
			// rightFactor.powerValue = powerValue;
			factors.add(leftFactor);
			leftIter.remove();
			return true;
		}

		// final double powerValue
		private boolean factorOutRight() {
			// leftFactor.powerValue = powerValue;
			factors.add(rightFactor);
			rightIter.remove();
			return true;
		}

		private void seekFactors(final Expression exp, boolean inverse, Collection<Factor> pool) {
			if (exp instanceof OperatorExpression operatorExp && (operatorExp.getOperator() == MUL || operatorExp.getOperator() == DIV)) {
				seekFactors(operatorExp.getLeft(), inverse, pool);
				seekFactors(operatorExp.getRight(), inverse ^ (operatorExp.getOperator() == DIV), pool);
			} else {
				pool.add(new Factor(exp, inverse));
			}
		}

		private Expression combineInvertibleFactors(Collection<Factor> factors) {
			Collection<Expression> numeratorFactors = new ArrayList<>();
			Collection<Expression> denominatorFactors = new ArrayList<>();
			for (Factor f : factors) {
				if (f.inverse)
					denominatorFactors.add(f.reconstruct());
				else
					numeratorFactors.add(f.reconstruct());
			}
			final Expression numerator = combineFactors(numeratorFactors);
			if (denominatorFactors.size() == 0)
				return numerator;
			final Expression denominator = combineFactors(denominatorFactors);
			return numerator.div(denominator);
		}

		private Expression combineFactors(Collection<Expression> factors) {
			if (factors.size() == 0)
				return Constant.ONE;
			final Iterator<Expression> iterator = factors.iterator();
			Expression acc = iterator.next();
			while (iterator.hasNext()) {
				acc = acc.times(iterator.next());
			}
			return acc;
		}

		static class Factor {
			// final Expression source;
			final Expression base;
				  Expression power;
				  boolean inverse;
			// final boolean constantPower;
			// 	  double powerValue;
				  // boolean noPower;


			public Factor(final Expression base, final Expression power, final boolean inverse) {
				this.base = base;
				this.power = power;
				this.inverse = inverse;
			}

			public Factor(final Expression expression, final boolean inverse) {
				// source = expression;
				this.inverse = inverse;
				// noPower = false;
				if (expression instanceof OperatorExpression operatorExp && operatorExp.getOperator() == POW) {
					base = operatorExp.getLeft();
					power = operatorExp.getRight();
					if (power instanceof Constant powerConstant) {
						// constantPower = true;
						double powerValue = powerConstant.evaluate();
						if (powerValue < 0) {
							power = Constant.of(-powerValue);
							this.inverse = !inverse;
						}
					}

					// if (power instanceof Constant powerConstant) {
					// 	// constantPower = true;
					// 	powerValue = powerConstant.evaluate();
					// 	if (inverse)
					// 		powerValue = -powerValue;
					// 	// if (powerValue == 1)
					// 	// 	noPower = true;
					// 	this.inverse = false;
					// 	// final double val = powerConstant.evaluate();
					// 	// this.inverse = (powerValue < 0) ^ inverse;
					// 	// powerValue = Math.abs(val);
					// 	// if (powerValue == 1)
					// 	// 	noPower = true;
					// 	// this.inverse = false;
					// } else {
					// 	constantPower = false;
					// 	powerValue = 0.0; // N/A - null
					// }
				} else {
					base = expression;
					power = Constant.ONE; // for expression power factoring
					// constantPower = true;
					// powerValue = 1;
					// noPower = true;
					// this.inverse = false;
				}
			}

			public Expression reconstruct() {
				// if (constantPower) {
				// 	if (powerValue == 1)
				// 		return base;
				// 	else
				// 		return base.pow(Constant.of(powerValue));
				// } else {
					if (power == Constant.ONE)
						return base;
					else
						return base.pow(power);
				// }
			}

			// private static class Power {
			// 	boolean exists;
			// 	public double powerValue() {
			// 	}
			// 	public Expression power() {
			// 	}
			// }

		}

	}

}
