package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalInverseExpressionException;
import bran.tree.compositions.expressions.functions.appliers.DomainSupplier;
import bran.tree.compositions.expressions.functions.appliers.FunctionDerivable;
import bran.tree.compositions.expressions.functions.appliers.Functional;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.statements.VariableStatement;

import java.util.function.Function;

import static bran.tree.compositions.expressions.operators.Operator.POW;
import static bran.tree.compositions.expressions.values.Constant.*;
import static bran.tree.compositions.expressions.functions.appliers.DomainSupplier.*;

import static java.lang.Math.*;

public enum MultiArgFunction implements ExpFunction {
	LOG  (2, e -> ((e[0].greater(ZERO).and(e[0].notEquates(ONE))).or(e[0].equates(INFINITY))).and(e[1].greater(ZERO)),
		  a -> log(a[0]) / log(a[1]), null),
	LN   (e -> e.greater(ZERO), Math::log, a -> a.derive().div(a)),
	SIGN (Math::signum, a -> ZERO), // not roots
	ABS  (Math::abs, a -> SIGN.ofS(a).chain(a)),
	CEIL (Math::ceil, a -> ZERO),
	ROUND(a -> (double) Math.round(a), a -> ZERO), // not roots??
	FLOOR(Math::floor, a -> ZERO),
	SQRT (e -> e.greaterEqual(ZERO), Math::sqrt, a -> a.derive().div(Constant.TWO.times(a.sqrt()))),
	 SIN (Math::sin),
	ASIN (SIN, e -> e.greaterEqual(NEG_ONE).and(e.lessEqual(ONE)), Math::asin, a -> a.derive().div(ONE.minus(a).sqrt())),
	 SINH(Math::sinh),
	ASINH(SINH, a -> Math.log(a + Math.sqrt(a * a + 1)), a -> a.derive().div(a.squared().inc().sqrt())),
	 CSC (e -> SIN.ofS(e).notEquates(ZERO), a -> 1 / sin(a), null),
	ACSC (CSC, e -> e.lessEqual(NEG_ONE).or(e.greaterEqual(ONE)), a -> Math.asin(1 / a), a -> a.derive().div(ABS.ofS(a).times(ONE.minus(a).sqrt()).negate())),
	 CSCH(NOT_ZERO, a -> 1 / Math.sinh(a), null),
	ACSCH(CSCH, NOT_ZERO, a -> Math.log((1 + Math.sqrt(1 + a * a)) / a), a -> a.derive().div(ABS.ofS(a).times(a.squared().inc().sqrt()).negate())),
	 COS (Math::cos, a -> SIN.ofS(a).negate().chain(a)),
	ACOS (COS, e -> e.greaterEqual(NEG_ONE).and(e.lessEqual(ONE)), Math::acos, a -> a.derive().div(ONE.minus(a).sqrt().negate())),
	COSH (Math::cosh, a -> SINH.ofS(a).chain(a)),
	ACOSH(COSH, e -> e.greaterEqual(ONE), a -> Math.log(a + Math.sqrt(a * a - 1)), a -> a.derive().div(a.squared().dec().sqrt())),
	 SEC (e -> COS.ofS(e).notEquates(ZERO), a -> 1 / cos(a), null),
	ASEC (SEC, e -> e.lessEqual(NEG_ONE).or(e.greaterEqual(ONE)), a -> Math.acos(1 / a), a -> a.derive().div(ABS.ofS(a).times(ONE.minus(a).sqrt()))),
	 SECH(a -> 1 / Math.cosh(a)),
	ASECH(SECH, e -> e.greater(ZERO).and(e.lessEqual(ONE)), a -> Math.log((1 + Math.sqrt(1 - a * a)) / a), a -> a.derive().div(ABS.ofS(a).times(a.squared().dec().sqrt()).negate())),
	 TAN (e -> COS.ofS(e).notEquates(ZERO), Math::tan, a -> SEC.ofS(a).squared().chain(a)),
	ATAN (TAN, Math::atan, a -> a.derive().div(ONE.minus(a))),
	 TANH(Math::tanh, a -> SECH.ofS(a).squared().chain(a)),
	ATANH(TANH, e -> e.greater(NEG_ONE).and(e.less(ONE)), a -> .5 * Math.log((1 + a) / (1 - a)), a -> a.derive().div(a.squared().dec())),
	 COT (e -> SIN.ofS(e).notEquates(ZERO), a -> 1 / tan(a), a -> CSC.ofS(a).negate().squared().chain(a)),
	ACOT (COT, a -> Math.atan(1 / a), a -> a.derive().div(ONE.minus(a).negate())),
	 COTH(NOT_ZERO, a -> 1 / Math.tanh(a), a -> CSCH.ofS(a).negate().squared().chain(a)),
	ACOTH(COTH, e -> e.less(NEG_ONE).or(e.greater(ONE)), a -> .5 * Math.log((1 + a) / (a - 1)), a -> a.derive().div(a.squared().dec())),

	// DER(a -> 0, a -> a[0].derive().derive()),
	// INT()

	//TODO derivative and integral functions

