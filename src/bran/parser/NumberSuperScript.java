package bran.parser;

import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.OrderedOperator;

public enum NumberSuperScript implements OrderedOperator { // TODO every super script symbol??
	S0(0, "\u2070"), S1(1, "\u00B9"), S2(2, "\u00B2"), S3(3, "\u00B3"), S4(4, "\u2074"), S5(5, "\u2075"), S6(6, "\u2076"), S7(7, "\u2077"), S8(8, "\u2078"), S9(9, "\u2079");

	private final int value;
	private final String[] symbols;

	NumberSuperScript(int value, String... symbols) {
		this.value = value;
		this.symbols = symbols;
	}

	public int exponentValue() {
		return value;
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return ArithmeticOperator.POW.level();
	}

}
