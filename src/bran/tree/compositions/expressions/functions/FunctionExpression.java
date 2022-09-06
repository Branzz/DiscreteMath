package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.AbstractFunctionExpression;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Value;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.statements.Statement;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bran.tree.compositions.expressions.functions.MultiArgFunction.*;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.POW;

public class FunctionExpression extends AbstractFunctionExpression<ExpFunction, Expression> {

	final ExpFunction function;
	final Expression[] expressions;

	public FunctionExpression(ExpFunction function, Expression... expressions) throws IllegalArgumentAmountException {
		super(Stream.concat(Stream.of(function.domain(expressions)), Arrays.stream(expressions).map(Expression::getDomainConditions)).toArray(Statement[]::new));
		expressions = Arrays.stream(expressions).filter(e -> !e.equals(Composition.empty())).toArray(Expression[]::new);
		function.checkArguments(expressions.length);
		this.function = function;
		this.expressions = expressions;
	}

	public FunctionExpression(ExpFunction function, boolean secure, Expression... expressions) {
		super(Stream.concat(Stream.of(function.domainS(expressions)), Arrays.stream(expressions).map(Expression::getDomainConditions)).toArray(Statement[]::new));
		this.function = function;
		this.expressions = expressions;
	}

	@Override
	public List<? extends Expression> getChildren() {
		return List.of(expressions);
	}

	public Expression[] getExpressions() {
		return expressions;
	}

	@Override
	public ExpFunction getFunction() {
		return function;
	}

	@Override
	public Set<Variable> getVariables() {
		return Arrays.stream(expressions).flatMap(e -> e.getVariables().stream()).collect(Collectors.toSet());
	}

	@Override
	public Expression simplified() {
		boolean allConstants = true;
		Expression[] simplifiedExpressions = new Expression[expressions.length];
		for (int i = 0; i < expressions.length; i++) {
			if (!((simplifiedExpressions[i] = expressions[i].simplified()) instanceof Constant))
				allConstants = false;
		}
		if (allConstants) {
			double[] constants = new double[simplifiedExpressions.length];
			for (int i = 0; i < expressions.length; i++)
				constants[i] = ((Constant) simplifiedExpressions[i]).evaluate();
			return Constant.of(function.function(constants));
		}
		if (function instanceof MultiArgFunction mF) {
			if (mF == LOG) {
				if (simplifiedExpressions[0].equals(Constant.E))
					return new FunctionExpression(LN, true, simplifiedExpressions[1]);
				if (simplifiedExpressions[1] instanceof ExpressionOperation rightOp && rightOp.getOperator() == POW
							&& simplifiedExpressions[0].equals(rightOp.getLeft()))
					return rightOp.getRight();
			} else if (mF == TETRA) {
				Expression tower = tower(simplifiedExpressions[0], simplifiedExpressions[1]);
				if (tower != null) {
					Expression towerSimplified = tower.simplified();
					if (!tower.equals(towerSimplified))
						return towerSimplified;
				}
			}
		}
		return new FunctionExpression(function, true, simplifiedExpressions);
	}

	@Override
	public double evaluate() {
		double[] evaluation = new double[expressions.length];
		for (int i = 0; i < expressions.length; i++)
			evaluation[i] = expressions[i].evaluate();
		return this.function.function(evaluation);
	}

	@Override
	public Expression derive() {
		return function.derive(expressions);
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		for (Expression e : expressions)
			if (e.respect(respectsTo))
				return true;
		return false;
		// return Arrays.stream(expressions).map(e -> e.respect(respectsTo)).reduce(false, Boolean::logicalOr);
	}

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		for (int i = 0; i < expressions.length; i++)
			if (expressions[i].equals(original))
				expressions[i] = (Expression) replacement;
			else
				expressions[i].replaceAll(original, replacement);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
		boolean childIsVar = this.getExpressions()[0] instanceof Value;
		if (!childIsVar)
			godelBuilder.push(GodelNumberSymbols.LEFT);
		if (expressions.length == 1)
			expressions[0].appendGodelNumbers(godelBuilder);
		else
			godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
		if (!childIsVar)
			godelBuilder.push(GodelNumberSymbols.RIGHT);
	}

	// @Override
	// public Expression clone() {
	// 	return new FunctionExpression(function, true, Arrays.stream(expressions).map(Expression::clone).toArray(Expression[]::new));
	// }

	/**
	 * functions with multiple parameters have multiple possible inverses
	 * @param arg the argument index to be respected
	 */
	public Expression inverse(int arg) {
		if (function == LOG) {
				return arg == 0 ? POW.of(expressions[1], expressions[0].reciprocal())
						: arg == 1 ? POW.of(expressions[0], expressions[1])
						: null;
		} else {
			return new FunctionExpression(function.inverse(), true, expressions);
		}
	}

	@Override
	public boolean equals(final Object other) {
		return this == other || other instanceof FunctionExpression func && function.equals(func.getFunction())
				&& Arrays.equals(expressions, func.getExpressions());
	}

	@Override
	public String toFullString() {
		return function + "(" + Arrays.stream(expressions).map(Expression::toFullString).collect(Collectors.joining(", ")) + ')';
	}

	@Override
	public String toString() {
		String argString = Arrays.stream(expressions)
								 .map(Expression::toString)
								 .collect(Collectors.joining(", "));
		String args = (expressions.length == 1 && expressions[0] instanceof Constant ? " " + argString : Composition.parens(argString));
		return function == MultiArgFunction.ABS ? "|" + args + "|" : function + args;
	}

}
