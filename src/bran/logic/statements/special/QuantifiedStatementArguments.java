package bran.logic.statements.special;

import bran.logic.statements.Statement;

@FunctionalInterface
public interface QuantifiedStatementArguments<E> {
	Statement state(E... args);
}
