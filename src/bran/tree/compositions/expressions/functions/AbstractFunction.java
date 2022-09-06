package bran.tree.compositions.expressions.functions;

import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.structure.mapper.BranchOperator;

import java.util.function.Function;

public interface AbstractFunction<E> extends BranchOperator<E, E[]> {

	// E function(E[] a);
	default E function(E[] a) { // TODO weird double method
		checkArguments(a.length);
		return null;
	}

	@Override
	default E operate(E[] arg) {
		return function(arg);
	}

	int getArgAmount();

	void checkArguments(int length) throws IllegalArgumentAmountException;

	default void checkArguments(int input, int actual) throws IllegalArgumentAmountException {
		if (input != actual)
			throw new IllegalArgumentAmountException(input, actual);
	}

}
