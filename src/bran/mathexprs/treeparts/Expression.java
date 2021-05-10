package bran.mathexprs.treeparts;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.UniversalNumbersStatement;
import bran.logic.tree.Equivalable;
import bran.logic.tree.TreePart;
import bran.mathexprs.Equation;
import bran.mathexprs.EquationType;
import bran.mathexprs.Inequality;
import bran.mathexprs.treeparts.functions.FunctionExpression;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.sets.SpecialSet;
import bran.sets.SpecialSetType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static bran.logic.statements.operators.Operator.AND;
import static bran.mathexprs.InequalityType.*;
import static bran.mathexprs.treeparts.operators.Operator.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.*;

public abstract class Expression implements TreePart, Comparable<Expression>, Equivalable<Expression> {

	protected Statement domainConditions;

	public Expression(Statement... domainConditions) {
		this.domainConditions = combineDomains(domainConditions);
	}

	public Expression(Statement domainConditions) {
		this.domainConditions = domainConditions;
	}

	public Expression() {
		this.domainConditions = VariableStatement.TAUTOLOGY;
	}

	public Expression limitDomain(Statement domain) {
		this.domainConditions = this.domainConditions.and(domain);
		return this;
	}

	public Statement getDomainConditions() {
		return domainConditions;
	}

	public UniversalNumbersStatement getUniversalStatement() {
		return new UniversalNumbersStatement(new SpecialSet(SpecialSetType.R), domainConditions, getVariables().toArray(Variable[]::new));
	}

	protected static Statement combineDomains(Statement... statementsArray) {
		List<Statement> statements = Arrays.stream(statementsArray)
										   .filter(s -> !s.equals(VariableStatement.TAUTOLOGY))
										   .collect(Collectors.toList());
		if (statements.size() == 0)
			return VariableStatement.TAUTOLOGY;
		if (statements.size() == 1)
			return statements.get(0);
		OperationStatement combinedStatements = new OperationStatement(statements.get(0), AND, statements.get(1));
		for (int i = 2; i < statements.size(); i++)
			combinedStatements = new OperationStatement(combinedStatements, AND, statements.get(i));
		return combinedStatements;
	}

	public abstract List<Variable> getVariables();

	public abstract Expression simplified();

	public abstract double evaluate();

	/**
	 * A derivative isn't discrete math, but the tree structure was the perfect opportunity to set it up.
 	 */
	public abstract Expression derive();

	public abstract boolean respect(final Collection<Variable> respectsTo);

	private static final Expression emptyExpression = new Expression() {
		@Override public List<Variable> getVariables() { return Collections.emptyList(); }
		@Override public Expression simplified() { return empty(); }
		@Override public double evaluate() { return 0.0; }
		@Override public Expression derive() { return empty(); }
		@Override public boolean respect(final Collection<Variable> respectsTo) { return false; }
		// @Override public boolean equivalentTo(final Expression other) { return other == empty(); }
		@Override public String toString() { return ""; }
		// @Override public Expression clone() { return empty(); }
		@Override public boolean equals(Object o) { return o == empty(); }
	};

	public static Expression empty() {
		return emptyExpression;
	}

	// public abstract Expression clone();


	@Override
	public boolean equivalentTo(final Expression other) {
		return evaluate() == other.evaluate();
	}

	@Override
	public int compareTo(final Expression o) {
		// TODO; simplify first? you can prove if a function is less somehow.
		return Double.compare(evaluate(), o.evaluate());
	}

	public abstract boolean equals(Object other);

	public OperatorExpression pow(Expression exp) {
		return new OperatorExpression(this, POW, exp);
	}

	public OperatorExpression squared() {
		return pow(Constant.of(2));
	}

	public OperatorExpression cubed() {
		return pow(Constant.of(3));
	}

	public OperatorExpression times(Expression multiplicand) {
		return new OperatorExpression(this, MUL, multiplicand);
	}

	public OperatorExpression div(Expression divisor) {
		return new OperatorExpression(this, DIV, divisor);
	}

	public OperatorExpression mod(Expression divisor) {
		return new OperatorExpression(this, MOD, divisor);
	}

	public OperatorExpression plus(Expression addend) {
		return new OperatorExpression(this, ADD, addend);
	}

	public OperatorExpression minus(Expression subtrahend) {
		return new OperatorExpression(this, SUB, subtrahend);
	}

	public OperatorExpression reciprocal() {
		return Constant.ONE.div(this);
	}

	public OperatorExpression negate() {
		return Constant.of(0).minus(this);
	}

	public OperatorExpression inc() {
		return this.plus(Constant.ONE);
	}

	public OperatorExpression dec() {
		return this.minus(Constant.ONE);
	}

	public OperatorExpression chain(Expression next) {
		return this.times(next.derive());
	}

	public FunctionExpression sqrt() {
		try {
			return new FunctionExpression(SQRT, this);
		} catch (IllegalArgumentAmountException ignored) {
		}
		return null;
	}

	public Equation<Expression> equates(Expression right) {
		return new Equation<>(this, right);
	}

	public Equation<Expression> notEquates(Expression right) {
		return new Equation<>(this, EquationType.UNEQUAL, right);
	}

	public Inequality<Expression, Expression> greater(Expression right) {
		return new Inequality<>(this, GREATER, right);
	}

	public Inequality<Expression, Expression> greaterEqual(Expression right) {
		return new Inequality<>(this, GREATER_EQUAL, right);
	}

	public Inequality<Expression, Expression> less(Expression right) {
		return new Inequality<>(this, LESS, right);
	}

	public Inequality<Expression, Expression> lessEqual(Expression right) {
		return new Inequality<>(this, LESS_EQUAL, right);
	}

	public String deriveString() {
		return "the derivative of " + this + " is\n" + this.derive();
	}

}
