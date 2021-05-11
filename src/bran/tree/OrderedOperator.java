package bran.tree;

public interface OrderedOperator extends Operator {

	int getOrder();

	int maxOrder();
	int minOrder();

	Associativity getDirection();

}
