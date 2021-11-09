package bran.tree;

import bran.sets.numbers.godel.GodelBuilder;

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

	public abstract void appendGodelNumbers(final GodelBuilder godelBuilder);

}
