package bran.tree.structure;

import bran.tree.structure.mapper.ForkOperator;

public interface Fork <L extends TreePart, F extends ForkOperator, R extends TreePart> extends TreePart {

	L getLeft();

	F getOperator();

	R getRight();

}
