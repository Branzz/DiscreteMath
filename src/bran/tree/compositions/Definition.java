package bran.tree.compositions;

import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.special.quantifier.UniversalNumbersStatement;
import bran.tree.compositions.statements.special.equivalences.inequality.Inequality;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.sets.regular.SpecialSetType;

public class Definition<T> {

	private final Definable<T> definable;
	private final String[] symbols;

	public Definition(Definable<T> definable, String... symbols) {
		this.definable = definable;
		this.symbols = symbols;
	}

	public static final Definition<Constant> ODD = new Definition<>(
			n -> n.mod(Constant.TWO).equates(Constant.ONE), "integer", "odd"); // Actually, should be: there exists a k such that n = 2 * k + 1
	public static final Definition<Constant> EVEN = new Definition<>(
			n -> n.mod(Constant.TWO).equates(Constant.ZERO), "integer",  "even");
	public static final Definition<Constant> PRIME = new Definition<>(n -> {
				Variable r = new Variable("r"), s = new Variable("s");
				return n.greater(Constant.ONE).and(
						new UniversalNumbersStatement(new SpecialSet(SpecialSetType.Z), n.equates(r.times(s)).then(s.equates(n)), r, s));
			}, "a number is prime");
	public static final Definition<Constant> COMPOSITE = new Definition<>(
			n -> new Inequality(n, InequalityType.GREATER, Constant.ONE).and(PRIME.definable.defined(n).not()),
			"a number is composite");
	public static final Definition<Expression> INTEGER = new Definition<>(
			n -> n.mod(Constant.ONE).equates(Constant.ZERO), "real number", "integer");

	// public static final Definition<Expression> EXISTS = new Definition<>(e -> e.getDomainConditions(), "expression exists"); // if for each of the functions used.
	// public static final Definition<Expression> DIFFERENTIABLE = new Definition<>(e -> e.getDomainConditions(), "equation is differentiable");
	// public static final Definition<LimitExpression> LIMIT = new Definition<>(e -> { // https://tutorial.math.lamar.edu/classes/calcI/defnoflimit.aspx
	// 			Variable epsilon = new Variable("\u03b5"), delta = new Variable("\u03b4");
	// 			return new UniversalNumbersStatement(new SpecialSet(SpecialSetType.R),
	// 					epsilon.greater(Constant.ZERO).and(new ExistentialNumbersStatement(new SpecialSet(SpecialSetType.R),
	// 					Constant.ZERO.less(ABS.ofS(e.approached.minus(e.approaches))).and(ABS.ofS(e.approached.minus(e.approaches)).less(delta))
	// 					.then(e.function.minus(e.limit).less(epsilon)), delta)), epsilon);
	// 			});

	@FunctionalInterface
	interface Definable<T> {
		Statement defined(T t);
	}

	public Statement of(T t) {
		return definable.defined(t);
	}

	@Override
	public String toString() {
		return symbols[0] + " if " + definable.defined(null);
	}

	public String test(T arg) {
		return "for the " + symbols[0] + " " + arg + ", it is " + symbols[1] + " iff " + definable.defined(arg)
			   + ", which is " + definable.defined(arg).truth() + ", so " + arg + " is " + symbols[1];
	}

}
