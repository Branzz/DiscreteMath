package bran.tree;

@FunctionalInterface
public interface BranchSupplier<C extends TreePart, F extends BranchOperator> {
	Branch<C, F> get(C treePart, F branchFunction);
}
