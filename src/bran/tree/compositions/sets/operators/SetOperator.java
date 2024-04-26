package bran.tree.compositions.sets.operators;

import bran.tree.compositions.sets.Set;
import bran.tree.structure.UnifiedOperable;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.ForkOperator;

public enum SetOperator implements ForkOperator<Set, Set, Set> {
	UNION(Set::union, "\u222A"),
	INTERSECTION(Set::intersection, "\u2229"),
//	DIFFERENCE(Set::difference, ""),
	// TODO CARTESIAN_PRODUCT
	SYMMETRIC_DIFFERENCE(Set::symmetricDifference, "\u2206", "\u2295"); // TODO SYMBOLS


	private final UnifiedOperable<Set, SetOperator> operator;
	private final String[] symbols;

	SetOperator(UnifiedOperable<Set, SetOperator> operator, String... symbols) {
		this.operator = operator;
		this.symbols = symbols;
	}

	@Override
	public UnifiedOperable<Set, SetOperator> operator() {
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
