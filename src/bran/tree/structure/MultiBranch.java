package bran.tree.structure;

import bran.tree.structure.mapper.Mapper;

public interface MultiBranch<T extends TreePart, F extends Mapper> extends Branch<T, F> {

	T[] getChildren();

	F getFunction();

	default F getMapper() {
		return getFunction();
	}

}
