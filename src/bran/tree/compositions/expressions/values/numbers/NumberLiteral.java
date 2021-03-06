package bran.tree.compositions.expressions.values.numbers;

import java.io.Serial;

public class NumberLiteral extends java.lang.Number implements Comparable<NumberLiteral> {


	// Treat like a primitive
// TODO tie in logic.Variable

	@Serial
	private static final long serialVersionUID = 1L;

	final double value;

//	private static ArrayList<Number> literals;
//
//	static {
//		literals = new ArrayList<Number>();
//	}

	public NumberLiteral(double value) {
		this.value = value;
//		for (Number n : literals)
//			if (n.equals(this)) {
//				n = this;
//				return;
//			}
//		literals.add(this);
	}

	public boolean equals(Object other) {
		return other instanceof NumberLiteral && Double.compare(value, ((NumberLiteral) other).getValue()) == 0;
	}

	public double getValue() {
		return value;
	}

	public static final NumberLiteral INFINITY = new NumberLiteral(Double.POSITIVE_INFINITY);
	public static final NumberLiteral NEG_INFINITY = new NumberLiteral(Double.NEGATIVE_INFINITY);

	public NumberLiteral add(NumberLiteral other) {
		return new NumberLiteral(other.getValue() + value);
	}

	public NumberLiteral subtract(NumberLiteral other) {
		return new NumberLiteral(other.getValue() - value);
	}

	public NumberLiteral multiply(NumberLiteral other) {
		return new NumberLiteral(other.getValue() * value);
	}

	public NumberLiteral divide(NumberLiteral other) {
		return new NumberLiteral(other.getValue() / value);
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

	@Override
	public float floatValue() {
		return (float) value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	// @Override
	// public NumberLiteral clone() {
	// 	return new NumberLiteral(value);
	// }

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public int compareTo(final NumberLiteral numberLiteral) {
		return Double.compare(value, numberLiteral.value);
	}

}
