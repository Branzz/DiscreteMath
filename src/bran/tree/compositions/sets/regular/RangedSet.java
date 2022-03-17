package bran.tree.compositions.sets.regular;

import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.Set;
import bran.tree.structure.Leaf;

public class RangedSet extends Set implements Leaf {

	// if it exists, it exists along this domain
	private final SpecialSet baseDomain;
	private final boolean fromInclusive;
	private final NumberLiteral from;
	private final boolean toInclusive;
	private final NumberLiteral to; // inclusive

	public RangedSet(final SpecialSet baseDomain, final boolean fromInclusive, final NumberLiteral from, final boolean toInclusive, final NumberLiteral to) {
		this.baseDomain = baseDomain;
		this.fromInclusive = fromInclusive;
		this.from = from;
		this.toInclusive = toInclusive;
		this.to = to;
	}

	public SpecialSet baseDomain() {
		return baseDomain;
	}

	public NumberLiteral from() {
		return from;
	}

	public boolean isFromInclusive() {
		return fromInclusive;
	}

	public NumberLiteral to() {
		return to;
	}

	public boolean isToInclusive() {
		return toInclusive;
	}

	@Override
	public boolean isSubsetOf(final Set other) {
		if (other instanceof RangedSet oRanged) {
			return from.doubleValue() >= oRanged.from.doubleValue()
					&& to.doubleValue() <= oRanged.from.doubleValue()
					&& baseDomain.isSubsetOf(oRanged.baseDomain);
		} else if (other instanceof SpecialSet oSpec) {

		}
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
		return (fromInclusive ? "[" : "(") + from + ", " + to + (toInclusive ? ']' : ')');
	}

	// @Override
	// public Object clone() {
	// 	return null;
	// }

}
