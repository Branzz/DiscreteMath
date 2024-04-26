package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

import java.util.List;

public interface MonoTypeFork<O, T, F extends ForkOperator<O, T, T>, S extends MonoTypeFork<O, T, F, S>>
		extends MonoTypeChildBranch<F, T> {

	T getLeft();

	F getOperator();

	T getRight();

	default O operate() {
		return getOperator().operate(getLeft(), getRight());
	}

	@Override
	default F getMapper() {
		return getOperator();
	}

	@Override
	default List<? super T> getChildren() {
		return List.of(getLeft(), getRight());
	}

	default TriFunction<T, F, T, S> getter() { return (a, b, c) -> null; }

	@FunctionalInterface
	interface TriFunction<A, B, C, O> {
		O apply(A a, B b, C c);
	}
}
