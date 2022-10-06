package bran.tree.compositions.expressions.operators;

import bran.parser.matching.Pattern;
import bran.parser.matching.Tokenable;
import bran.tree.compositions.Definition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.ExpressionDisplayStyle;
import bran.tree.compositions.godel.GodelNumber;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.statements.Statement;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;

import java.lang.reflect.Constructor;
import java.util.function.Function;

import static bran.tree.compositions.expressions.ExpressionDisplayStyle.expressionStyle;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.LN;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.LOG;
import static bran.tree.compositions.expressions.operators.DomainSupplier.DENOM_NOT_ZERO;
import static bran.tree.compositions.expressions.values.Constant.*;

/**
 * <pre>
 * reasoning for the power's domain
 * from {@link java.lang.FdLibm.Pow}:                                            | simplified logic:
 * for x**p,                                                   |
 *   1.  (anything) ** 0  is 1                                 | p == 0 OR
 *   4.  NAN ** (anything except 0) is NAN                     | (((x exists AND
 *   14. -0 ** (odd integer) = -( +0 ** (odd integer) )        |
 *   10. +0 ** (+anything except 0, NAN)               is +0   |
 *   11. -0 ** (+anything except 0, NAN, odd integer)  is +0   |
 *   12. +0 ** (-anything except 0, NAN)               is +INF |
 *   13. -0 ** (-anything except 0, NAN, odd integer)  is +INF | NOT (x == +-0 AND p < 0)) OR
 *   16. +INF ** (-anything except 0,NAN) is +0                | // already checked p==0
 *   17. -INF ** (anything)  = -0 ** (-anything)               | (x == +-INF AND p < 0) ) AND
 *   3.  (anything)  **   NAN is NAN                           | (p exists OR
 *   9.  +-1         ** +-INF is NAN                           |
 *   5.  +-(|x| > 1) **  +INF is +INF                          |
 *   8.  +-(|x| < 1) **  -INF is +INF                          |
 *   6.  +-(|x| > 1) **  -INF is +0                            | (p == -INF AND (x < -1 OR x > 1)) OR
 *   7.  +-(|x| < 1) **  +INF is +0                            | (p == +INF AND (x > -1 AND x < 1)) AND
 *   15. +INF ** (+anything except 0,NAN) is +INF              |
 *   19. (-anything except 0 and inf) ** (non-integer) is NAN  | NOT (x < 0 && p not int)))
 * </pre>
 */
public enum ArithmeticOperator implements ForkOperator<Double, Expression, Expression> {
	POW(2, Math::pow, (a, b) -> (a.pow(b)).times((b.div(a).times(a.derive())).plus(LN.ofS(a).times(b.derive()))),
		(x, p) -> p.equates(ZERO)
				.or(((x.getDomainConditions()
						.and(((x.equates(ZERO).or(x.equates(NEG_ZERO)).and(p.less(NEG_ZERO))).not())))
					.or((x.equates(INFINITY).or(x.equates(NEG_INFINITY))).and(p.less(ZERO))))
					.and(p.getDomainConditions()
						.or(p.equates(NEG_INFINITY).and(x.less(NEG_ONE).or(x.greater(ONE))))
						.or(p.equates(INFINITY).and(x.greater(NEG_ONE).and(x.less(ONE)))))
					.and((x.less(NEG_ZERO).and(Definition.INTEGER.of(p).not())).not())) // only in double's version of pow
		, false, "^", "power"), // a^b * ((b/a) * da + ln(a) * db)
	MUL(3, (a, b) -> a * b, (a, b) -> a.times(b.derive()).plus(a.derive().times(b)), true, GodelNumberSymbols.TIMES, "*", "times", "\u00D7", "\u22C5"),
	DIV(3, (a, b) -> a / b, (a, b) -> ((b.times(a.derive())).minus(a.times(b.derive()))).div(b.squared()), DENOM_NOT_ZERO, false, "/", "over", "\u00F7"),
	MOD(3, (a, b) -> a % b, (a, b) -> a.derive().limitDomain(a.mod(b).notEquates(ZERO)) /*jump not diff.*/, DENOM_NOT_ZERO, false, "%", "mod"), // TODO derivative
	ADD(4, Double::sum,     (a, b) -> a.derive().plus(b.derive()), true, GodelNumberSymbols.PLUS, "+", "plus"),
	SUB(4, (a, b) -> a - b, (a, b) -> a.derive().minus(b.derive()), false, "-", "minus");

