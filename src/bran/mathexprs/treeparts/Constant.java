package bran.mathexprs.treeparts;

import bran.sets.numbers.NumberLiteral;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.*;

public class Constant extends Value {

	public Constant(double value) {
		this.number = new NumberLiteral(value);
	}

	public static final Expression NEG_ONE = new Constant(-1.0);
	public static final Constant ZERO = new Constant(0.0);
	public static final Constant ONE = new Constant(1.0);
	public static final Constant E = new Constant(Math.E) { @Override public String toString() { return "e"; } };
	public static final Constant PI = new Constant(Math.PI) { @Override public String toString() { return "pi"; } };
	public static final Constant INFINITY = new Constant(Double.POSITIVE_INFINITY);

	public static Constant of(final double value) {
		return new Constant(value);
	}

	@Override
	public Set<Variable> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public Expression simplified() {
		return this;
	}

	@Override
	public Expression derive() {
		return Constant.ZERO;
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
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		int num = number.intValue();
		if (num < 0) {
			godelNumbers.push(GodelNumberSymbols.SYNTAX_ERROR);
			num = -num;
		}
		while (num-- > 0)
			godelNumbers.push(GodelNumberSymbols.SUCCESSOR);
		godelNumbers.push(GodelNumberSymbols.ZERO);
	}

}
