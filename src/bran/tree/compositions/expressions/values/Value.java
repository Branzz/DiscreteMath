package bran.tree.compositions.expressions.values;

import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.structure.Leaf;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;

import java.util.Objects;

public abstract class Value extends Expression implements Leaf {

	protected NumberLiteral number; // non-final; it can/will change in hypothetical - FOR VARIABLES

	public Value() {
		this(0.0);
	}

	public Value(double value) {
		number = new NumberLiteral(value);
	}

	public NumberLiteral value() {
		return number;
	}

	@Override
	public double evaluate() {
		return number.doubleValue();
	}

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		//TODO ?
	}

	@Override
	public String toFullString() {
		if (number.doubleValue() % 1 == 0)
			return String.valueOf(number.intValue());
		else
			return number.toString();
	}

	@Override
	public String toString() {
		return toFullString();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Value value && Objects.equals(value.number, number);
	}

	// @Override
	// public int compareTo(Composition other) {
	// 	if (other instanceof Value value)
	// 		return number.compareTo(value.number);
	// 	else
	// 		return super.compareTo(other);
	// }

	// @Override
	// public boolean equivalentTo(final Value other) {
	// 	return number.equals(other.number);
	// }

}
