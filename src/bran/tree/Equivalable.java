package bran.tree;

import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberFactors;
import bran.sets.numbers.godel.GodelVariableMap;

import java.util.Stack;

public interface Equivalable<T> {

	boolean equivalentTo(T other);

	void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables);

	default GodelNumberFactors godelNumber() {
		Stack<GodelNumber> godelNumbers = new Stack<>();
		appendGodelNumbers(godelNumbers, new GodelVariableMap());
		return new GodelNumberFactors(godelNumbers.toArray(GodelNumber[]::new));
	}

}
