package bran.tree.compositions.statements;

import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.OperatorType;

import static bran.tree.structure.mapper.Associativity.*;

public enum StatementOperatorType implements OperatorType {
	ANDS(5),
	ORS(4),
	XORS(3),
	IMPLY(2),
	REVERSE(1);

	AssociativityPrecedenceLevel level;

	StatementOperatorType(int level) {
		this.level = AssociativityPrecedenceLevel.of(level);
	}

	public AssociativityPrecedenceLevel level() {
		return level;
	}

}
