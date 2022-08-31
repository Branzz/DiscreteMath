package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.structure.mapper.BranchOperator;

public interface AbstractFunction<E> extends BranchOperator {

	// E function(E[] a);
	default E function(E[] a) {
		checkArguments(a.length);
		return null;
	}

	int getArgAmount();

	void checkArguments(int length) throws IllegalArgumentAmountException;

	default void checkArguments(int input, int actual) throws IllegalArgumentAmountException {
		if (input != actual)
			throw new IllegalArgumentAmountException(input, actual);
	}

}
