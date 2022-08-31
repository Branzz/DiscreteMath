package bran.tree.group;

import bran.tree.Holder;
import bran.tree.Var;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;

import static bran.tree.compositions.statements.Statement.forAll;
import static bran.tree.compositions.statements.Statement.thereExists;

/**
 * closure
 * associativity
 * neutral/identity
 * inverse
 */
public interface Group<E extends Element<E>, I extends E, O extends Operation<E, I>, S extends Set<E>> {

	I identity();
	S domain();
	Set<O> operations();

	default boolean identityProver(O o, E e) {
		return o.operate(e, identity()).equals(e)
			   && o.operate(identity(), e).equals(e);
	}

	default Statement isGroup() {
		return
		// 	forAll((Holder<E>) Var.of("a")).in(domain()).thatVar(a ->
		// 			forAll((Holder<E>) Var.of("b")).in(domain()).thatVar(b ->
		// 					forAll((Holder<O>) Var.of("o")).in(operations()).thatVar(o ->
		// 							thereExists((Holder<E>) Var.of("c")).in(domain()).thatVar(c ->
		// 									VariableStatement.of(c.get().equivalent(o.get().operate(a.get(), b.get())))
		// ).unProven()).unProven()).unProven()).unProven()
		// .and(
		// 	forAll((Holder<E>) Var.of("a")).in(domain()).that(a ->
		// 			VariableStatement.of(a.get())
		// 	).unProven()
		// )
				null;
	}

}
