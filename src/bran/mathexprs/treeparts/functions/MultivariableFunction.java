package bran.mathexprs.treeparts.functions;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.*;

import java.util.Arrays;

import static bran.mathexprs.treeparts.Constant.*;
import static bran.mathexprs.treeparts.functions.DomainSupplier.*;

import static java.lang.Math.*;

public enum MultivariableFunction implements Function {
	LOG  (e -> ((e[0].greater(ZERO).and(e[0].notEquates(ONE))).or(e[0].equates(INFINITY))).and(e[1].greater(ZERO)),
		  2, a -> log(a[0]) / log(a[1])),
	LN   (e -> e[0].greater(ZERO),
		  1, a -> log(a[0]), a -> a[0].derive().div(a[0])),
	SIGN (1, a -> Math.signum(a[0]), a -> ZERO), // not roots
	ABS  (1, a -> abs(a[0]), a -> SIGN.ofS(a[0]).chain(a[0])),
	CEIL (1, a -> Math.ceil(a[0]), a -> ZERO),
	ROUND(1, a -> Math.round(a[0]), a -> ZERO), // not roots??
	FLOOR(1, a -> Math.floor(a[0]), a -> ZERO),
	SQRT (1, a -> Math.sqrt(a[0]), a -> a[0].derive().div(Constant.TWO.times(a[0].sqrt()))),

	 SIN (1, a -> Math.sin(a[0])),
	ASIN (e -> e[0].greaterEqual(NEG_ONE).and(e[0].lessEqual(ONE)),
		  1, a -> Math.asin(a[0]), a -> a[0].derive().div(ONE.minus(a[0]).sqrt())),
	 SINH(1, a -> Math.sinh(a[0])),
	ASINH(1, a -> Math.log(a[0] + Math.sqrt(a[0] * a[0] + 1)), a -> a[0].derive().div(a[0].squared().inc().sqrt())),
	 CSC (e -> SIN.ofS(e[0]).notEquates(ZERO),
		  1, a -> 1 / sin(a[0])),
	ACSC (e -> e[0].lessEqual(NEG_ONE).or(e[0].greaterEqual(ONE)),
		  1, a -> Math.asin(1 / a[0]), a -> a[0].derive().div(ABS.ofS(a[0]).times(ONE.minus(a[0]).sqrt()).negate())),
	 CSCH(NOT_ZERO,
		  1, a -> 1 / Math.sinh(a[0])),
	ACSCH(NOT_ZERO,
		  1, a -> Math.log((1 + Math.sqrt(1 + a[0] * a[0])) / a[0]), a -> a[0].derive().div(ABS.ofS(a[0]).times(a[0].squared().inc().sqrt()).negate())),

	 COS (1, a -> Math.cos(a[0]), a -> SIN.ofS(a[0]).negate().chain(a[0])),
	ACOS (e -> e[0].greaterEqual(NEG_ONE).and(e[0].lessEqual(ONE)),
		  1, a -> Math.acos(a[0]), a -> a[0].derive().div(ONE.minus(a[0]).sqrt().negate())),
	COSH (1, a -> Math.cosh(a[0]), a -> SINH.ofS(a[0]).chain(a[0])),
	ACOSH(e -> e[0].greaterEqual(ONE),
		  1, a -> Math.log(a[0] + Math.sqrt(a[0] * a[0] - 1)), a -> a[0].derive().div(a[0].squared().dec().sqrt())),
	 SEC (e -> COS.ofS(e[0]).notEquates(ZERO),
		  1, a -> 1 / cos(a[0])),
	ASEC (e -> e[0].lessEqual(NEG_ONE).or(e[0].greaterEqual(ONE)),
		  1, a -> Math.acos(1 / a[0]), a -> a[0].derive().div(ABS.ofS(a[0]).times(ONE.minus(a[0]).sqrt()))),
	 SECH(1, a -> 1 / Math.cosh(a[0])),
	ASECH(e -> e[0].greater(ZERO).and(e[0].lessEqual(ONE)),
		  1, a -> Math.log((1 + Math.sqrt(1 - a[0] * a[0])) / a[0]), a -> a[0].derive().div(ABS.ofS(a[0]).times(a[0].squared().dec().sqrt()).negate())),

	 TAN (e -> COS.ofS(e[0]).notEquates(ZERO),
		  1, a -> Math.tan(a[0]), a -> SEC.ofS(a[0]).squared().chain(a[0])),
	ATAN (1, a -> Math.atan(a[0]), a -> a[0].derive().div(ONE.minus(a[0]))),
	 TANH(1, a -> Math.tanh(a[0]), a -> SECH.ofS(a[0]).squared().chain(a[0])),
	ATANH(e -> e[0].greater(NEG_ONE).and(e[0].less(ONE)),
		  1, a -> .5 * Math.log((1 + a[0]) / (1 - a[0])), a -> a[0].derive().div(a[0].squared().dec())),
	 COT (e -> SIN.ofS(e[0]).notEquates(ZERO),
		  1, a -> 1 / tan(a[0]), a -> CSC.ofS(a[0]).negate().squared().chain(a[0])),
	ACOT (1, a -> Math.atan(1 / a[0]), a -> a[0].derive().div(ONE.minus(a[0]).negate())),
	 COTH(NOT_ZERO,
		  1, a -> 1 / Math.tanh(a[0]), a -> CSCH.ofS(a[0]).negate().squared().chain(a[0])),
	ACOTH(e -> e[0].less(NEG_ONE).or(e[0].greater(ONE)),
		  1, a -> .5 * Math.log((1 + a[0]) / (a[0] - 1)), a -> a[0].derive().div(a[0].squared().dec())),

	//TODO derivative and integral functions

	RNG(0, a -> Math.random(), a -> ZERO);

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
	private final Functional functional;
	private final DomainSupplier domainSupplier;
	public Derivable derivable;
	private final String[] symbols;

	MultivariableFunction(final int arguments, final Functional functional, final String... symbols) {
		this(Expression::combineDefaultDomains, arguments, functional, null, symbols);
	}

	MultivariableFunction(final int arguments, final Functional functional, Derivable derivable, final String... symbols) {
		this(Expression::combineDefaultDomains, arguments, functional, derivable, symbols);
	}

	MultivariableFunction(DomainSupplier domainSupplier, final int arguments, final Functional functional, final String... symbols) {
		this(domainSupplier, arguments, functional, null, symbols);
	}

	MultivariableFunction(DomainSupplier domainSupplier, final int arguments, final Functional functional, Derivable derivable, final String... symbols) {
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

}
