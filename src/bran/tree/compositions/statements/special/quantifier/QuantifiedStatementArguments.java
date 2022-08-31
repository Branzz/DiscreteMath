package bran.tree.compositions.statements.special.quantifier;

import bran.tree.compositions.statements.Statement;

@FunctionalInterface
public interface QuantifiedStatementArguments<E> { // Function<E[], Statement>
	Statement state(E... args);
	// default Statement state(E args) {
	// 	return state(args);
	// }
	// static <E> QuantifiedStatementArguments<E> oneArg(E arg) {
	// 	return
	// }
}
