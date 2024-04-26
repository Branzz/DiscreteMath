package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

import java.util.function.BiFunction;

public interface Operable<T, L, R, F extends ForkOperator<T, ?, ?>> extends BiFunction<L, R, T> {

	default T operate(L left, R right) {
		return apply(left, right);
	}

}
