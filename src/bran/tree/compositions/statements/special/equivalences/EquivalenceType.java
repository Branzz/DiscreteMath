package bran.tree.compositions.statements.special.equivalences;

import bran.tree.compositions.expressions.Expression;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.RelationalOperator;

import java.util.function.BiFunction;

public interface EquivalenceType extends RelationalOperator<Boolean, Expression> {

	EquivalenceType opposite();

	 // boolean evaluate(final Comparable<T> left, final Comparable<T> right);
	boolean evaluate(Expression left, Expression right);

	boolean lesser();

	boolean greater();

	boolean equal();

	/**
	 * for when two sides are flipped, such as when by multiplying both sides by negative
	 */
	EquivalenceType flipped();

	@FunctionalInterface
	interface Comparison<R, L extends Comparable<R>> extends BiFunction<Comparable<L>, Comparable<R>, Boolean> {
		@Override
		Boolean apply(Comparable left, Comparable right);
	}

	@Override
	default AssociativityPrecedenceLevel level() {
		return AssociativityPrecedenceLevel.of(10);
	}

}
