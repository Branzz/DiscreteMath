package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.logic.tree.MultiLeaf;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionExpression extends Expression implements MultiLeaf<Expression, Function> {

	final Function function;
	final Expression[] expressions;

	public FunctionExpression(final Function function, final Expression... expressions) throws IllegalArgumentAmountException {
		super(Stream.concat(Stream.of(function.domain(expressions)), Arrays.stream(expressions).map(Expression::getDomainConditions)).toArray(Statement[]::new));
		function.checkArguments(expressions.length);
		this.function = function;
		this.expressions = expressions;
	}

	private FunctionExpression(final Function function, boolean secure, final Expression... expressions) {
		super(Stream.concat(Stream.of(function.domain(expressions)), Arrays.stream(expressions).map(Expression::getDomainConditions)).toArray(Statement[]::new));
		this.function = function;
		this.expressions = expressions;
	}

	@Override
	public Expression[] getChildren() {
		return expressions;
	}

	@Override
	public Function getFunction() {
		return function;
	}

	@Override
	public List<Variable> getVariables() {
		return Arrays.stream(expressions).flatMap(e -> e.getVariables().stream()).collect(Collectors.toList());
	}

	@Override
	public Expression simplified() {
		// if (function instanceof MultivariableFunction mF)
		// 	switch (mF) {
		// 		case :
		// 	}
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

	// @Override
	// public Expression clone() {
	// 	return new FunctionExpression(function, true, Arrays.stream(expressions).map(Expression::clone).toArray(Expression[]::new));
	// }

	@Override
	public boolean equals(final Object other) {
		return this == other || other instanceof FunctionExpression func && function.equals(func.getFunction()) && Arrays.equals(expressions, func.getChildren());
	}

	@Override
	public String toString() {
		return "(" + function + " " + Arrays.stream(expressions).map(Expression::toString).collect(Collectors.joining(", ")) + ')';
	}

}
