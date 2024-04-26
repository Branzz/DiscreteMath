package bran.tree.supplier;

import bran.tree.structure.MonoTypeFork;
import bran.tree.structure.TreePart;
import bran.tree.structure.mapper.ForkOperator;

@FunctionalInterface
public interface ForkSupplier <O, T extends TreePart, F extends ForkOperator<O, T, T>> {
	MonoTypeFork<O, T, F, ?> get(T left, F forkFunction, T right);
}
