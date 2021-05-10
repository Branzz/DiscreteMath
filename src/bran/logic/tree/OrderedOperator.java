package bran.logic.tree;

public interface OrderedOperator extends Operator {

	int getOrder();

	int maxOrder();
	int minOrder();

	Associativity getDirection();

}
