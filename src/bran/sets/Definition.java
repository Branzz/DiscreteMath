package bran.sets;

import bran.logic.statements.Statement;
import bran.logic.statements.special.ExistentialNumbersStatement;
import bran.logic.statements.special.UniversalNumbersStatement;
import bran.mathexprs.Inequality;
import bran.mathexprs.InequalityType;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;

import static bran.mathexprs.treeparts.functions.MultivariableFunction.ABS;

public class Definition <T> {

	private final Definable<T> definable;
	private final String[] symbols;

	public Definition(Definable<T> definable, String... symbols) {
		this.definable = definable;
		this.symbols = symbols;
	}

	public static final Definition<Constant> ODD = new Definition<>(
			n -> n.mod(Constant.of(2)).equates(Constant.of(1)), "integer", "odd");
	public static final Definition<Constant> EVEN = new Definition<>(
			n -> n.mod(Constant.of(2)).equates(Constant.of(0)), "integer",  "even");
	public static final Definition<Constant> PRIME = new Definition<>(n -> {
				Variable r = new Variable("r"), s = new Variable("s");
				return n.greater(Constant.ONE).and(
						new UniversalNumbersStatement(new SpecialSet(SpecialSetType.Z), n.equates(r.times(s)).then(s.equates(n)), r, s));
			}, "a number is prime");
	public static final Definition<Constant> COMPOSITE = new Definition<>(
			n -> new Inequality(n, InequalityType.GREATER, Constant.of(1)).and(PRIME.definable.defined(n).not()),
			"a number is composite");
	// public static final Definition<Expression> EXISTS = new Definition<>(e -> e.getDomainConditions(), "expression exists"); // if for each of the functions used.
	// public static final Definition<Expression> DIFFERENTIABLE = new Definition<>(e -> e.getDomainConditions(), "equation is differentiable");
	public static final Definition<LimitExpression> LIMIT = new Definition<>(e -> { // https://tutorial.math.lamar.edu/classes/calcI/defnoflimit.aspx
				Variable epsilon = new Variable("\u03b5"), delta = new Variable("\u03b4");
				return new UniversalNumbersStatement(new SpecialSet(SpecialSetType.R),
						epsilon.greater(Constant.ZERO).and(new ExistentialNumbersStatement(new SpecialSet(SpecialSetType.R),
						Constant.ZERO.less(ABS.ofS(e.approached.minus(e.approaches))).and(ABS.ofS(e.approached.minus(e.approaches)).less(delta))
						.then(e.function.minus(e.limit).less(epsilon)), delta)), epsilon);
				});

	@FunctionalInterface
	interface Definable <T> {
		Statement defined(T t);
	}

	@Override
	public String toString() {
		return symbols[0] + " if " + definable.defined(null);
	}

	public String test(T arg) {
		return "for the " + symbols[0] + " " + arg + ", it is " + symbols[1] + " iff " + definable.defined(arg) + ", which is " + definable.defined(arg).truth();
	}

}
