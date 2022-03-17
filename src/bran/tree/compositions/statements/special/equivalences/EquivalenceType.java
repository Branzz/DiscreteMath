package bran.tree.compositions.statements.special.equivalences;

import bran.tree.structure.Branch;
import bran.tree.structure.mapper.Mapper;

import java.util.function.BiFunction;

public interface EquivalenceType extends Mapper {

	EquivalenceType opposite();

	<R, L extends Comparable<R>> boolean evaluate(final Comparable<L> left, final Comparable<R> right);

	boolean lesser();

	boolean greater();

	boolean equal();

	/**
	 * for when two sides are flipped, such as when by multiplying both sides by negative
	 */
	EquivalenceType flipped();

	@FunctionalInterface
	interface Comparison extends BiFunction<Comparable, Comparable, Boolean> {
		@Override
		Boolean apply(Comparable left, Comparable right);
	}

}
