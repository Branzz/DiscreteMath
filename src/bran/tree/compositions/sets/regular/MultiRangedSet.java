package bran.tree.compositions.sets.regular;

import bran.exceptions.SetException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.special.equivalences.Equivalence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultiRangedSet implements Set<NumberLiteral> {

	// private final SpecialSet baseDomain;
	private final List<RangedSet> ranges;

	// /**
	//  * from pivots[i] to pivots[i+1]
	//  * pivotEquals[i] == it's inclusive at pivots[i]
	//  */
	// public MultiRangedSet(final SpecialSet baseDomain, final NumberLiteral[] pivots, final Boolean[] pivotEquals) {
	// 	this.baseDomain = baseDomain;
	// 	for (int i = 0; i < pivots.length; i += 2) {
	//
	// 	}
	// }
	//
	// public MultiRangedSet(final SpecialSet baseDomain, Statement ranges) {
	// 	this.baseDomain = baseDomain;
	// }

	public MultiRangedSet(List<Equivalence> equivalences) {
		// this.baseDomain = baseDomain;
		List<RangedSet> ranges = new ArrayList<>();
		for (Equivalence equivalence : equivalences) {
			Set<NumberLiteral> toSet = equivalence.toSet();
			if (toSet instanceof RangedSet rangedSet) {
				if (!rangedSet.baseDomain().getType().equals(SpecialSetType.R))
					throw new SetException("multi ranged set is only base real numbers");
				ranges.add(rangedSet);
			// }
			// else if (toSet instanceof VariableStatement varSt) {
			// 	if (varSt.equals(VariableStatement.CONTRADICTION)) {
			// 		this.ranges = Collections.emptyList();
			// 		return;
			// 	} else if (!varSt.equals(VariableStatement.TAUTOLOGY)){
			// 		throw new SetException("Weird ranges");
			// 	}
			} else {
				throw new SetException("Weird ranges");
			}
		}
		ranges.sort(Comparator.comparing(RangedSet::from));
		for (int i = 0; i < ranges.size() - 1; i++) {
			RangedSet current = ranges.get(i);
			RangedSet next = ranges.get(i + 1);
			final int tailOverlap = current.to().compareTo(next.from());
			if (tailOverlap > 0 || (tailOverlap == 0 && current.isToInclusive() && next.isFromInclusive())) { // accumulate a furthest to
				ranges.remove(i);
				ranges.remove(i);
				ranges.add(i--, new RangedSet(new SpecialSet(SpecialSetType.R), // something off about the type
											current.isFromInclusive(), current.from(),
											next.isToInclusive(), next.to()));
			}
		}
		this.ranges = ranges;
	}

	public List<RangedSet> getRanges() {
		return ranges;
	}

	@Override
	public Composition simplified() {
		return Set.super.simplified();
	}

	@Override
	public boolean subsetImpl(final Set<NumberLiteral> s) {
		return false;
	}

	@Override
	public boolean properSubsetImpl(final Set<NumberLiteral> s) {
		return false;
	}

	@Override
	public boolean equivalentImpl(Set<NumberLiteral> other) {
		return false;
	}

	@Override
	public boolean containsImpl(final NumberLiteral e) {
		return false;
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
