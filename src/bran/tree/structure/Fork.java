package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

public interface Fork<O, L, F extends ForkOperator<O, L, R>, R> extends TreePart {

	L getLeft();

	F getOperator();

	R getRight();

	default O operate() {
		return getOperator().operate(getLeft(), getRight());
	}

}
