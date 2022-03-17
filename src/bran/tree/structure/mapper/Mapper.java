package bran.tree.structure.mapper;

import bran.tree.structure.TreePart;

public interface Mapper extends TreePart {

	String[] getSymbols();

	default Mapper inverse() {
		return null;
	}

	// R of(A arg);

}
