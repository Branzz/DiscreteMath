package bran.mathexprs.treeparts;

import bran.sets.numbers.NumberLiteral;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Constant extends Value {

	public Constant(double value) {
		this.number = new NumberLiteral(value);
	}

	public static final Expression NEG_ONE = new Constant(-1.0);
	public static final Constant ZERO = new Constant(0.0);
	public static final Constant ONE = new Constant(1.0);
	public static final Constant E = new Constant(Math.E);
	public static final Constant PI = new Constant(Math.PI);
	public static final Constant INFINITY = new Constant(Double.POSITIVE_INFINITY);

	public static Constant of(final double value) {
		return new Constant(value);
	}

	@Override
	public List<Variable> getVariables() {
		return Collections.emptyList();
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

}
