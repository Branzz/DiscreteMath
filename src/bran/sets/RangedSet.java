package bran.sets;

import bran.sets.numbers.NumberLiteral;

public class RangedSet implements Set {

	private final SpecialSet baseDomain;
	private final NumberLiteral from;
	private final NumberLiteral to; // inclusive

	public RangedSet(final SpecialSet baseDomain, final NumberLiteral from, final NumberLiteral to) {
		this.baseDomain = baseDomain;
		this.from = from;
		this.to = to;
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
		return false;
	}

	@Override
	public String toString() {
		return "[" + from + ", ..., " + to + ']';
	}

	// @Override
	// public Object clone() {
	// 	return null;
	// }

}
