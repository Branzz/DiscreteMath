package bran.tree.structure.mapper;

import static bran.tree.structure.mapper.Associativity.*;

public record AssociativityPrecedenceLevel(int precedence, Associativity associativity) { // TODO to interface

	public static final int MIN = 0;
	public static final int MAX;
	public static final int SIZE;

	private static final AssociativityPrecedenceLevel[] LEVELS;

	static {
		Associativity[] associativities = new Associativity[] {
				NON_ASSOCIATIVE,
				RIGHT_TO_LEFT, RIGHT_TO_LEFT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT,
				LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, RIGHT_TO_LEFT,
				LEFT_TO_RIGHT, NON_ASSOCIATIVE
		};
		MAX = associativities.length - MIN;
		SIZE = MAX - MIN;
		LEVELS = new AssociativityPrecedenceLevel[SIZE + 1];
		for (int i = 0; i < associativities.length; i++) {
			LEVELS[i] = new AssociativityPrecedenceLevel(i + MIN, associativities[i]);
		}
	}

	public static AssociativityPrecedenceLevel of(int level) {
		return LEVELS[level - MIN];
	}

}
