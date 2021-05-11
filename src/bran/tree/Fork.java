package bran.tree;

public interface Fork <L extends TreePart, F extends ForkOperator, R extends TreePart> extends TreePart {

	L getLeft();

	F getOperator();

	R getRight();

}
