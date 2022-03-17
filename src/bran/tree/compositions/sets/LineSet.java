package bran.tree.compositions.sets;

import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.sets.regular.FiniteSet;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.structure.MonoBranch;

public class LineSet extends Set implements MonoBranch<Set, LineOperator> {

	private LineOperator lineOperator;
	private Set child;

	private SpecialSet s;

	public LineSet(LineOperator lineOperator, Set child) {
		this.child = child;
		this.lineOperator = lineOperator;
		if (child instanceof FiniteSet)
			; // Behave like all numbers - those.
		// else if (child instanceof SpecialSet) // TODO temp commment
		// 	s = new SpecialSet(child.complement()); //do boolean calculation  // TODO temp commment
	}

	@Override
	public Set getChild() {
		return child;
	}

	@Override
	public LineOperator getOperator() {
		return lineOperator;
	}

	// @Override
	// public Branch<AbstractSet, LineOperator> create(final AbstractSet abstractSet, final LineOperator lineOperator) {
	// 	return new LineSet(abstractSet, lineOperator);
	// }

	// @Override
	// public Object clone() {
	// 	return new LineSet(lineOperator, child);
	// }

	@Override
	public boolean isSubsetOf(Set s) {
        // for (Object e : this)  // TODO temp commment
        //     if (!contains(e))  // TODO temp commment
        //         return false;  // TODO temp commment
        return true;

//		return child.isSubsetOf(s);//TODO
	}

	@Override
	public boolean isProperSubsetOf(Set s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o0) {
		return !child.contains(o0);
	}

}
