package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

import java.util.List;

public interface PolyTypeFork<O, L, F extends ForkOperator<O, L, R>, R> extends PolyTypeChildBranch<F> {

	L getLeft();

	F getOperator();

	R getRight();

	default O operate() {
		return getOperator().operate(getLeft(), getRight());
	}

	@Override
	default F getMapper() {
		return getOperator();
	}

	@Override
	default List<?> getChildren() {
		return List.of(getLeft(), getRight());
	}

}
