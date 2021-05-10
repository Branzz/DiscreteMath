package bran.mathexprs.treeparts.operators;

import bran.logic.tree.Associativity;

import static bran.logic.tree.Associativity.*;

public enum OperatorType {
	E(7, RIGHT_TO_LEFT), MD(5, LEFT_TO_RIGHT), AS(3, LEFT_TO_RIGHT);

	private final int precedence; // DON'T REPEAT
	private final Associativity associativity;

	OperatorType(final int precedence, final Associativity associativity) {
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
