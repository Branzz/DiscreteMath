package bran.tree.compositions.expressions.functions.rec;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.AbstractFunctionExpression;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumberSymbols;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static bran.tree.compositions.expressions.functions.rec.RecFunction.DERIVE;

public class RecFunctionExpression extends AbstractFunctionExpression<Expression, RecFunction> {

	RecFunction function;
	Expression[] expressions;

	public RecFunctionExpression(RecFunction function, Expression[] expressions) {
		this.function = function;
		this.expressions = expressions;
	}

	public Expression apply() {
		return function.function(expressions);
	}

	@Override
	public void replaceAll(Composition original, Composition replacement) {
		for (int i = 0; i < expressions.length; i++)
			if (expressions[i].equals(original))
				expressions[i] = (Expression) replacement;
			else
				expressions[i].replaceAll(original, replacement);
	}

	@Override
	public Expression simplified() {
		return copy();
	}

	@Override
	public Set<Variable> getVariables() {
		return Expression.combineVariableSets(expressions);
	}

	@Override
	public double evaluate() {
		return function.function(expressions).evaluate();
	}

	@Override
	public Expression derive() {
		return DERIVE.of(this);
	}

	@Override
	public boolean respect(Collection<Variable> respectsTo) {
		for (Expression e : expressions)
			if (e.respect(respectsTo))
				return true;
		return false;
	}

	@Override
	public void appendGodelNumbers(GodelBuilder godelBuilder) {
		godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
		for (final Expression expression: expressions) {
			expression.appendGodelNumbers(godelBuilder);
		}
	}

	@Override
	public boolean equals(Object other) {
		return this == other ||
			   (other instanceof RecFunctionExpression r
				&& function.equals(r.function)
				&& Arrays.equals(expressions, r.expressions));
	}

	@Override
	public String toFullString() {
		return function.toString() + "(" + Arrays.stream(expressions).map(Expression::toString).collect(Collectors.joining(", ")) + ")";
	}

	@Override
	public Expression[] getChildren() {
		return new Expression[0];
	}

	@Override
	public RecFunction getFunction() {
		return function;
	}

	private RecFunctionExpression copy() {
		return new RecFunctionExpression(function, expressions);
	}

}
