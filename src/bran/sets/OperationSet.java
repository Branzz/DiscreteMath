package bran.sets;

import bran.logic.statements.operators.Operator;
import bran.tree.Fork;

public class OperationSet implements Set, Fork<Set, Operator, Set> {

	private final Set left;
	private final Operator operator;
	private final Set right;


	public OperationSet(Set left, Operator operator, Set right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

//	public ArrayList<AbstractSet> getElements() {
//		// TODO
//		return null;
//	}

	@Override
	public Set getLeft() {
		return left;
	}

	@Override
	public Operator getOperator() {
		return operator;
	}

	@Override
	public Set getRight() {
		return right;
	}

	// @Override
	// public OperationSet create(final AbstractSet left, final Operator function, final AbstractSet right) {
	// 	return new OperationSet(left, function, right);
	// }

	@Override
	public boolean isSubsetOf(Set s) {
		switch(operator) {
		case AND:
			;
		case OR:
			return left.isSubsetOf(s) && right.isSubsetOf(s);
		case XOR:
			;
		default:
			return false;
		}
	}

	@Override
	public boolean isProperSubsetOf(Set s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public Object clone() {
	// 	return new OperationSet((Set) left.clone(), operator, (Set) right.clone());
	// }

}
