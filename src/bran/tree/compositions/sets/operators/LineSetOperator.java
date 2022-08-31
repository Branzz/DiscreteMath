package bran.tree.compositions.sets.operators;

import bran.tree.structure.mapper.AssociativityPrecedenceLevel;
import bran.tree.structure.mapper.BranchOperator;

public enum LineSetOperator implements BranchOperator {
	COMPLEMENT
	;

	@Override
	public String[] getSymbols() {
		return new String[0];
	}

	@Override
	public AssociativityPrecedenceLevel level() {
		return null;
	}
}
