package bran.mathexprs.treeparts;

import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.functions.FunctionExpression;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public static final Constant NEG_E = new Constant(Math.E) { @Override public String toFullString() { return "-e"; } };
	public static final Constant NEG_PI = new Constant(Math.PI) { @Override public String toFullString() { return "-pi"; } };

	private final static Matcher constantMatcher
			= Pattern.compile("[+-]?([iI]|[eE]|[pP][iI]|NaN|Infinity|((((\\d+)(\\.)?((\\d+)?)" +
							  "([eE][+-]?(\\d+))?)|(\\.(\\d+)([eE][+-]?(\\d+))?)|" +
							  "(((0[xX]([\\da-fA-F]+)(\\.)?)|(0[xX]([\\da-fA-F]+)?(\\.)([\\da-fA-F]+)))" +
							  "[pP][+-]?(\\d+)))[fFdD]?))").matcher("");

	public static Constant of(final double value) {
		var x = 0x5p-5;
		return new Constant(value);
	}

	/**
	 * returns +-infinity if value exceeds range
	 */
	public static Constant of(final String value) {
		try {
			return new Constant(Double.parseDouble(value));
		} catch (NumberFormatException e) {
			final char[] valueChars = value.toCharArray();
			int firstInd = 0;
			boolean negative = false;
			switch (valueChars[0]){
				case '-':
					negative = true;
					//FALLTHROUGH
				case '+':
					firstInd++;
			}
			int length = valueChars.length - firstInd;
			switch(valueChars[firstInd]) {
				case 'i', 'I':
					if (length == 1)
						return negative ? NEG_i : i;
				case 'e', 'E':
					if (length == 1)
						return negative ? NEG_E : E;
				case 'p', 'P':
					if (length == 2) {
						final char secInd = valueChars[firstInd + 1];
						if (secInd == 'i' || secInd == 'I')
							return negative ? NEG_PI : PI;
					}
			}
			return null;
		}
	}

	public static boolean validName(final String prefix) {
		return constantMatcher.reset(prefix).matches();
	}

	Constant negative() {
		return new Constant(-number.doubleValue());
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

	private static class iBase extends Constant {

		public iBase(final double value) {
			super(value);
		}

		@Override
		public Expression derive() {
			return ZERO;
		}

		@Override
		public void appendGodelNumbers(final GodelBuilder godelBuilder) {
			godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
		}

		@Override
		public String toString() {
			return "i";
		}

	}

	public static Constant i = new iBase(Math.sqrt(-1));
	public static Constant NEG_i = new iBase(-Math.sqrt(-1)) {
		@Override
		public String toString() {
			return "-i";
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
