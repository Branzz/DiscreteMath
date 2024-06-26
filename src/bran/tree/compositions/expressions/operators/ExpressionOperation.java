package bran.tree.compositions.expressions.operators;

import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.structure.MonoTypeFork;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.functions.FunctionExpression;

import java.util.*;
import java.util.function.Function;

import static bran.tree.compositions.expressions.operators.ArithmeticOperator.*;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.*;

public class ExpressionOperation extends Expression implements MonoTypeFork<Double, Expression, ArithmeticOperator, ExpressionOperation> {

	private Expression left;
	private final ArithmeticOperator arithmeticOperator;
	private Expression right;

	public ExpressionOperation(Expression left, ArithmeticOperator arithmeticOperator, Expression right) {
		super(left.getDomainConditions().and(right.getDomainConditions()).and(arithmeticOperator.domain(left, right)));
		this.left = left;
		this.arithmeticOperator = arithmeticOperator;
		this.right = right;
	}

	@Override
	public TriFunction<Expression, ArithmeticOperator, Expression, ExpressionOperation> getter() {
		return ExpressionOperation::new;
	}

	@Override
	public Expression getLeft() {
		return left;
	}

	@Override
	public ArithmeticOperator getOperator() {
		return arithmeticOperator;
	}

	@Override
	public Expression getRight() {
		return right;
	}

	@Override
	public double evaluate() {
		return arithmeticOperator.operate(left, right);
	}

	@Override
	public Expression derive() {
		return arithmeticOperator.derive(left, right);
	}

