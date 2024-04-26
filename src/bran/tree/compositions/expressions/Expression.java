package bran.tree.compositions.expressions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.exceptions.IllegalInverseExpressionException;
import bran.tree.Equivalable;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Value;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.values.numbers.StandardOperand;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.sets.regular.SpecialSetType;
import bran.tree.compositions.statements.StatementOperation;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.inequality.Inequality;
import bran.tree.compositions.statements.special.quantifier.UniversalNumbersStatement;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.tree.compositions.expressions.functions.MultiArgFunction.LOG;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.SQRT;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.*;
import static bran.tree.compositions.expressions.values.Constant.*;
import static bran.tree.compositions.statements.operators.LogicalOperator.AND;
import static bran.tree.compositions.statements.special.equivalences.inequality.InequalityType.*;

public abstract class Expression implements Composition, Equivalable<Expression>, Comparable<Expression>, StandardOperand<Expression, Expression> {

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
		StatementOperation combinedStatements = new StatementOperation(statements.get(0), AND, statements.get(1));
		for (int i = 2; i < statements.size(); i++)
			combinedStatements = new StatementOperation(combinedStatements, AND, statements.get(i));
		return combinedStatements;
	}

	public static Statement combineDefaultDomains(Expression... expressionArray) {
		return combineDomains(Arrays.stream(expressionArray).map(Expression::defaultConditions).toArray(Statement[]::new));
	}

	public abstract Set<Variable> getVariables();

	public static Set<Variable> combineVariableSets(Expression... expressions) {
		return Arrays.stream(expressions).flatMap(e -> e.getVariables().stream()).collect(Collectors.toSet());
	}

	public static Set<Variable> combineVariableSets(Set<Variable>... variables) {
		return Arrays.stream(variables).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	public abstract double evaluate();

	// public abstract boolean hasImaginary();
	//
	// public Expression evaluateNonReal() {
	// 	return Constant.of(evaluate());
	// }

	/*
	 * derivatives aren't discrete math, but the tree structure was the perfect opportunity to set it up.
 	 */
	public abstract Expression derive();

	public <R, T> R traverse(T t, Function<T, R> function) {
		return null;
	}

	public boolean respect(final Collection<Variable> respectsTo) { return false; }

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) { /* add nothing*/ }

	private static final Expression emptyExpression = new Expression() {
		@Override public Set<Variable> getVariables()					{ return Collections.emptySet(); }
		@Override public Expression simplified()						{ return empty(); }
		@Override public double evaluate()								{ return 0.0; }
		// @Override public boolean hasImaginary() { return false; }
		@Override public Expression derive()							{ return empty(); }
		@Override public <R, T> R traverse(final T t, final Function<T, R> function) { return null; } // TODO
		@Override public boolean respect(final Collection<Variable> respectsTo) { return false; }
		@Override public void replaceAll(final Composition original, final Composition replacement) { }
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
	public int compareTo(final Expression expression) {
		if (this == expression)
			return 0;
		if (this.equals(expression))
			return 0;
		if (expression.equals(INFINITY))
			return -1;
		else if (expression.equals(NEG_INFINITY))
			return 1;
		final ExpressionOperation subtraction = this.minus(expression);
		Expression leftComp = subtraction.simplified();
		// a compare b = a - b compare 0
		// the domain of f(f'(x)) is the domain of f'(x)
		// performing operations between domains won't work
		//		(-1, 1) && (0, inf) = [0, 1), but won't tell if intersects in this area
		//      storing the original domains of each function is therefore useless in complex expressions
		// the range of f(x) is the domain of f'(x)
		// TODO; simplify first? you can prove if a function is less somehow; you can with given variables for sure
		// return Double.compare(evaluate(), expression.evaluate());
		try {

		} catch (IllegalInverseExpressionException e) {

		}
		final Expression inverse = subtraction.inverse().iterator().next();
		final Set<Variable> variables = subtraction.getVariables();
		// if (variables.size() != 1)
		// 	return 0;
		Variable variable = variables.iterator().next();
		final Statement range = inverse.domainConditions; // TODO getAll method needed to replace everything properly (?)
		// new RangedSet(new SpecialSet(SpecialSetType.R), range);
		// if (!expression.equals(ZERO)) {
		// 	if (range.and(variable.lessEqual(Constant.ZERO)).simplified().equals(VariableStatement.TAUTOLOGY))
		// 		 return 1;
		// 	if (range.and(variable.greaterEqual(Constant.ZERO)).simplified().equals(VariableStatement.TAUTOLOGY))
		// 		return -1;
		// }
		return 0;
	}

	public List<Expression> inverse() {
		return inverse(null);
	}

	public List<Expression> inverse(Expression replacement) {
		final Expression reserved = new Variable("reserved");
		List<Expression> otherSides = new ArrayList<>();
		otherSides.add(reserved);
		Expression thisSide = this;
		boolean simplifiedStep = false;
		while (!(thisSide instanceof Variable)) {
			if (thisSide instanceof ExpressionOperation opExp) {
				boolean varsOnLeft = !opExp.getLeft().getVariables().isEmpty();
				boolean varsOnRight = !opExp.getRight().getVariables().isEmpty();
				if (varsOnLeft && varsOnRight) {
					if (simplifiedStep)
						throw new IllegalInverseExpressionException("can't create the inverse of " + opExp);
					thisSide = opExp.simplified();
					simplifiedStep = true; // can only try to simplify once before it's infinite loop
					continue;
				} else {
					if (!varsOnLeft && !varsOnRight)
						throw new IllegalStateException("simplification of constants was weak; coder's fault");
					if (opExp.getOperator() == POW) {
						final int originalSize = otherSides.size();
						if (opExp.getRight().equals(Constant.TWO))
							for (int i = 0; i < originalSize; i++) {
								otherSides.set(i, otherSides.get(i).sqrt());
							}
							for (int i = 0; i < originalSize; i++) {
								otherSides.add(new ExpressionOperation(NEG_ONE, MUL, otherSides.get(i)));
							}
					} else
						map(otherSides, otherSide -> opExp.getOperator().inverse(varsOnLeft ? 0 : 1, otherSide, varsOnLeft ? opExp.getRight() : opExp.getLeft()));
					thisSide = varsOnLeft ? opExp.getLeft() : opExp.getRight();
				}
				simplifiedStep = false;
			} else if (thisSide instanceof FunctionExpression fExp) {
				if (fExp.getFunction().getArgAmount() == 1) {
					thisSide = fExp.getExpressions()[0];
					map(otherSides, otherSide -> fExp.getFunction().inverse(0, otherSide));
				} else if (fExp.getFunction().getArgAmount() == 2) {
					boolean varsOnLeft = !fExp.getExpressions()[0].getVariables().isEmpty();
					boolean varsOnRight = !fExp.getExpressions()[1].getVariables().isEmpty();
					if (varsOnLeft && varsOnRight) {
						if (simplifiedStep)
							throw new IllegalInverseExpressionException("can't create the inverse of " + fExp.getFunction());
						thisSide = fExp.simplified();
						simplifiedStep = true; // can only try to simplify once before it's infinite loop
						continue;
					} else if (!varsOnLeft && !varsOnRight) {
						throw new IllegalStateException("simplification of constants was weak; coder's fault");
					}
					thisSide = fExp.getExpressions()[0];
					map(otherSides, otherSide -> fExp.getFunction().inverse(varsOnLeft ? 0 : 1, otherSide));
				}
			} else if (thisSide instanceof Value) {
				return List.of(this.simplified());
			} else {
				throw new IllegalInverseExpressionException("unimplemented expression type");
			}
		}
		final Expression finalThisSide = thisSide;
		otherSides.forEach(otherSide -> otherSide.domainConditions.replaceAll(reserved, finalThisSide));
		otherSides.forEach(otherSide -> otherSide.replaceAll(reserved, replacement == null ? finalThisSide : replacement));
		return otherSides;
	}

	private static void map(List<Expression> list, Function<Expression, Expression> mapper) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, mapper.apply(list.get(i)));
		}
	}

	// public Expression findARoot() {
	// 	final Statement simplified = derive().equates(ZERO)
	// 										 .simplified();
	// 	if (simplified instanceof OperationStatement o)
	// 		if (o.getLeft() instanceof VariableStatement)
	// 			return o.getRight();
	// 		else
	// 			return o.getLeft();
	// }

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
							 this, domainConditions.toString(), toFullString(),
							 simplified(), derivative.toString(), derivative.simplified().toString());
	}

	public double depthProb() {
		char[] parens = toFullString().toCharArray();
		int deepnessTotal = 0;
		int deepnessPositivity = 0;
		boolean leftPrevious = true;
		for (final char paren : parens) {
			if (paren == '(') {
				deepnessTotal++;
				if (!leftPrevious)
					deepnessPositivity += 2;
				leftPrevious = true;
			} else if (paren == ')') {
				// if (leftPrevious)
				// 	deepnessTotal--;
				leftPrevious = false;
			}
		}
		return deepnessTotal == 0 ? 0 : (double) deepnessPositivity / deepnessTotal;
	}

	public ExpressionOperation pow(Expression exp) {
		return new ExpressionOperation(this, POW, exp);
	}

	public FunctionExpression log(Expression exp) throws IllegalArgumentAmountException {
		return new FunctionExpression(LOG, this, exp);
	}

	public ExpressionOperation squared() {
		return pow(TWO);
	}

	public ExpressionOperation cubed() {
		return pow(Constant.of(3));
	}

	@Override
	public Expression add(Expression expression) {
		return this.plus(expression);
	}

	@Override
	public Expression subtract(Expression expression) {
		return this.minus(expression);
	}

	@Override
	public Expression multiply(Expression expression) {
		return this.times(expression);
	}

	@Override
	public Expression divide(Expression expression) {
		return this.div(expression);
	}

	public ExpressionOperation plus(Expression addend) {
		return new ExpressionOperation(this, ADD, addend);
	}

	public ExpressionOperation minus(Expression subtrahend) {
		return new ExpressionOperation(this, SUB, subtrahend);
	}

	public ExpressionOperation times(Expression multiplicand) {
		return new ExpressionOperation(this, MUL, multiplicand);
	}

	public ExpressionOperation div(Expression divisor) {
		return new ExpressionOperation(this, DIV, divisor);
	}

	public ExpressionOperation mod(Expression divisor) {
		return new ExpressionOperation(this, MOD, divisor);
	}

	public ExpressionOperation reciprocal() {
		return ONE.div(this);
	}

	public ExpressionOperation negate() {
		return ZERO.minus(this);
	}

	public ExpressionOperation inc() {
		return this.plus(ONE);
	}

	public ExpressionOperation dec() {
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
		public LimitExpression of(Expression function) {
			return new LimitExpression(approaches, approached, function);
		}
	}

	public static record LogBasePart(Expression base) {
		public FunctionExpression of(Expression expression) throws IllegalArgumentAmountException {
			return LOG.of(base, expression);
		}
		public FunctionExpression ofS(Expression expression) {
			return LOG.ofS(base, expression);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
