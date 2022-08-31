package bran.tree.structure.mapper;

import java.util.function.BiFunction;

public interface ForkOperator<O, L, R> extends OrderedOperator {

	/**
	 * implementation option: implement operator() and not operate(L, R)
	 * to automatically call a BiFunction
	 */
	default BiFunction<L, R, O> operator() {
		return null;
	}

	default O operate(L l, R r) {
		return operator().apply(l, r);
	}

}
