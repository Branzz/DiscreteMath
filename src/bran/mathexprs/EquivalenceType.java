package bran.mathexprs;

public interface EquivalenceType {

	EquivalenceType opposite();

	boolean evaluate(Comparable left, Comparable right);

	boolean lesser();

	boolean greater();

	boolean equal();

	/**
	 * for when two sides are flipped, such as when by multiplying both sides by negative
	 */
	EquivalenceType flipped();

	@FunctionalInterface
	interface Comparison {
		boolean evaluate(Comparable left, Comparable right);
	}

}
