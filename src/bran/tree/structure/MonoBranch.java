package bran.tree.structure;

import bran.tree.structure.mapper.BranchOperator;

public interface MonoBranch<C extends TreePart, F extends BranchOperator> extends Branch<C, F> {

	C getChild();

	F getOperator();

	default F getMapper() {
		return getOperator();
	}

	default Object getChildren() {
		return getChild();
	}

}
