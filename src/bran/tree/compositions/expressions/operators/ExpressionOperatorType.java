package bran.tree.compositions.expressions.operators;

import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.OperatorType;

import static bran.tree.structure.mapper.Associativity.*;

public enum ExpressionOperatorType implements OperatorType {
	E(13),
	MD(12),
	AS(11);

	AssociativityPrecedenceLevel level;

	ExpressionOperatorType(int level) {
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	public AssociativityPrecedenceLevel level() {
		return level;
	}

	public static final int MIN_ORDER = 11;
	public static final int MAX_ORDER = 13;

}
