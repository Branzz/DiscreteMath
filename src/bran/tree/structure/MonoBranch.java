package bran.tree.structure;

import bran.tree.structure.mapper.BranchOperator;

import java.util.List;

public interface MonoBranch<C extends TreePart, F extends BranchOperator> extends MonoTypeChildBranch<F, C> {

	C getChild();

	F getOperator();

	default F getMapper() {
		return getOperator();
	}

	@Override
	default List<? super C> getChildren() {
		return List.of(getChild());
	}

}
