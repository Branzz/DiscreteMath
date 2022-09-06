package bran.tree.structure;

import bran.tree.structure.mapper.Mapper;

import java.util.List;

public interface MultiBranch<F extends Mapper, T extends TreePart> extends MonoTypeChildBranch<F, T> {

	List<? extends T> getChildren();

	F getFunction();

	default F getMapper() {
		return getFunction();
	}

}
