package bran.logic.statements.special;

import bran.logic.statements.Statement;

@FunctionalInterface
public interface UniversalStatementArguments <E> {
	Statement state(E... args);
}
