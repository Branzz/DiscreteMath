package bran.tree.structure.mapper;

import java.util.function.BiFunction;

public interface ForkOperator<T, L, R> extends OrderedOperator {

	/**
	 * implementation option: implement operator() and not operate(L, R)
	 * to automatically call a BiFunction
	 */
	default BiFunction<L, R, T> operator() {
		throw new RuntimeException("implement operator() OR operate(L, R)");
	}

	default T operate(L l, R r) {
		return operator().apply(l, r);
	}

}
