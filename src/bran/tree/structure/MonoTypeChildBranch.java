package bran.tree.structure;

import bran.tree.structure.mapper.Mapper;

import java.util.List;

public interface MonoTypeChildBranch<F extends Mapper, T> extends PolyTypeChildBranch<F> {

	List<? super T> getChildren();

}
