package bran.mathexprs;

public interface EquivalenceType {

	EquivalenceType opposite();

	boolean evaluate(Comparable left, Comparable right);

	@FunctionalInterface
	public interface Comparison {

		boolean evaluate(Comparable left, Comparable right);

	}

}
