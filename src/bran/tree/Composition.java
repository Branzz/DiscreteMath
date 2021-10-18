package bran.tree;

import bran.logic.statements.Statement;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.Stack;

public abstract class Composition implements TreePart, Comparable<Composition> {

	public abstract String toFullString();

	@Override
	public abstract String toString();

	public static String parens(String string) {
		return '(' + string + ')';
	}

	@Override
	public abstract boolean equals(Object s);

	public abstract Composition simplified();

	@Override
	public abstract int compareTo(Composition statement);

	public abstract void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables);

}
