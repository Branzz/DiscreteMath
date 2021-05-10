package bran.logic.tree;

public interface Branch <C extends TreePart, F extends BranchOperator> extends TreePart {

	C getChild();

	F getOperator();

}
