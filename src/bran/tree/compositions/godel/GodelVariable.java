package bran.tree.compositions.godel;

import bran.tree.compositions.expressions.values.numbers.PublicPrimes;
public class GodelVariable implements GodelNumber {

	private final int ordinal;
	private final boolean isVariableType;
	private final int number;

	public GodelVariable(final int ordinal, final boolean isVariableType) {
		this.ordinal = ordinal;
		this.isVariableType = isVariableType;
		this.number = PublicPrimes.getInstance().get((ordinal * 2 + (isVariableType ? 1 : 0)) + 6).intValue();
	}

	public static GodelNumber variableOf(final int factors) {
		int ordinal = PublicPrimes.getInstance().indexOfRange(factors, 6) - 6;
		if (ordinal < 0)
			return GodelNumberSymbols.SYNTAX_ERROR;
		return new GodelVariable(ordinal / 2, ordinal % 2 == 1);
	}

	@Override
	public int number() {
		return number;
	}

	@Override
	public String toString() {
		return (char) (ordinal + (isVariableType ? 'a' : 'A'))
			   + (ordinal > 25 ? String.valueOf((ordinal + 1) / 26) : ""); // a, a1, a2...
	}

}
