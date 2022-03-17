package bran.tree.compositions.statements;

import bran.tree.structure.mapper.Associativity;
import bran.tree.structure.mapper.OperatorType;

import static bran.tree.structure.mapper.Associativity.*;

public enum StatementOperatorType implements OperatorType {
	ANDS(5, LEFT_TO_RIGHT), ORS(4, LEFT_TO_RIGHT), XORS(3, LEFT_TO_RIGHT), IMPLY(2, LEFT_TO_RIGHT), REVERSE(1, RIGHT_TO_LEFT);

	private final int precedence; // DON'T REPEAT
	private final Associativity associativity;

	StatementOperatorType(final int precedence, final Associativity associativity) {
		this.precedence = precedence;
		this.associativity = associativity;
	}

	public int precedence() {
		return precedence;
	}

	public Associativity associativity() {
		return associativity;
	}
	// static {Arrays.stream(values()).map(precedence).distinct()}
}