	RNG(0, a -> VariableStatement.TAUTOLOGY, a -> Math.random(), a -> ZERO);

	static {
		// d/dx (log(b, a))  =  (da - (a*log(b,a)*db/b)) / a*lnb
		LOG.derivable = a -> a[1].derive().minus(a[1].times(LOG.ofS(a[0], a[1])).times(a[0].derive()).div(a[0])).div(a[1].times(LN.ofS(a[0])));
		SIN.derivable = a -> COS.ofS(a[0]).chain(a[0]);
		SINH.derivable = a -> COSH.ofS(a[0]).chain(a[0]);
		CSC.derivable = a -> CSC.ofS(a[0]).negate().times(COT.ofS(a[0])).chain(a[0]);
		CSCH.derivable = a -> CSCH.ofS(a[0]).negate().times(COTH.ofS(a[0])).chain(a[0]);
		SEC.derivable = a -> SEC.ofS(a[0]).times(TAN.ofS(a[0])).chain(a[0]);
		SECH.derivable = a -> SECH.ofS(a[0]).times(TANH.ofS(a[0])).chain(a[0]);
	}

	private int arguments;
	private Functional functional;
	private DomainSupplier domainSupplier;
	public FunctionDerivable derivable;
	private final String[] symbols;
	private MultiArgFunction inverse;

	MultiArgFunction() {
		this(null);
	}

	MultiArgFunction(Function<Double, Double> singleArgFun) {
		this(singleArgFun, null);
	}

	MultiArgFunction(MultiArgFunction inverse, Function<Double, Double> singleArgFun, Function<Expression, Expression> oneArgDer) {
		this(singleArgFun, oneArgDer);
		inverse.inverse = this;
	}

	MultiArgFunction(Function<Double, Double> singleArgFun, Function<Expression, Expression> oneArgDer) {
		this(Expression::combineDefaultDomains, singleArgFun, oneArgDer);
	}

	// MultivariableFunction(final int arguments, final Function<Double, Double> singleArgFunction, FunctionDerivable derivable, final String... symbols) {
	// 	this(Expression::combineDefaultDomains, arguments, a -> singleArgFunction.apply(a[0]), derivable, symbols);
	// }

	// MultivariableFunction(Function<Expression, Statement> oneArgDomainSup, Function<Double, Double> oneArgFun, String... symbols) {
	// 	this(a -> oneArgDomainSup.apply(a[0]), oneArgFun, null, symbols);
	// }

	MultiArgFunction(MultiArgFunction inverse,
					 Function<Expression, Statement> oneArgDomainSup,
					 Function<Double, Double> oneArgFunction,
					 Function<Expression, Expression> oneArgDer, String... symbols) {
		this(oneArgDomainSup, oneArgFunction, oneArgDer, symbols);
		inverse.inverse = this;
	}

	MultiArgFunction(Function<Expression, Statement> oneArgDomainSup,
					 Function<Double, Double> oneArgFunction,
					 Function<Expression, Expression> oneArgDer, String... symbols) {
		this(1, a -> oneArgDomainSup.apply(a[0]), a -> oneArgFunction.apply(a[0]), a -> oneArgDer.apply(a[0]), symbols);
	}


	MultiArgFunction(int arguments,
					 DomainSupplier domainSupplier,
					 Functional functional,
					 FunctionDerivable derivable, String... symbols) {
		this.functional = functional;
		this.arguments = arguments;
		this.domainSupplier = domainSupplier;
		this.derivable = derivable;
		this.symbols = new String[symbols.length + 2];
		this.symbols[0] = name();
		this.symbols[1] = name().toLowerCase();
		System.arraycopy(symbols, 0, this.symbols, 2, symbols.length);
	}

	public int getArguments() {
		return arguments;
	}

	/**
	 * "of Secure"; TODO for package use only
	 */
	public FunctionExpression ofS(Expression... other) {
		return new FunctionExpression(this, true, other);
	}

	public FunctionExpression of(Expression... other) throws IllegalArgumentAmountException {
		return new FunctionExpression(this, other);
	}

	@Override
	public double function(double... a) {
		return functional.function(a);
	}

	@Override
	public void checkArguments(int length) throws IllegalArgumentAmountException {
		if (length != arguments)
			throw new IllegalArgumentAmountException(String.format("wrong number of arguments. given %d but needed %d", length, arguments));
	}

	@Override
	public Expression derive(Expression... exp) {
		return derivable.derive(exp);
	}

	@Override
	public Statement domain(final Expression... expressions) {
		return domainSupplier.get(expressions);
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public MultiArgFunction inverse() {
		if (inverse == null)
			throw new IllegalInverseExpressionException("no inverse implementation for " + this);
		return inverse;
	}

	public Expression inverse(int arg, Expression... expressions) {
		if (this == LOG) {
			return arg == 0 ? POW.of(expressions[1], expressions[0].reciprocal())
						: arg == 1 ? POW.of(expressions[0], expressions[1])
					 		: null;
		} else if (this == LN) {
			return POW.of(Constant.E, expressions[0]);
		} else {
			return inverse().ofS(expressions);
		}
	}

}
