package bran.tree.supplier;

import bran.tree.structure.Fork;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.ForkOperator;

@FunctionalInterface
public interface ForkSupplier <O, L extends TreePart, F extends ForkOperator<O, L, R>, R extends TreePart> {
	Fork<O, L, F, R> get(L left, F forkFunction, R right);
}
