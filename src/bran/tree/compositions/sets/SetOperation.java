package bran.tree.compositions.sets;

import bran.tree.compositions.sets.operators.SetOperator;
import bran.tree.structure.Fork;

public class SetOperation<E> implements Set<E>, Fork<Set, Set, SetOperator, Set> { // TODO generics

	private final Set<E> left;
	private final SetOperator operator;
	private final Set<E> right;

	public SetOperation(Set<E> left, SetOperator operator, Set<E> right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	//	public ArrayList<AbstractSet> getElements() {
	//		// TODO
	//		return null;
	//	}

	@Override
	public Set<E> getLeft() {
		return left;
	}

	@Override
	public SetOperator getOperator() {
		return operator;
	}

	@Override
	public Set<E> getRight() {
		return right;
	}

	@Override
	public boolean subsetImpl(Set<E> s) {
		return false;
	}

	@Override
	public boolean properSubsetImpl(Set<E> s) {
		return false;
	}

	@Override
	public boolean equivalentImpl(Set<E> other) {
		return false;
	}

	@Override
	public boolean containsImpl(E e) {
		return false;
	}

	@Override
	public Set<E> complementImpl() {
		return null;
	}

	@Override
	public Set<E> intersectionImpl(Set<E> s) {
		return null;
	}

	@Override
	public Set<E> unionImpl(Set<E> s) {
		return null;
	}

	@Override
	public Set<E> symmetricDifferenceImpl(Set<E> s) {
		return null;
	}

}
