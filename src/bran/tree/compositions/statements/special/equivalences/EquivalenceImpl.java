package bran.tree.compositions.statements.special.equivalences;

import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;

public class EquivalenceImpl extends Equivalence {

	private EquivalenceTypeImpl equivalenceTypeImpl;

	public EquivalenceImpl(Expression left, EquivalenceTypeImpl equivalenceTypeImpl, Expression right) {
		super(left, right);
		this.equivalenceTypeImpl = equivalenceTypeImpl;
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {

	}

	@Override
	public Statement negation() {
		return new EquivalenceImpl(left,equivalenceTypeImpl.opposite(),right); // TODO ?
	}

	@Override
	public EquivalenceType getEquivalenceType() {
		return equivalenceTypeImpl;
	}

	@Override
	public Set toSet() {
		return null;
	}

}
