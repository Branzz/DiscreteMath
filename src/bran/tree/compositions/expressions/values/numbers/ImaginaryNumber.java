package bran.tree.compositions.expressions.values.numbers;

public class ImaginaryNumber { // TODO extends Value/NumberLiteral

	/**
	 * a + bi
	 */
	private final double a;
	private final double b;

	public ImaginaryNumber() {
		this(0d, 0d);
	}

	public ImaginaryNumber(double a) {
		this(a, 0d);
	}

	public ImaginaryNumber(double a, double b) {
		this.a = a;
		this.b = b;
	}

	public ImaginaryNumber add(ImaginaryNumber n) {
		return new ImaginaryNumber(a + n.a, b + n.b);
	}

	public ImaginaryNumber times(ImaginaryNumber n) {
		return new ImaginaryNumber(a + n.a, b + n.b);
	}

	@Override
	public String toString() {
		return a + " + " + b + "i";
	}

}
