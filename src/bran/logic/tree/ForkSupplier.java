package bran.logic.tree;

@FunctionalInterface
public interface ForkSupplier <L extends TreePart, F extends ForkOperator, R extends TreePart> {
	Fork<L, F, R> get(L left, F forkFunction, R right);
}