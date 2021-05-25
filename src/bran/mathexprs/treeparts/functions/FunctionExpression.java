package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.MultiLeaf;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bran.mathexprs.treeparts.functions.MultivariableFunction.LN;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.LOG;
import static bran.mathexprs.treeparts.operators.Operator.ADD;
import static bran.mathexprs.treeparts.operators.Operator.MUL;

public class FunctionExpression extends Expression implements MultiLeaf<Expression, Function> {

	final Function function;
	final Expression[] expressions;

	public FunctionExpression(final Function function, final Expression... expressions) throws IllegalArgumentAmountException {
		super(Stream.concat(Stream.of(function.domain(expressions)), Arrays.stream(expressions).map(Expression::getDomainConditions)).toArray(Statement[]::new));
		function.checkArguments(expressions.length);
		this.function = function;
		this.expressions = expressions;
	}

	FunctionExpression(final Function function, boolean secure, final Expression... expressions) {
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
		if (function instanceof MultivariableFunction mF && mF == LOG && simplifiedExpressions[0].equals(Constant.E))
			return new FunctionExpression(LN, true, simplifiedExpressions[1]);
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
	public void replaceAll(final Expression approaches, final Expression approached) {
		for (int i = 0; i < expressions.length; i++)
			if (expressions[i].equals(approaches))
				expressions[i] = approached;
			else
				expressions[i].replaceAll(approaches, approached);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		godelNumbers.push(GodelNumberSymbols.SYNTAX_ERROR);
		godelNumbers.push(GodelNumberSymbols.LEFT);
		if (expressions.length == 1)
			expressions[0].appendGodelNumbers(godelNumbers, variables);
		else
			godelNumbers.push(GodelNumberSymbols.SYNTAX_ERROR);
		godelNumbers.push(GodelNumberSymbols.RIGHT);
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
		return function + "(" + Arrays.stream(expressions).map(Expression::toString).collect(Collectors.joining(", ")) + ')';
	}

}
