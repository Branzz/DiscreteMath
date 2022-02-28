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

	private static final Composition emptyComposition = new Composition() {
		@Override public String toFullString() { return "()"; }
		@Override public String toString() { return "()"; }
		@Override public boolean equals(final Object c) { return this == c; }
		@Override public Composition simplified() { return this; }
		@Override public int compareTo(final Composition statement) { return 0; }
		@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { };
	};

	public static Composition empty() {
		return emptyComposition;
	}

}
