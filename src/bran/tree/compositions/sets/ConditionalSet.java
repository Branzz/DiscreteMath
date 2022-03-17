package bran.tree.compositions.sets;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.structure.Leaf;

import java.util.function.Function;

public class ConditionalSet extends Set implements Leaf {

	private final Function<NumberLiteral, Boolean> conditionalExpression;

	public ConditionalSet(final Function<NumberLiteral, Boolean> conditionalExpression) {
		super();
		this.conditionalExpression = conditionalExpression;
	}

	@Override
	public boolean isSubsetOf(final Set s) {
		return false;
	}

	@Override
	public boolean isProperSubsetOf(final Set s) {
		return false;
	}

	@Override
	public boolean contains(final Object o) {
		return o instanceof NumberLiteral n && conditionalExpression.apply(n);
	}

	@Override
	public boolean equals(final Object o) { //TODO
		return false;
	}

	@Override
	public String toString() {
		return null;
	}

}
