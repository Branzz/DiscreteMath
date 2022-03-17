package bran.tree.compositions.sets;

import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.structure.Fork;

public class OperationSet extends Set implements Fork<Set, LogicalOperator, Set> {

	private final Set left;
	private final LogicalOperator operator;
	private final Set right;

	public OperationSet(Set left, LogicalOperator operator, Set right) {
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
	public LogicalOperator getOperator() {
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
