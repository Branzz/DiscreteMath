package bran.tree.compositions.statements.special.equivalences;

import bran.tree.structure.Branch;
import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.ForkOperator;
import bran.tree.structure.mapper.Mapper;
import bran.tree.structure.mapper.OrderedOperator;

import java.util.function.BiFunction;

public interface EquivalenceType extends ForkOperator {

	EquivalenceType opposite();

	 // boolean evaluate(final Comparable<T> left, final Comparable<T> right);
	<R, L extends Comparable<R>> boolean evaluate(Comparable<L> left, Comparable<R> right);

	boolean lesser();

	boolean greater();

	boolean equal();

	/**
	 * for when two sides are flipped, such as when by multiplying both sides by negative
	 */
	EquivalenceType flipped();

	@FunctionalInterface
	interface Comparison <R, L extends Comparable<R>> extends BiFunction<Comparable<L>, Comparable<R>, Boolean> {
		@Override
		Boolean apply(Comparable left, Comparable right);
	}

	@Override
	default int getOrder() {
		return 0;
	}

	@Override
	default int maxOrder() {
		return 0;
	}

	@Override
	default int minOrder() {
		return 0;
	}

	@Override
	default Associativity getDirection() {
		return Associativity.RIGHT_TO_LEFT;
	}

}
