package bran.tree.compositions.sets;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.structure.Leaf;

import java.util.function.Function;

public class ConditionalSet implements Set<NumberLiteral>, Leaf {

	private final Function<NumberLiteral, Boolean> conditionalExpression;

	public ConditionalSet(Function<NumberLiteral, Boolean> conditionalExpression) {
		super();
		this.conditionalExpression = conditionalExpression;
	}

	@Override
	public boolean subsetImpl(Set<NumberLiteral> s) {
		return false;
	}

	@Override
	public boolean properSubsetImpl(Set<NumberLiteral> s) {
		return false;
	}

	@Override
	public boolean equivalentImpl(Set<NumberLiteral> other) {
		return false;
	}

	@Override
	public boolean containsImpl(NumberLiteral e) {
		return conditionalExpression.apply(e);
	}

	@Override
	public Set<NumberLiteral> complementImpl() {
		return null;
	}

	@Override
	public Set<NumberLiteral> intersectionImpl(Set<NumberLiteral> s) {
		return null;
	}

	@Override
	public Set<NumberLiteral> unionImpl(Set<NumberLiteral> s) {
		return null;
	}

	@Override
	public Set<NumberLiteral> symmetricDifferenceImpl(Set<NumberLiteral> s) {
		return null;
	}

}