	private ArithmeticOperator inverse;
	private Function<Expression, Expression> inverter;
	private Function<Expression, Expression> invertAndSimplifier;

	private final AssociativityPrecedenceLevel level;
	private final Operable operable;
	private final OperatorDerivable derivable;
	private final DomainSupplier domainSupplier;
	private final String[] symbols;
	private final boolean commutative;
	private final GodelNumberSymbols godelNumberSymbols;

	ArithmeticOperator(int level, final Operable operable, final OperatorDerivable derivable,
					   boolean commutative, final String... symbols) {
		this(level, operable, derivable, (l, r) -> defaultConditions(l).and(defaultConditions(r)), commutative, symbols);
	}

	ArithmeticOperator(int level, final Operable operable, final OperatorDerivable derivable, final DomainSupplier domainSupplier,
					   boolean commutative, final String... symbols) {
		this(level, operable, derivable, domainSupplier, commutative, GodelNumberSymbols.SYNTAX_ERROR, symbols);
	}

	ArithmeticOperator(int level, final Operable operable, final OperatorDerivable derivable,
					   boolean commutative, GodelNumberSymbols godelNumberSymbols, final String... symbols) {
		this(level, operable, derivable, (l, r) -> defaultConditions(l).and(defaultConditions(r)), commutative, godelNumberSymbols, symbols);
	}

	ArithmeticOperator(int level, final Operable operable, final OperatorDerivable derivable, final DomainSupplier domainSupplier,
					   boolean commutative, GodelNumberSymbols godelNumberSymbols, final String... symbols) {
		this.level = AssociativityPrecedenceLevel.of(level);
		this.operable = operable;
		this.derivable = derivable;
		this.domainSupplier = domainSupplier;
		this.commutative = commutative;
		this.godelNumberSymbols = godelNumberSymbols;
		this.symbols = symbols;
	}

	static {
		// POW.inverse = LOG;
		MUL.inverse = DIV; // 1 / x
		DIV.inverse = MUL; // 1 / x
		MOD.inverse = MOD; // TODO
		ADD.inverse = SUB; // 0 - x
		SUB.inverse = ADD; // 0 - x

		SUB.inverter = NEG_ONE::times;
		SUB.invertAndSimplifier = e -> ZERO.minus(e).simplified(ZERO, e);
		DIV.inverter = ONE::div;
		DIV.invertAndSimplifier = e -> ONE.div(e).simplified(ONE, e);

	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return level;
	}

	public Double operate(Expression left, Expression right) {
		return operable.operate(left.evaluate(), right.evaluate());
	}

	public Expression derive(Expression left, Expression right) {
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
	public ArithmeticOperator inverse() {
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

	public Expression of(Expression left, Expression right) {
		return new ExpressionOperation(left, this, right);
	}

	/**
	 * f(a) = left op right
	 * f(0) :=  left contains x
	 * f^-1(o, 0) = o inv.op right
	 * f^-1(o, 1) = o not commutative ? op : inv.op right
	 */
	public Expression inverse(int arg, Expression left, Expression right) {
		if (this == POW) {
			return arg == 0 ? left.pow(right.reciprocal())
						   : arg == 1 ? LOG.ofS(left, right)
									 : null;
		} else {
			return (arg == 1 && !commutative ? this : inverse).of(left, right); // not totally true #isCommutative
		}
	}

	public Expression invert(Expression expression) {
		return inverter == null ? expression : inverter.apply(expression);
	}

	public Expression invertSimplifier(Expression expression) {
		return invertAndSimplifier == null ? expression : invertAndSimplifier.apply(expression);
	}

	// @Override
	// public Pattern<ExpressionOperation> pattern() {
	// 	return new Pattern<>(precedence(),
	// 						 (Constructor<ExpressionOperation>) ExpressionOperation.class.getConstructors()[0],
	// 						 Expression.class, ArithmeticOperator.class, Expression.class);
	// }

}