	public <R, T> R traverse(T t, Function<T, R> function) {
		left.traverse(t, function);
		right.traverse(t, function);
		return function.apply(t);
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return left.respect(respectsTo) || right.respect(respectsTo);
	}

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		if (original.equals(left))
			left = (Expression) replacement;
		else
			left.replaceAll(original, replacement);
		if (original.equals(right))
			right = (Expression) replacement;
		else
			right.replaceAll(original, replacement);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		boolean leftParens = left instanceof ExpressionOperation leftOperator
							 && leftOperator.getOperator().godelOrder() < arithmeticOperator.godelOrder();
		boolean rightParens = right instanceof ExpressionOperation rightOperator &&
							  (rightOperator.getOperator().godelOrder() <= arithmeticOperator.godelOrder()
							   && arithmeticOperator.godelOperator() != GodelNumberSymbols.SYNTAX_ERROR);
		if (leftParens)
			godelBuilder.push(GodelNumberSymbols.LEFT);
		left.appendGodelNumbers(godelBuilder);
		if (leftParens)
			godelBuilder.push(GodelNumberSymbols.RIGHT);
		godelBuilder.push(arithmeticOperator.godelOperator());
		if (rightParens)
			godelBuilder.push(GodelNumberSymbols.LEFT);
		right.appendGodelNumbers(godelBuilder);
		if (rightParens)
			godelBuilder.push(GodelNumberSymbols.RIGHT);
	}

	@Override
	public Set<Variable> getVariables() {
		return Expression.combineVariableSets(left, right);
	}

	// @Override
	// public Expression clone() {
	// 	return new OperatorExpression(left.clone(), operator, right.clone());
	// }

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof ExpressionOperation opExp &&
				arithmeticOperator == opExp.getOperator() && left.equals(opExp.getLeft()) && right.equals(opExp.getRight()));
	}

	@Override
	public ExpressionOperation reciprocal() {
		if (arithmeticOperator == DIV)
			return right.div(left);
		else
			return Constant.ONE.div(this);
	}

	@Override
	public String toFullString() {
		return "(" + left.toFullString() + ' ' + arithmeticOperator + ' ' + right.toFullString() + ')';
	}

	/**
	 * @return full string, but with multiplication operator reduction
	 */
	public String toSemiFullString() {
		if ((left.equals(Constant.ZERO) && arithmeticOperator == SUB) || (left.equals(Constant.NEG_ONE) && arithmeticOperator == MUL))
			return "-" + right.toFullString(); // negative is a visual illusion
		else {
			if (arithmeticOperator == MUL) {
				if (right instanceof Constant && !(left instanceof Constant))
					return "(" + right.toFullString() + left.toFullString() + ')';
				else if (!(right instanceof Constant)) // left is a constant
					return "(" + left.toFullString() + right.toFullString() + ')'; // "2 * 3" != "23"
			}
		}
		return "(" + left.toFullString() + ' ' + arithmeticOperator + ' ' + right.toFullString() + ')';
	}

	@Override
	public String toString() {
		String leftString = left.toString();
		String rightString = right.toString();
		if ((left.equals(Constant.ZERO) && arithmeticOperator == SUB) || (left.equals(Constant.NEG_ONE) && arithmeticOperator == MUL)) {
			if (right instanceof ExpressionOperation
				&& ADD.level().precedence() <= arithmeticOperator.precedence())
				rightString = Composition.parens(rightString);
			return '-' + rightString;
		}
		boolean leftGiven = true;
		boolean rightGiven = true;
		if (left instanceof ExpressionOperation leftOperator) {
			if (leftOperator.getOperator().precedence() > arithmeticOperator.precedence()) {
					leftString = Composition.parens(leftString);
			} else
				leftGiven = false;
		}
		if (right instanceof ExpressionOperation rightOperator) {
			if ((rightOperator.getOperator().precedence() > arithmeticOperator.precedence()
				    && !(rightOperator.hideMultiply()))
				|| (rightOperator.getOperator().precedence() == arithmeticOperator.precedence()
					&& !rightOperator.getOperator().isCommutative())
				|| (arithmeticOperator == SUB)) {
				rightString = Composition.parens(rightString);
			} else
				rightGiven = false;
		}
		if (leftGiven && rightGiven && arithmeticOperator == MUL
			&& !(right instanceof Constant rightConstant &&
				   (left instanceof FunctionExpression || left instanceof Variable || left instanceof Constant || rightConstant.evaluate() < 0))
			&& !(left instanceof Variable && right instanceof Variable))
			// && !(left instanceof Variable leftVariable && right instanceof Variable rightVariable && leftVariable.equals(rightVariable)))
			return leftString + rightString;
		return leftString + " " + arithmeticOperator + " " + rightString;
	}

	boolean hideMultiply() {
		return !(left instanceof ExpressionOperation leftOperator && (leftOperator.getOperator().precedence() >= arithmeticOperator.precedence()))
		&& !(right instanceof ExpressionOperation rightOperator && (rightOperator.getOperator().precedence() >= arithmeticOperator.precedence() || !rightOperator.hideMultiply()))
		&& (arithmeticOperator.precedence() == MUL.precedence() && !(right instanceof Constant rightConstant && (left instanceof Constant || rightConstant.evaluate() < 0)));
	}

	/*
	 * notes:
	 *
	 * no super calls; this is handled recursively
	 * two powers multiplied/divided by each other -- common bases
	 *
	 * detect a multiplication between two rations and a division between two functions of multiplication and also adding
	 */
	@Override
	public Expression simplified() {
		return simplifiedNoDomain().limitDomain(domainConditions); // TODO Set new domain???
	}

	private Expression simplifiedNoDomain() {
		final Expression leftSimplified = left.simplified();
		final Expression rightSimplified = right.simplified();
		return nullToNew(simplifiedNoDomain(leftSimplified, rightSimplified), leftSimplified, rightSimplified); // TODO maybe simplifiedNoDomain()'s (does simplification limit domain?)
	}

	public Expression simplified(Expression leftSimplified, Expression rightSimplified) {
		return nullToNew(simplifiedNoDomain(leftSimplified, rightSimplified), leftSimplified, rightSimplified).limitDomain(domainConditions);
	}

	//@Nullable
	private Expression simplifiedNoDomain(Expression leftSimplified, Expression rightSimplified) {
		return simplifiedNoDomain(leftSimplified, arithmeticOperator, rightSimplified, true);
	}

	private Expression nullToNew(Expression simplified, Expression leftSimplified, Expression rightSimplified) {
		return simplified == null ? new ExpressionOperation(leftSimplified, arithmeticOperator, rightSimplified) : simplified;
	}

	/**
	 * TODO trig functions
	 * TODO a searcher agent
	 * to pass in the parameters manually if they were guaranteed to already have been simplified
	 */
	private Expression simplifiedNoDomain(Expression leftSimplified, ArithmeticOperator arithmeticOperator, Expression rightSimplified, boolean commutativeSearch) {
		if (leftSimplified instanceof Constant && rightSimplified instanceof Constant rightConst
			&& !(arithmeticOperator == DIV && rightConst.equals(Constant.ZERO)))
			return Constant.of(arithmeticOperator.operate(leftSimplified, rightSimplified)); // causes negative token to be evaluated as highest precedence
		switch (arithmeticOperator) {
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
				else if (rightSimplified instanceof FunctionExpression rightFunction) {
					if (rightFunction.getFunction() == LOG) {
						if (leftSimplified.equals(rightFunction.getExpressions()[0]))
							return rightFunction.getExpressions()[1];
					} else if (rightFunction.getFunction() == LN)
						if (Constant.E.equals(rightFunction.getExpressions()[0]))
							return rightFunction.getExpressions()[1];
				}
				break;
			case MUL: // a^b * a = a^(b + 1)
				if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return Constant.ZERO;
					else if (leftConstant.equals(Constant.ONE))
						return rightSimplified;
					else if (leftConstant.equals(Constant.NEG_ONE))
						return rightSimplified.negate();
					else if (rightSimplified instanceof ExpressionOperation rightOperator)
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
					else if (leftSimplified instanceof ExpressionOperation leftOperator)
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
				} else if (leftSimplified instanceof ExpressionOperation leftOperator) {
					if (leftOperator.getOperator() == POW) {
						if (rightSimplified instanceof ExpressionOperation rightOperator) {
							if (rightOperator.getOperator() == POW)
								if (leftOperator.getLeft().equals(rightOperator.getLeft()))
									return leftOperator.getLeft().pow(leftOperator.getRight().plus(rightOperator.getRight()).simplified());
						} else if (leftOperator.getLeft().equals(rightSimplified))
							return leftOperator.getLeft().pow(leftOperator.getRight().plus(Constant.ONE).simplified());
					} else if (leftOperator.getOperator() == DIV) {
						return (leftOperator.getLeft().times(rightSimplified).simplified(leftOperator.getLeft(), rightSimplified)).div(leftOperator.getRight());
					}
				} else if (rightSimplified instanceof ExpressionOperation rightOperator) {
					if (rightOperator.getOperator() == POW) {
						if (rightOperator.getLeft().equals(leftSimplified))
							return rightOperator.getLeft().pow(rightOperator.getRight().plus(Constant.ONE).simplified());
					} else if (rightOperator.getOperator() == DIV)
						return (leftSimplified.times(rightOperator.getLeft()).simplified(leftSimplified, rightOperator.getLeft())).div(rightOperator.getRight());
				}
				if (leftSimplified.equals(rightSimplified))
					return leftSimplified.squared();
				if (rightSimplified instanceof Constant && !(leftSimplified instanceof Constant)) // TODO
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
					else if (rightSimplified instanceof ExpressionOperation rightOperator)
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
					else if (leftSimplified instanceof ExpressionOperation leftOperator)
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
				} else if (leftSimplified instanceof ExpressionOperation leftOperator) {
					if (rightSimplified instanceof ExpressionOperation rightOperator) {
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
				} else if (rightSimplified instanceof ExpressionOperation rightOperator) {
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
					if (( leftFunction.getFunction() == LN ||  leftFunction.getFunction() == LOG &&  leftFunction.getExpressions()[0].equals(Constant.E))
					 && (rightFunction.getFunction() == LN || rightFunction.getFunction() == LOG && rightFunction.getExpressions()[0].equals(Constant.E)))
						return LN.ofS(left.times(right).simplified());
					else if (leftFunction.getFunction() == LOG && rightFunction.getFunction() == LOG && leftFunction.getExpressions()[0].equals(rightFunction.getExpressions()[1]))
						return LOG.ofS(leftFunction.getExpressions()[0], left.times(right).simplified());
				} // TODO extract exponent power
				else if (leftSimplified instanceof ExpressionOperation leftOperator && rightSimplified instanceof ExpressionOperation rightOperator) {
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
			case SUB:
				if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.evaluate() < 0) // may not work with edge values
						return leftSimplified.plus(new Constant(-rightConstant.evaluate()));
					else if (rightConstant.evaluate() == 0.0)
						return leftSimplified;
				}
				if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.equals(Constant.ZERO))
						return new ExpressionOperation(Constant.NEG_ONE, MUL, rightSimplified);
				}
				FactorParts subFactorParts = factor(leftSimplified, rightSimplified);
				if (subFactorParts != null) {
					return subFactorParts.factor.times(subFactorParts.leftPart.minus(subFactorParts.rightPart)).simplified();
				}
				if (leftSimplified instanceof FunctionExpression leftFunction && rightSimplified instanceof FunctionExpression rightFunction) {
					if ((leftFunction.getFunction() == LN || leftFunction.getFunction() == LOG && leftFunction.getExpressions()[0].equals(Constant.E))
						&& (rightFunction.getFunction() == LN || rightFunction.getFunction() == LOG && rightFunction.getExpressions()[0].equals(Constant.E)))
						return LN.ofS(left.div(right).simplified());
					else if (leftFunction.getFunction() == LOG && rightFunction.getFunction() == LOG && leftFunction.getExpressions()[0].equals(rightFunction.getExpressions()[1]))
						return LOG.ofS(leftFunction.getExpressions()[0], left.div(right).simplified());
				}
				if (leftSimplified.equals(rightSimplified))
					return Constant.ZERO;
				break;
			default:
		}
		if (commutativeSearch) {
			final boolean commutativeOp = arithmeticOperator.isCommutative();
			if (commutativeOp || (arithmeticOperator.inverse() != null && arithmeticOperator.inverse().isCommutative())) {
				final Expression simplified = commutativeCrossSearch(leftSimplified,
																	 rightSimplified,
																	 !commutativeOp,
																	 commutativeOp ? arithmeticOperator : arithmeticOperator.inverse());
				if (simplified != null)
					return simplified;
			}
		}
		return null;
	}

	public static record FactorParts(Expression factor, Expression leftPart, Expression rightPart) { }

	private FactorParts factor0(Expression leftSimplified, Expression rightSimplified) {
		if (leftSimplified instanceof ExpressionOperation leftOperator) { // tree search for all mults
			if (leftOperator.getOperator() == MUL) {
				if (rightSimplified instanceof ExpressionOperation rightOperator && rightOperator.getOperator() == MUL) {
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
		} else if (rightSimplified instanceof ExpressionOperation rightOperator && rightOperator.getOperator() == MUL) {
			if (rightOperator.getLeft().equals(leftSimplified))
				return new FactorParts(rightOperator.getLeft(), rightOperator.getRight(), Constant.ONE);
			else if (rightOperator.getRight().equals(leftSimplified))
				return new FactorParts(rightOperator.getRight(), Constant.ONE, rightOperator.getLeft());
		}
		return null;
	}

	// private Expression commutativeDeepSearchAddSub(Expression expression) { // TODO
	// 	Collection<Expression> terms = new ArrayList<>();
	// 	commutativeDeepSearchAddSub(expression, terms);
	//
	// 	// try to factor with all terms?
	// 	return expression;
	// }
	//
	// private void commutativeDeepSearchAddSub(Expression expression, Collection<Expression> terms) {
	// 	if (expression instanceof OperatorExpression operatorExpression) {
	// 		if ((operatorExpression.getOperator() == ADD || operatorExpression.getOperator() == SUB)) {
	// 			commutativeDeepSearchAddSub(operatorExpression.getLeft(), terms);
	// 			commutativeDeepSearchAddSub(operatorExpression.getRight(), terms);
	// 		}
	// 	} else
	// 		terms.add(expression);
	//
	// }

	// TODO translate into object with inverted version holding

	public static final record Term(Expression expression, boolean inverted) {

		public Expression expressionInvertCorrected(ArithmeticOperator op) {
			if (!inverted)
				return expression;
			return op.inverse().invertSimplifier(expression);
		}

	}

	private Expression commutativeCrossSearch(Expression leftStatement, Expression rightStatement, boolean inverted, ArithmeticOperator op) {
		List<Term> leftTerms = commutativeSearch(leftStatement, false, op);
		List<Term> rightTerms = commutativeSearch(rightStatement, inverted, op);
		List<Term> newTerms = new ArrayList<>();

		if (leftTerms.size() + rightTerms.size() < 3)
			return null;
		for (int i = 0; i < leftTerms.size(); i++) {
			for (int j = 0; j < rightTerms.size() && i < leftTerms.size(); j++) {
				Expression newTerm = simplifiedNoDomain(leftTerms.get(i).expressionInvertCorrected(op),
														rightTerms.get(j).inverted() ? op.inverse() : op,
														rightTerms.get(j).expression(),
														false);
				if (newTerm != null) { // check to see if any of the current terms can combine and accumulate further:
					leftTerms.remove(i);
					rightTerms.remove(j--);
					newTerm = accumulateTerms(newTerms, newTerm, op, false);
					newTerm = accumulateTerms(leftTerms, newTerm, op, true);
					newTerm = accumulateTerms(rightTerms, newTerm, op, true);
					newTerms.add(new Term(newTerm, false));
				}
			}
		}
		if (newTerms.size() == 0)
			return null;
		else {
			newTerms.addAll(leftTerms);
			newTerms.addAll(rightTerms);
			return combine(newTerms, arithmeticOperator);
		}
	}

	public static Expression combine(Collection<ExpressionOperation.Term> terms, ArithmeticOperator arithmeticOperator) {
		final Iterator<Term> iterator = terms.iterator();
		if (terms.size() == 1)
			return iterator.next().expression();
		ExpressionOperation
				combinedExpressions = new ExpressionOperation(iterator.next().expression(), arithmeticOperator, iterator.next().expression());
		while (iterator.hasNext())
			combinedExpressions = new ExpressionOperation(combinedExpressions, arithmeticOperator, iterator.next().expression());
		return combinedExpressions;
	}

	private Expression accumulateTerms(final List<Term> terms, Expression newTerm, final ArithmeticOperator op, boolean newTermLeftSide) {
		for (int i = 0; i < terms.size(); i++) {
			Expression accNewTerm = simplifiedNoDomain(newTermLeftSide ? newTerm : terms.get(i).expression(),
													   newTermLeftSide && terms.get(i).inverted() ? op.inverse() : op,
													   newTermLeftSide ? terms.get(i).expression() : newTerm,
													   false);
			if (accNewTerm != null) {
				terms.remove(i--);
				newTerm = accNewTerm;
			}
		}
		return newTerm;
	}

	public static List<Term> commutativeSearch(final Expression statement, final boolean inverted, final ArithmeticOperator op) {
		List<Term> terms = new ArrayList<>();
		commutativeSearch(statement, terms, inverted, op);
		return terms;
	}

	public static void commutativeSearch(Expression statement, Collection<Term> terms, boolean inverted, final ArithmeticOperator op) {
		if (statement instanceof ExpressionOperation expressionOperation) {
			if (expressionOperation.getOperator() == op || expressionOperation.getOperator() == op.inverse()) {
				commutativeSearch(expressionOperation.getLeft(), terms, inverted, op);
				commutativeSearch(expressionOperation.getRight(), terms, (expressionOperation.getOperator() == SUB || expressionOperation.getOperator() == DIV) ^ inverted, op);
				return;
			}
		}
		// if (inverted) {
		// 	if (operator == SUB || operator.inverse() == SUB) {
		// 		terms.add(statement instanceof Constant constant
		// 						  ? Constant.of(-constant.evaluate()) : statement.negate());
		// 		return;
		// 	} else if (operator == DIV || operator.inverse() == DIV) {
		// 		terms.add(statement instanceof Constant constant
		// 					  ? Constant.of(1 / constant.evaluate()) : statement.reciprocal());
		// 		return;
		// 	} // else ???
		// }
		// terms.add(statement);
		terms.add(new Term(statement , inverted));
	}

	public static FactorParts factor(Expression leftExp, Expression rightExp) {
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
			if (leftFactors.size() + rightFactors.size() < 2)
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
			if (exp instanceof ExpressionOperation operatorExp && (operatorExp.getOperator() == MUL || operatorExp.getOperator() == DIV)) {
				seekFactors(operatorExp.getLeft(), inverse, pool);
				seekFactors(operatorExp.getRight(), inverse ^ (operatorExp.getOperator() == DIV), pool);
			} else if (!exp.equals(Constant.ZERO)) {
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
				if (expression instanceof ExpressionOperation operatorExp && operatorExp.getOperator() == POW) {
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
