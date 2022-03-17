package bran.tree.structure.mapper;

public interface OperatorType { // extends TreePart?

	int precedence();

	Associativity associativity();

}
