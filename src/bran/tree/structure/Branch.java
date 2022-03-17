package bran.tree.structure;

import bran.tree.structure.mapper.Mapper;

public interface Branch<T extends TreePart, F extends Mapper> extends TreePart {

	Object getChildren();

	F getMapper();

}
