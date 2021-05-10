package bran.logic.tree;

import java.util.List;

public interface MultiLeaf <T extends TreePart, F extends Operator> {

	T[] getChildren();

	F getFunction();

}
