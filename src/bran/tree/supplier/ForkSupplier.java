package bran.tree.supplier;

import bran.tree.structure.Fork;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.ForkOperator;
@FunctionalInterface

public interface ForkSupplier <L extends TreePart, F extends ForkOperator, R extends TreePart> {
	Fork<L, F, R> get(L left, F forkFunction, R right);
}
