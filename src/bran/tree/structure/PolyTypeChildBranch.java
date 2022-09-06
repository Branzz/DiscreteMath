package bran.tree.structure;

import bran.tree.structure.mapper.Mapper;

import java.util.List;

public interface PolyTypeChildBranch<F extends Mapper> extends TreePart {

	List<?> getChildren();

	F getMapper();

}
