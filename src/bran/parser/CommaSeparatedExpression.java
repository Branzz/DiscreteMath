package bran.parser;

import bran.tree.compositions.expressions.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class CommaSeparatedExpression {

	private final List<Expression> expressions;

	CommaSeparatedExpression() {
		this.expressions = new ArrayList<>();
	}

	CommaSeparatedExpression(List<Expression> expressions) {
		this.expressions = expressions;
	}

	CommaSeparatedExpression(Expression singleton) {
		this();
		expressions.add(singleton);
	}

	public CommaSeparatedExpression(Expression current, CommaSeparatedExpression proceeding) {
		this();
		expressions.add(current);
		expressions.addAll(proceeding.expressions);
	}

	public boolean isSingleton() {
		return expressions.size() == 1;
	}

	public Expression getAsSingleton() {
		return expressions.get(0);
	}

	public List<Expression> getFull() {
		return expressions;
	}

	public List<Expression> expressions() {
		return expressions;
	}

	@Override
	public String toString() {
		return expressions.stream().map(Expression::toString).collect(Collectors.joining(","));
	}

}
