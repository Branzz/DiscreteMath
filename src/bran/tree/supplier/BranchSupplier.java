package bran.tree.supplier;

import bran.tree.structure.MonoBranch;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.BranchOperator;

@FunctionalInterface
public interface BranchSupplier<C extends TreePart, F extends BranchOperator> {
	MonoBranch<C, F> get(C treePart, F branchFunction);
}
