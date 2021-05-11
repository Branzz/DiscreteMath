package bran.mathexprs.treeparts;

import bran.tree.Leaf;
import bran.sets.numbers.NumberLiteral;

import java.util.Objects;

public abstract class Value extends Expression implements Leaf {

	protected NumberLiteral number; // non-final; it can/will change in hypothetical - FOR VARIABLES ONLY

	public Value() {
		number = new NumberLiteral(0.0);
	}

	@Override
	public double evaluate() {
		return number.doubleValue();
	}

	@Override
	public String toString() {
		if (number.doubleValue() % 1 == 0)
			return String.valueOf(number.intValue());
		return number.toString();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof Value v && Objects.equals(v.number, number);
	}

	@Override
	public int compareTo(Expression other) {
		if (other instanceof Value value)
			return number.compareTo(value.number);
		else
			return super.compareTo(other);
	}

	// @Override
	// public boolean equivalentTo(final Value other) {
	// 	return number.equals(other.number);
	// }

}
