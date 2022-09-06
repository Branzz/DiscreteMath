package bran.tree.compositions.sets.operators;

import bran.tree.compositions.sets.Set;
import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

import java.util.function.Function;

public enum UnarySetOperator implements BranchOperator<Set, Set> {
	COMPLEMENT(Set::complementImpl)
	;

	private Function<Set, Set> operator;

	UnarySetOperator(Function<Set, Set> operator) {
		this.operator = operator;
	}

	@Override
	public String[] getSymbols() {
		return new String[0];
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return null;
	}

	@Override
	public Function<Set, Set> operator() {
		return operator;
	}

}
