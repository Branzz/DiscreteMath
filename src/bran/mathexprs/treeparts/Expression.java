package bran.mathexprs.treeparts;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.UniversalNumbersStatement;
import bran.mathexprs.Equation;
import bran.mathexprs.EquationType;
import bran.mathexprs.Inequality;
import bran.mathexprs.treeparts.functions.FunctionExpression;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.sets.SpecialSet;
import bran.sets.SpecialSetType;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelBuilder;
import bran.tree.Composition;
import bran.tree.Equivalable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.logic.statements.operators.LogicalOperator.AND;
import static bran.mathexprs.InequalityType.*;
import static bran.mathexprs.treeparts.Constant.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.LOG;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.SQRT;
import static bran.mathexprs.treeparts.operators.Operator.*;

public abstract class Expression extends Composition implements Equivalable<Expression> {

	protected Statement domainConditions;

	public abstract Expression simplified();

	public Expression(Statement... domainConditions) {
		this.domainConditions = combineDomains(domainConditions);
	}

	public Expression(Statement domainConditions) {
		this.domainConditions = domainConditions;
	}

	public Expression() {
		this.domainConditions = VariableStatement.TAUTOLOGY;
	}

	public static Statement defaultConditions(Expression expression) {
		return expression.greater(NEG_INFINITY).and(expression.less(INFINITY));
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

	public static Statement combineDefaultDomains(Expression... expressionArray) {
		return combineDomains(Arrays.stream(expressionArray).map(Expression::defaultConditions).toArray(Statement[]::new));
	}

	public abstract Set<Variable> getVariables();

	public abstract double evaluate();

	// public abstract boolean hasImaginary();
	//
	// public Expression evaluateNonReal() {
	// 	return Constant.of(evaluate());
	// }

	/**
	 * derivatives aren't discrete math, but the tree structure was the perfect opportunity to set it up.
 	 */
	public abstract Expression derive();

	public <R, T> R traverse(T t, Function<T, R> function) {
		return null;
	}

	public abstract boolean respect(final Collection<Variable> respectsTo);

	public abstract void replaceAll(final Expression approaches, final Expression approached);

	@Override
	public abstract void appendGodelNumbers(final GodelBuilder godelBuilder);

	private static final Expression emptyExpression = new Expression() {
		@Override public Set<Variable> getVariables()					{ return Collections.emptySet(); }
		@Override public Expression simplified()						{ return empty(); }
		@Override public double evaluate()								{ return 0.0; }
		// @Override public boolean hasImaginary() { return false; }
		@Override public Expression derive()							{ return empty(); }
		@Override public <R, T> R traverse(final T t, final Function<T, R> function) { return null; } // TODO
		@Override public boolean respect(final Collection<Variable> respectsTo) { return false; }
		@Override public void replaceAll(final Expression approaches, final Expression approached) { }
		@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) {
			godelBuilder.push(GodelNumberSymbols.LEFT);
			godelBuilder.push(GodelNumberSymbols.RIGHT); // TODO Is this how it'd be in Godel?
		}
		@Override public boolean equivalentTo(final Expression other) 	{ return other == empty(); }
		@Override public String toFullString()							{ return "()"; }
		@Override public String toString()								{ return "()"; }
		// @Override public Expression clone()							{ return empty(); }
		@Override public boolean equals(Object o)						{ return this == o; }
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
	public int compareTo(final Composition o) {
		// TODO; simplify first? you can prove if a function is less somehow; you can with given variables for sure
		return o instanceof Expression exp ? Double.compare(evaluate(), exp.evaluate()) : -1;
	}

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract String toFullString();

	@Override
	public String toString() {
		return toFullString();
	}

	public static String innerString(final String toString) {
		return toString.length() > 2 ? toString.substring(1, toString.length() - 1) : toString;
	}

	public String deriveString() {
		return "the derivative of " + this + " is\n" + this.derive();
	}

	public String summaryString() {
		final Expression derivative = derive();
		return String.format("%s\nDomain: %s\nWith Parens: %s\nSimplified: %s\nDerivative: %s\nDerivative Simplified: %s",
							 toString(), domainConditions.toString(), toFullString(),
							 simplified(), derivative.toString(), derivative.simplified().toString());
	}

	public OperatorExpression pow(Expression exp) {
		return new OperatorExpression(this, POW, exp);
	}

	public OperatorExpression squared() {
		return pow(TWO);
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
		return ONE.div(this);
	}

	public OperatorExpression negate() {
		return ZERO.minus(this);
	}

	public OperatorExpression inc() {
		return this.plus(ONE);
	}

	public OperatorExpression dec() {
		return this.minus(ONE);
	}

	public Expression chain(Expression next) {
		return this.times(next.derive());
	}

	public Expression sqrt() {
		try {
			return new FunctionExpression(SQRT, this);
		} catch (IllegalArgumentAmountException ignored) {
		}
		return null;
	}

	public Equation equates(Expression right) {
		return new Equation(this, right);
	}

	public Equation notEquates(Expression right) {
		return new Equation(this, EquationType.UNEQUAL, right);
	}

	public Inequality greater(Expression right) {
		return new Inequality(this, GREATER, right);
	}

	public Inequality greaterEqual(Expression right) {
		return new Inequality(this, GREATER_EQUAL, right);
	}

	public Inequality less(Expression right) {
		return new Inequality(this, LESS, right);
	}

	public Inequality lessEqual(Expression right) {
		return new Inequality(this, LESS_EQUAL, right);
	}

	public LimitApproachesPart approaches(final Expression approached) {
		return new LimitApproachesPart(this, approached);
	}

	// Imperative style assistance classes

	public static record LimitApproachesPart(Expression approaches, Expression approached) {
		public Expression of(Expression function) {
			return new LimitExpression(approaches, approached, function);
		}
	}

	public static record LogBasePart(Expression base) {
		public Expression of(Expression expression) throws IllegalArgumentAmountException {
			return LOG.of(base, expression);
		}
		public Expression ofS(Expression expression) {
			return LOG.ofS(base, expression);
		}
	}

}
