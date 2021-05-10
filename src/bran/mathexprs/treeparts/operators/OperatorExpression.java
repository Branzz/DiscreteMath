package bran.mathexprs.treeparts.operators;

import bran.logic.tree.Fork;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.FunctionExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static bran.mathexprs.treeparts.operators.Operator.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.*;

public class OperatorExpression extends Expression implements Fork<Expression, Operator, Expression> {

	private final Expression left;
	private final Operator operator;
	private final Expression right;

	public OperatorExpression(final Expression left, final Operator operator, final Expression right) {
		super(left.getDomainConditions(), right.getDomainConditions(), operator.domain(left, right));
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
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<>();
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
		return this == o || (o instanceof OperatorExpression opExp && operator == opExp.getOperator() && left.equals(opExp.getLeft()) && right.equals(opExp.getRight()));
	}

	@Override
	public OperatorExpression reciprocal() {
		if (operator == DIV)
			return right.div(left);
		else
			return super.reciprocal();
	}

	@Override
	public String toString() {
		// if ((left.equals(Constant.ZERO) && operator == SUB) || (left.equals(Constant.NEG_ONE) && operator == MUL))
		// 	return "-" + right;
		return "(" + left + " " + operator + " " + right + ')';
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
		return simplifiedNoDomain().limitDomain(domainConditions);
	}

	private Expression simplifiedNoDomain() {
		Expression leftSimplified = left.simplified();
		Expression rightSimplified = right.simplified();
		switch (operator) {
			case POW:
				if (rightSimplified instanceof Constant rightConstant) {
					if (rightConstant.equals(Constant.ZERO))
						return Constant.ONE;
					else if (rightConstant.equals(Constant.ONE))
						return leftSimplified;
					else if (leftSimplified instanceof Constant leftConstant)
						return Constant.of(Math.pow(leftConstant.evaluate(), rightConstant.evaluate()));
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
					else if (rightSimplified instanceof Constant rightConstant) {
						if (rightConstant.equals(Constant.ZERO))
							return Constant.ZERO;
					}
					if (leftConstant.equals(Constant.ONE))
						return rightSimplified;
					else if (rightSimplified instanceof Constant rightConstant)
						return Constant.of(leftConstant.evaluate() * rightConstant.evaluate());
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
					}
				} else if (rightSimplified instanceof OperatorExpression rightOperator)
					if (rightOperator.getOperator() == POW)
						if (rightOperator.getLeft().equals(leftSimplified))
							return rightOperator.getLeft().pow(rightOperator.getRight().plus(Constant.ONE).simplified());
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
					else if (rightSimplified instanceof Constant rightConstant)
						return Constant.of(leftConstant.evaluate() / rightConstant.evaluate());
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
					if (leftOperator.getOperator() == POW) {
						if (rightSimplified instanceof OperatorExpression rightOperator) {
							if (rightOperator.getOperator() == POW)
								if (leftOperator.getLeft().equals(rightOperator.getLeft()))
									return leftOperator.getLeft().pow(leftOperator.getRight().minus(rightOperator.getRight()).simplified());
						} else if (leftOperator.getLeft().equals(rightSimplified))
							return leftOperator.getLeft().pow(leftOperator.getRight().minus(Constant.ONE).simplified());
					}
				} else if (rightSimplified instanceof OperatorExpression rightOperator) {
					if (rightOperator.getOperator() == POW)
						if (rightOperator.getLeft().equals(leftSimplified))
							return rightOperator.getLeft().pow(rightOperator.getRight().minus(Constant.ONE).simplified());
				}
				break;
			case MOD:
				if (leftSimplified instanceof Constant leftConstant)
					if (rightSimplified instanceof Constant rightConstant && !rightConstant.equals(Constant.ZERO))
						return Constant.of(leftConstant.evaluate() % rightConstant.evaluate());
					else if (leftConstant.equals(Constant.ZERO))
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
				}
				else if (rightSimplified instanceof Constant rightConstant) {
					if (leftSimplified instanceof Constant leftConstant)
						return Constant.of(leftConstant.evaluate() + rightConstant.evaluate());
					if (rightConstant.evaluate() < 0) // may not work with edge values
						return leftSimplified.minus(new Constant(-rightConstant.evaluate()));
					else if (rightConstant.evaluate() == 0.0)
						return leftSimplified;
				} else if (leftSimplified instanceof Constant leftConstant) {
					if (leftConstant.evaluate() == 0.0)
						return rightSimplified;
				}
				break;
			case SUB:
				if (leftSimplified.equals(rightSimplified))
					return Constant.ZERO;
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

				break;
			default:
		}
		return new OperatorExpression(leftSimplified, operator, rightSimplified);
	}

	private static record FactorParts(Expression factor, Expression leftPart, Expression rightPart) { }

	private FactorParts factor(Expression leftSimplified, Expression rightSimplified) {
		if (leftSimplified instanceof OperatorExpression leftOperator) {
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

}
