package bran.tree.structure.mapper;

public interface OrderedOperator extends Mapper {

	int getOrder();

	int maxOrder();
	int minOrder();

	Associativity getDirection();

}
