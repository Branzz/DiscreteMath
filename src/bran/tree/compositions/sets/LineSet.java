package bran.tree.compositions.sets;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.operators.LineSetOperator;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.structure.MonoBranch;

public class LineSet<E> implements Set<E>, MonoBranch<Set<E>, LineSetOperator> {

	private final LineSetOperator lineOperator;
	private final Set<E> child;

	private SpecialSet s;

	public LineSet(LineSetOperator lineOperator, Set<E> child) {
		this.child = child;
		this.lineOperator = lineOperator;
		// if (child instanceof FiniteSet)
		// 	; // Behave like all numbers - those.
		// else if (child instanceof SpecialSet) // TODO temp commment
		// 	s = new SpecialSet(child.complement()); //do boolean calculation  // TODO temp commment
	}

	@Override
	public Set<E> getChild() {
		return child;
	}

	@Override
	public LineSetOperator getOperator() {
		return lineOperator;
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

	// @Override
	// public Branch<AbstractSet, LineOperator> create(final AbstractSet abstractSet, final LineOperator lineOperator) {
	// 	return new LineSet(abstractSet, lineOperator);
	// }

	// @Override
	// public Object clone() {
	// 	return new LineSet(lineOperator, child);
	// }

	// @Override
	// public Statement contains(Object o0) {
	// 	return !child.contains(o0);
	// }

}
