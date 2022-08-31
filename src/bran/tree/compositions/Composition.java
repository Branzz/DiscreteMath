package bran.tree.compositions;

import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumber;
import bran.tree.compositions.godel.GodelNumberFactors;
import bran.tree.structure.TreePart;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public interface Composition extends TreePart { // TODO group (theory)

	String toFullString();

	@Override
	String toString();

	static String parens(String string) {
		return '(' + string + ')';
	}

	// @Override
	// public abstract boolean equals(Object s);

	Composition simplified();

	void replaceAll(final Composition original, final Composition replacement);

	// public abstract <T> Collection<T> getAll(Function<T, Boolean> matcher);

	void appendGodelNumbers(final GodelBuilder godelBuilder);

	default GodelNumberFactors godelNumber() {
		final GodelBuilder godelBuilder = new GodelBuilder();
		appendGodelNumbers(godelBuilder);
		return new GodelNumberFactors(godelBuilder.getNumbers().toArray(GodelNumber[]::new));
	}

	Composition emptyComposition = new Composition() {
		@Override public String toFullString() { return "()"; }
		@Override public String toString() { return "()"; }
		@Override public boolean equals(Object c) { return this == c; }
		@Override public Composition simplified() { return this; }
		@Override public void replaceAll(Composition approaches, Composition approached) { }
		// @Override public <T> Collection<T> getAll(Function<T, Boolean> matcher) { return Collections.emptyList(); }
		@Override public void appendGodelNumbers(GodelBuilder godelBuilder) { }
	};

	static Composition empty() {
		return emptyComposition;
	}

}
