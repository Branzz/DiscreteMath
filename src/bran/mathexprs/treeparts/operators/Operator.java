package bran.mathexprs.treeparts.operators;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.mathexprs.ExpressionDisplayStyle;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.sets.Definition;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.tree.Associativity;
import bran.tree.ForkOperator;

import static bran.mathexprs.ExpressionDisplayStyle.expressionStyle;
import static bran.mathexprs.treeparts.Constant.*;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.LN;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.LOG;
import static bran.mathexprs.treeparts.operators.DomainSupplier.DENOM_NOT_ZERO;
import static bran.mathexprs.treeparts.operators.ExpressionOperatorType.*;

public enum Operator implements ForkOperator {
	/**
	 * for x**p, {@link java.lang.FdLibm.Pow}
     *   1.  (anything) ** 0  is 1									p == 0 OR
	 *   4.  NAN ** (anything except 0) is NAN							(((x exists AND
     *   14. -0 ** (odd integer) = -( +0 ** (odd integer) )
	 *   10. +0 ** (+anything except 0, NAN)               is +0
	 *   11. -0 ** (+anything except 0, NAN, odd integer)  is +0
     *   12. +0 ** (-anything except 0, NAN)               is +INF
     *   13. -0 ** (-anything except 0, NAN, odd integer)  is +INF			NOT (x == +-0 AND p < 0)) OR
	 *   16. +INF ** (-anything except 0,NAN) is +0						// already checked p==0
	 *   17. -INF ** (anything)  = -0 ** (-anything)					(x == +-INF AND p < 0) ) AND
	 *   3.  (anything) ** NAN is NAN								(p exists OR
     *   9.  +-1         ** +-INF is NAN
	 *   5.  +-(|x| > 1) **  +INF is +INF
     *   8.  +-(|x| < 1) **  -INF is +INF
	 *   6.  +-(|x| > 1) **  -INF is +0									(p == -INF AND (x < -1 OR x > 1)) OR
	 *   7.  +-(|x| < 1) **  +INF is +0									(p == +INF AND (x > -1 AND x < 1)) AND
	 *   15. +INF ** (+anything except 0,NAN) is +INF
	 * 	 19. (-anything except 0 and inf) ** (non-integer) is NAN	NOT (x < 0 && p not int)))
	 */
	POW(ExpressionOperatorType.E, Math::pow, (a, b) -> (a.pow(b)).times((b.div(a).times(a.derive())).plus(LN.ofS(a).times(b.derive()))),
		(x, p) -> p.equates(ZERO)
				.or(((x.getDomainConditions()
						.and(((x.equates(ZERO).or(x.equates(NEG_ZERO)).and(p.less(NEG_ZERO))).not())))
					.or((x.equates(INFINITY).or(x.equates(NEG_INFINITY))).and(p.less(ZERO))))
					.and(p.getDomainConditions()
						.or(p.equates(NEG_INFINITY).and(x.less(NEG_ONE).or(x.greater(ONE))))
						.or(p.equates(INFINITY).and(x.greater(NEG_ONE).and(x.less(ONE)))))
					.and((x.less(NEG_ZERO).and(Definition.INTEGER.of(p).not())).not())) // only in double's version of pow
		, false, "^", "power"), // a^b * ((b/a) * da + ln(a) * db)
	MUL(MD, (a, b) -> a * b, (a, b) -> a.times(b.derive()).plus(a.derive().times(b)), true, GodelNumberSymbols.TIMES, "*", "times"),
	DIV(MD, (a, b) -> a / b, (a, b) -> ((b.times(a.derive())).minus(a.times(b.derive()))).div(b.squared()), DENOM_NOT_ZERO, false, "/", "over"),
	MOD(MD, (a, b) -> a % b, (a, b) -> a.derive().limitDomain(a.mod(b).notEquates(ZERO)) /*jump not diff.*/, DENOM_NOT_ZERO, false, "%", "mod"), // TODO derivative
	ADD(AS, Double::sum,     (a, b) -> a.derive().plus(b.derive()), true, GodelNumberSymbols.PLUS, "+", "plus"),
	SUB(AS, (a, b) -> a - b, (a, b) -> a.derive().minus(b.derive()), false, "-", "minus");

	private bran.tree.Operator inverse;
	private final ExpressionOperatorType operatorType;
	private final Operable operable;
	private final Derivable derivable;
	private final DomainSupplier domainSupplier;
	private final String[] symbols;
	private final boolean commutative;
	private final GodelNumberSymbols godelNumberSymbols;

	Operator(final ExpressionOperatorType operatorType, final Operable operable, final Derivable derivable,
			 boolean commutative, final String... symbols) {
		this(operatorType, operable, derivable, (l, r) -> defaultConditions(l).and(defaultConditions(r)), commutative, symbols);
	}

	Operator(final ExpressionOperatorType operatorType, final Operable operable, final Derivable derivable, final DomainSupplier domainSupplier,
			 boolean commutative, final String... symbols) {
		this(operatorType, operable, derivable, domainSupplier, commutative, GodelNumberSymbols.SYNTAX_ERROR, symbols);
	}

	Operator(final ExpressionOperatorType operatorType, final Operable operable, final Derivable derivable,
			 boolean commutative, GodelNumberSymbols godelNumberSymbols, final String... symbols) {
		this(operatorType, operable, derivable, (l, r) -> defaultConditions(l).and(defaultConditions(r)), commutative, godelNumberSymbols, symbols);
	}

	Operator(final ExpressionOperatorType operatorType, final Operable operable, final Derivable derivable, final DomainSupplier domainSupplier,
			 boolean commutative, GodelNumberSymbols godelNumberSymbols, final String... symbols) {
		this.operatorType = operatorType;
		this.operable = operable;
		this.derivable = derivable;
		this.domainSupplier = domainSupplier;
		this.commutative = commutative;
		this.godelNumberSymbols = godelNumberSymbols;
		this.symbols = symbols;
	}

	static {
		POW.inverse = LOG;
		MUL.inverse = DIV; // 1 / x
		DIV.inverse = MUL; // 1 / x
		MOD.inverse = MOD;
		ADD.inverse = SUB; // 0 - x
		SUB.inverse = ADD; // 0 - x
	}

	@Override
	public int getOrder() {
		return operatorType.precedence();
	}

	@Override
	public int maxOrder() {
		return MAX_ORDER;
	}

	@Override
	public int minOrder() {
		return MIN_ORDER;
	}

	@Override
	public Associativity getDirection() {
		return operatorType.associativity();
	}

	public double operate(double left, double right) {
		return operable.operate(left, right);
	}

	public Expression derive(final Expression left, final Expression right) {
		return derivable.derive(left, right);
	}

	public Statement domain(Expression left, Expression right) {
		return domainSupplier.get(left, right);
	}

	@Override
	public String toString() {
		return toString(expressionStyle);
	}

	public String toString(ExpressionDisplayStyle displayStyle) {
		return switch(displayStyle) {
			case NAME -> symbols[1].toUpperCase();
			case LOWERCASE_NAME -> symbols[1];
			default -> symbols[0];
		};
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public bran.tree.Operator inverse() {
		return inverse;
	}

	public boolean isCommutative() {
		return commutative;
	}

	public GodelNumber godelOperator() {
		return godelNumberSymbols;
	}

	public int godelOrder() {
		return switch (godelNumberSymbols) {
			case TIMES -> 12;
			case PLUS -> 11;
			default -> 13;
		};
	}

}
