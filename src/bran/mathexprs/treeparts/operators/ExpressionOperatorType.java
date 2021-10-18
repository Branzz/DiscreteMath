package bran.mathexprs.treeparts.operators;

import bran.tree.Associativity;
import bran.tree.OperatorType;

import static bran.tree.Associativity.*;

public enum ExpressionOperatorType implements OperatorType {
	E(13, RIGHT_TO_LEFT), MD(12, LEFT_TO_RIGHT), AS(11, LEFT_TO_RIGHT);

	private final int precedence; // DON'T REPEAT
	private final Associativity associativity;

	ExpressionOperatorType(final int precedence, final Associativity associativity) {
		this.precedence = precedence;
		this.associativity = associativity;
	}

	public int precedence() {
		return precedence;
	}

	public Associativity associativity() {
		return associativity;
	}

	public static final int MIN_ORDER = 0;
	public static final int MAX_ORDER = 9;

}
