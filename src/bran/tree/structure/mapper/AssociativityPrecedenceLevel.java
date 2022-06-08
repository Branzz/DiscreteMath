package bran.tree.structure.mapper;

import static bran.tree.structure.mapper.Associativity.LEFT_TO_RIGHT;
import static bran.tree.structure.mapper.Associativity.RIGHT_TO_LEFT;

public record AssociativityPrecedenceLevel(int precedence, Associativity associativity) {

	public static final int MAX = 10;
	public static final int MIN = 1;

	private static final AssociativityPrecedenceLevel[] LEVELS = new AssociativityPrecedenceLevel[MAX - MIN + 1];

	static {
		Associativity[] associativities = new Associativity[] {
				RIGHT_TO_LEFT, RIGHT_TO_LEFT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT,
				LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, LEFT_TO_RIGHT, RIGHT_TO_LEFT,
		};
		for (int i = 0; i < associativities.length; i++) {
			LEVELS[i] = new AssociativityPrecedenceLevel(i + MIN, associativities[i]);
		}
	}

	public static AssociativityPrecedenceLevel of(int level) {
		return LEVELS[level - MIN];
	}

}
