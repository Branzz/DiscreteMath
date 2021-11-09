package bran.tree;

import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberFactors;
import bran.sets.numbers.godel.GodelBuilder;

import java.util.Stack;

public interface Equivalable<T> {

	boolean equivalentTo(T other);

	void appendGodelNumbers(final GodelBuilder variables);

	default GodelNumberFactors godelNumber() {
		final GodelBuilder godelBuilder = new GodelBuilder();
		appendGodelNumbers(godelBuilder);
		return new GodelNumberFactors(godelBuilder.getNumbers().toArray(GodelNumber[]::new));
	}

}
