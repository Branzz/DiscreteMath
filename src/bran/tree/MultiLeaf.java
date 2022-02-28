package bran.tree;

public interface MultiLeaf <T extends TreePart, F extends Mapper> {

	T[] getChildren();

	F getFunction();

}
