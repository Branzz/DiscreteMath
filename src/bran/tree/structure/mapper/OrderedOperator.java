package bran.tree.structure.mapper;

public interface OrderedOperator extends Mapper {

	AssociativityPrecedenceLevel level();

	default int precedence() {
		return level().precedence();
	}
	default Associativity associativity() {
		return level().associativity();
	}

	// int maxOrder();

	// int minOrder();

}
