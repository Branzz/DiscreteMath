package bran.tree.structure.mapper;

import bran.parser.matching.Pattern;
import bran.parser.matching.Tokenable;

import java.lang.reflect.Constructor;

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
