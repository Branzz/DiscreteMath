package bran.tree;

public interface MultiLeaf <T extends TreePart, F extends Operator> {

	T[] getChildren();

	F getFunction();

}
