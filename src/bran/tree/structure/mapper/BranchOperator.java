package bran.tree.structure.mapper;

import java.util.function.Function;

public interface BranchOperator<O, A> extends OrderedOperator {

	/**
	 * implementation option: implement operator() and not operate(L, R)
	 * to automatically call a BiFunction
	 */
	default Function<A, O> operator() {
		throw new RuntimeException("implement operator() OR operate(L, R)");
	}

	default O operate(A arg) {
		return operator().apply(arg);
	}

}
