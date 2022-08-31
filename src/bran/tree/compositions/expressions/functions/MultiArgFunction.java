package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.exceptions.IllegalInverseExpressionException;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.functions.appliers.DomainSupplier;
import bran.tree.compositions.expressions.functions.appliers.FunctionDerivable;
import bran.tree.compositions.expressions.functions.appliers.Functional;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;

import java.util.function.Function;

import static bran.tree.compositions.expressions.functions.appliers.DomainSupplier.NOT_ZERO;
import static bran.tree.compositions.expressions.operators.Operator.POW;
import static bran.tree.compositions.expressions.values.Constant.*;
import static java.lang.Math.*;

public enum MultiArgFunction implements ExpFunction {
	LOG   (2, e -> ((e[0].greater(ZERO).and(e[0].notEquates(ONE))).or(e[0].equates(INFINITY))).and(e[1].greater(ZERO)),
		   a -> log(a[0]) / log(a[1]), null),
	LN    (e -> e.greater(ZERO), Math::log, a -> a.derive().div(a)),
	SIGN  (Math::signum, a -> ZERO), // not roots
	ABS   (Math::abs, a -> SIGN.ofS(a).chain(a)),
	CEIL  (Math::ceil, a -> ZERO),
	ROUND (a -> (double) round(a), a -> ZERO), // not roots??
	FLOOR (Math::floor, a -> ZERO),
	SQRT  (e -> e.greaterEqual(ZERO), Math::sqrt, a -> a.derive().div(Constant.TWO.times(a.sqrt()))),
	 SIN  (Math::sin),
	ASIN  (SIN, e -> e.greaterEqual(NEG_ONE).and(e.lessEqual(ONE)), Math::asin, a -> a.derive().div(ONE.minus(a).sqrt())),
	 SINH (Math::sinh),
	ASINH (SINH, a -> log(a + sqrt(a * a + 1)), a -> a.derive().div(a.squared().inc().sqrt())),
	 CSC  (e -> SIN.ofS(e).notEquates(ZERO), a -> 1 / sin(a), null),
	ACSC  (CSC, e -> e.lessEqual(NEG_ONE).or(e.greaterEqual(ONE)), a -> asin(1 / a), a -> a.derive().div(ABS.ofS(a).times(ONE.minus(a).sqrt()).negate())),
	 CSCH (NOT_ZERO, a -> 1 / sinh(a), null),
	ACSCH (CSCH, NOT_ZERO, a -> log((1 + sqrt(1 + a * a)) / a), a -> a.derive().div(ABS.ofS(a).times(a.squared().inc().sqrt()).negate())),
	 COS  (Math::cos, a -> SIN.ofS(a).negate().chain(a)),
	ACOS  (COS, e -> e.greaterEqual(NEG_ONE).and(e.lessEqual(ONE)), Math::acos, a -> a.derive().div(ONE.minus(a).sqrt().negate())),
	COSH  (Math::cosh, a -> SINH.ofS(a).chain(a)),
	ACOSH (COSH, e -> e.greaterEqual(ONE), a -> log(a + sqrt(a * a - 1)), a -> a.derive().div(a.squared().dec().sqrt())),
	 SEC  (e -> COS.ofS(e).notEquates(ZERO), a -> 1 / cos(a), null),
	ASEC  (SEC, e -> e.lessEqual(NEG_ONE).or(e.greaterEqual(ONE)), a -> acos(1 / a), a -> a.derive().div(ABS.ofS(a).times(ONE.minus(a).sqrt()))),
	 SECH (a -> 1 / cosh(a)),
	ASECH (SECH, e -> e.greater(ZERO).and(e.lessEqual(ONE)), a -> log((1 + sqrt(1 - a * a)) / a), a -> a.derive().div(ABS.ofS(a).times(a.squared().dec().sqrt()).negate())),
	 TAN  (e -> COS.ofS(e).notEquates(ZERO), Math::tan, a -> SEC.ofS(a).squared().chain(a)),
	ATAN  (TAN, Math::atan, a -> a.derive().div(ONE.minus(a))),
	 TANH (Math::tanh, a -> SECH.ofS(a).squared().chain(a)),
	ATANH (TANH, e -> e.greater(NEG_ONE).and(e.less(ONE)), a -> .5 * log((1 + a) / (1 - a)), a -> a.derive().div(a.squared().dec())),
	 COT  (e -> SIN.ofS(e).notEquates(ZERO), a -> 1 / tan(a), a -> CSC.ofS(a).negate().squared().chain(a)),
	ACOT  (COT, a -> Math.atan(1 / a), a -> a.derive().div(ONE.minus(a).negate())),
	 COTH (NOT_ZERO, a -> 1 / Math.tanh(a), a -> CSCH.ofS(a).negate().squared().chain(a)),
	ACOTH (COTH, e -> e.less(NEG_ONE).or(e.greater(ONE)), a -> .5 * Math.log((1 + a) / (a - 1)), a -> a.derive().div(a.squared().dec())),
	RNG   (0, a -> VariableStatement.TAUTOLOGY, a -> Math.random(), a -> ZERO),
	ACOT  (COT, a -> atan(1 / a), a -> a.derive().div(ONE.minus(a).negate())),
	 COTH (NOT_ZERO, a -> 1 / tanh(a), a -> CSCH.ofS(a).negate().squared().chain(a)),
	ACOTH (COTH, e -> e.less(NEG_ONE).or(e.greater(ONE)), a -> .5 * log((1 + a) / (a - 1)), a -> a.derive().div(a.squared().dec())),

//	INVERSE((Double e) -> e, (Expression e) -> e[0].inverse().iterator().next().derive()),

