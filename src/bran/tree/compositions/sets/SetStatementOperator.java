package bran.tree.compositions.sets;

import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;

import java.util.function.BiFunction;

public enum SetStatementOperator implements ForkOperator<Boolean, Set, Set> {
	SUBSET(Set::subsetImpl, "\u2286"),
	PROPER_SUBSET(Set::properSubsetImpl, "\u2282"),
	BIJECTION(Set::equivalentImpl, "\u2194"),
	;

	SetStatementOperator(BiFunction<Set, Set, Boolean> operator, String... symbols) {
		this.operator = operator;
		this.symbols = symbols;
	}

	private BiFunction<Set, Set, Boolean> operator;
	private String[] symbols;

	@Override
	public BiFunction<Set, Set, Boolean> operator() {
		return operator;
	}

	@Override
	public String[] getSymbols() {
		return symbols;
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return null;
	}

}
