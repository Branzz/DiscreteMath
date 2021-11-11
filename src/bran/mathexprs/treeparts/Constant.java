package bran.mathexprs.treeparts;

import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.functions.FunctionExpression;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelBuilder;

import java.util.*;

public class Constant extends Value {

	public Constant(double value) {
		super(value);
	}

	public Constant(double value, boolean exists) {
		super(value);
		domainConditions = VariableStatement.of(exists);
		// domainConditions = (this.equates(INFINITY).or(this.equates(NEG_INFINITY)).or(this.equates(NAN))).not();
		// ^^^ REQUIRES static PRE-INIT for these Constants
	}

	public static final Constant NEG_INFINITY = new Constant(Double.NEGATIVE_INFINITY, false);
	public static final Constant INFINITY = new Constant(Double.POSITIVE_INFINITY, false);
	public static final Constant NAN = new Constant(Double.NaN, false);
	public static final Constant NEG_ONE = new Constant(-1.0);
	public static final Constant NEG_ZERO = new Constant(-0.0) { @Override public String toFullString() { return "-0"; } };
	public static final Constant ZERO = new Constant(0.0);
	public static final Constant ONE = new Constant(1.0);
	public static final Constant TWO = new Constant(2.0);
	public static final Constant E = new Constant(Math.E) { @Override public String toFullString() { return "e"; } };
	public static final Constant PI = new Constant(Math.PI) { @Override public String toFullString() { return "pi"; } };


	public static Constant of(final double value) {
		return new Constant(value);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet(); // TODO represent null as empty set?
	}

	@Override
	public Expression simplified() {
		return this;
	}

	@Override
	public Expression derive() {
		return this.equals(NAN) || this.equals(INFINITY) || this.equals(NEG_INFINITY) ? NAN : ZERO;
	}

	@Override
	public boolean respect(final Collection<Variable> respectsTo) {
		return false;
	}

	@Override
	public Expression clone() {
		return new Constant(number.doubleValue());
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		int num = number.intValue();
		if (num < 0) {
			godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
			num = -num;
		}
		while (num-- > 0)
			godelBuilder.push(GodelNumberSymbols.SUCCESSOR);
		godelBuilder.push(GodelNumberSymbols.ZERO);
	}

	public static Constant i = new Constant(Math.sqrt(-1)) {

		@Override
		public Expression derive() {
			return ZERO;
		}

		@Override
		public void appendGodelNumbers(final GodelBuilder godelBuilder) {
			godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
		}

	};

	// @Override
	// public Constant squared() {
	// 	return Constant.of(Math.pow(number.doubleValue(), 2));
	// }
	//
	// @Override
	// public Constant cubed() {
	// 	return Constant.of(Math.pow(number.doubleValue(), 3));
	// }
	//
	// @Override
	// public Constant reciprocal() {
	// 	return Constant.of(1 / number.doubleValue());
	// }
	//
	// @Override
	// public Constant negate() {
	// 	return Constant.of(-number.doubleValue());
	// }
	//
	// @Override
	// public Constant inc() {
	// 	return Constant.of(number.doubleValue() + 1);
	// }
	//
	// @Override
	// public Constant dec() {
	// 	return Constant.of((number.doubleValue() - 1));
	// }
	//
	// @Override
	// public Constant sqrt() {
	// 	return Constant.of(Math.sqrt(number.doubleValue()));
	// }

}