	TETRA (2, e -> VariableStatement.TAUTOLOGY,
			a -> a[0] > 100 ? Double.POSITIVE_INFINITY : IntStream.range(0, (int) (a[0])).asDoubleStream().map(i -> a[1]).reduce(1.0, (l, r) -> pow(r, l)),
			a -> {
				if (a[0] instanceof Constant n) {
					if (n.value().doubleValue() == 0)
						return ZERO;
					else if (n.value().doubleValue() == 1)
						return a[1].derive();
					else if (n.value().doubleValue() > 100)
						return null;
					else
						return tetraDerive(n.value().intValue(), a[1]).chain(a[1]);
				} else {
					return null;
				}
			}),
	// DER(a -> 0, a -> a[0].derive().derive()),
	// INT()

	//TODO derivative and integral functions

	NOP   (e -> e), // LINE
	NULL  (),
	;

	private static Expression tetraDerive(int n, Expression x) {
		return n == 2 ? tetra(2, x).times(ONE.plus(LN.ofS(x)))
				: tetra(n, x).times(tetra(n - 1, x).div(x).plus(tetraDerive(n - 1, x))).times(LN.ofS(x));
	}

	public static Expression tetra(int n, Expression x) {
		return TETRA.ofS(Constant.of(n), x);
	}

	public static Expression tower(Expression... args) {
		if (args[0] instanceof Constant c)
			return tower(c.value().intValue(), args[1]);
		else
			return null;
	}

	public static Expression tower(int n, Expression x) {
		if (n == 0)
			return ONE;
		Expression tower = x;
		for (int i = 1; i < n; i++)
			tower = x.pow(tower);
		return tower;
	}

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

	private final int arguments;
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

	public int getArgAmount() {
		return arguments;
	}

	@Override
	public double function(double... a) {
		return functional.function(a);
	}

	@Override
	public void checkArguments(int length) throws IllegalArgumentAmountException {
		checkArguments(length, arguments);
	}

	@Override
	public Expression derive(Expression... exp) {
		return derivable.derive(exp);
	}

	@Override
	public Statement domain(Expression... expressions) throws IllegalArgumentAmountException {
		checkArguments(expressions.length);
		return domainSupplier.get(expressions);
	}

	@Override
	public String toString() {
		return symbols.length > 0 ? symbols[0] : name();
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

	@Override
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
