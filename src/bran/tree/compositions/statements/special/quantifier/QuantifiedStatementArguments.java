package bran.tree.compositions.statements.special.quantifier;

import bran.tree.compositions.statements.Statement;

@FunctionalInterface
public interface QuantifiedStatementArguments<E> {
	Statement state(E... args);
}
