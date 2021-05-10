package bran.sets;

import bran.sets.numbers.NumberLiteral;

/**
 * An infinitely sized set of {@link NumberLiteral} Objects or 0
 */
public class SpecialSet implements Set {

	// P N Z Q R C H
	// R Q Z W N P 0|C H

//	private final static ArrayList<SpecialSetType> orderedTypes;
//	static {
//		orderedTypes = new ArrayList<SpecialSetType>(8);
//		for (SpecialSetType s : SpecialSetType.values())
//			orderedTypes.add(0, s);
//	}

	private final boolean limitedPositivity;
	private final boolean nonPositivity;
	private final boolean positivity;

	private final boolean containsNegatives;
	private final boolean containsZero;
	private final boolean containsPositives;

	private final SpecialSetType type;

	public SpecialSet(SpecialSetType type) {
		limitedPositivity = false;
		nonPositivity = false;
		positivity = false;
		this.type = type;
		containsNegatives = type.containsNegatives;
		containsZero = type.containsZero;
		containsPositives = type.containsPositives;
	}

	/*
	 * non-	positivity
	 * 0	0	negative - not including 0
	 * 0	1	positive - not including 0
	 * 1	0	positive - including 0
	 * 1	1	negative - including 0
	 */
	public SpecialSet(SpecialSetType type, boolean nonPositivity, boolean positivity) {
		limitedPositivity = true;
		this.nonPositivity = nonPositivity;
		this.positivity = positivity;
		this.type = type;
		containsNegatives = type.containsNegatives && nonPositivity == positivity;
		containsZero = type.containsZero && nonPositivity;
		containsPositives = type.containsPositives && nonPositivity ^ positivity;
	}

	public SpecialSetType getType() {
		return type;
	}

	private boolean getNonPositivity() {
		return nonPositivity;
	}

	private boolean getPositivity() {
		return positivity;
	}

	private boolean getLimitedPositivity() {
		return limitedPositivity;
	}

	private boolean getContainsNegatives() {
		return containsNegatives;
	}

	private boolean getContainsZeros() {
		return containsZero;
	}

	private boolean getContainsPositives() {
		return containsPositives;
	}

	private int getLevel() {
		return getLevel(type);
	}

	private int getLevel(SpecialSetType type) {
		return type.level;
//		return orderedTypes.indexOf(type);
	}

	//limited positivity X s type
	@Override
	public boolean isSubsetOf(Set s) {
		return limitedPositivity
			? s instanceof SpecialSet
				? containsNegatives || !((SpecialSet) s).getContainsNegatives()
					&& containsZero || !((SpecialSet) s).getContainsZeros()
					&& containsPositives || !((SpecialSet) s).getContainsPositives()
						&& !(getLevel() >= getLevel(SpecialSetType.Q))
						|| getLevel() <= getLevel(((SpecialSet) s).getType())
				: s instanceof FiniteSet && isZero() && s.contains(new NumberLiteral(0))
			: s instanceof SpecialSet && getLevel(type) <= getLevel(((SpecialSet) s).getType());
//		if (limitedPositivity)
//			if (s instanceof SpecialSet)
//				return ((containsNegatives || !((SpecialSet) s).getContainsNegatives())
//						&& (containsZero || !((SpecialSet) s).getContainsZeros())
//						&& (containsPositives || !((SpecialSet) s).getContainsPositives()));
//			else if (s instanceof FiniteSet)
//				return isZero() && ((FiniteSet<?>) s).contains(new Number(0));
//			else
//				return false;
//		else
//			if (s instanceof SpecialSet)
//				return orderedTypes.indexOf(type) <= orderedTypes.indexOf(((SpecialSet) s).getType());
//			else if (s instanceof FiniteSet)
//				return false; //	return isZero() && ((FiniteSet<?>) s).contains(new Number(0));
//			else
//				return false;
	}

	@Override
	public boolean isProperSubsetOf(Set s) {
		return isSubsetOf(s) && !s.isSubsetOf(this);
//		if (!limitedPositivity)
//			if (s instanceof SpecialSet)
//				return orderedTypes.indexOf(type) < orderedTypes.indexOf(((SpecialSet) s).getType());
//			else {
//				return false;
//			}
//		else {
//			if (s instanceof SpecialSet)
//			return (((containsNegatives || !((SpecialSet) s).getContainsNegatives())
//					&& (containsZero || !((SpecialSet) s).getContainsZeros())
//					&& (containsPositives || !((SpecialSet) s).getContainsPositives()))
//						&& !((containsNegatives == ((SpecialSet) s).getContainsNegatives()) //they aren't all equal
//							&& (containsZero == ((SpecialSet) s).getContainsZeros())
//							&& (containsPositives == ((SpecialSet) s).getContainsPositives())));
//
//		}
//		return false;
	}

	private boolean isZero() {
		return !containsNegatives && containsZero && !containsPositives;
	}

	public int size() {
		return isZero() ? 1 : (int) Float.POSITIVE_INFINITY; // TODO awkward cast?
	}

//	private SpecialSet(SpecialSetType type, boolean containsNegatives, boolean containsZero, boolean containsPositives) {
//		this.type = type;
//		limitedPositivity = false;
//		nonPositivity = false;
//		positivity = false;
//		this.containsNegatives = containsNegatives;
//		this.containsZero = containsZero;
//		this.containsPositives = containsPositives;
//	}

	public Set complement() {
		if (limitedPositivity)
			if (getLevel() >= getLevel(SpecialSetType.Q))
				return new SpecialSet(SpecialSetType.C, !nonPositivity, !positivity);
			else
				return new SpecialSet(SpecialSetType.C, !nonPositivity, !positivity)
						.union(new SpecialSet(SpecialSetType.C, nonPositivity, positivity)
						.symmetricDifference(this));
		else
			if (type.equals(SpecialSetType.Q))
				return new SpecialSet(SpecialSetType.O);
			else if (type.equals(SpecialSetType.O))
				return new SpecialSet(SpecialSetType.C);
			else
				return new SpecialSet(SpecialSetType.C, nonPositivity, positivity)
						.symmetricDifference(this);
		//OperationSet(SpecialSetType.C, nonPositivity, positivity), XOR this.clone())
//			if (getLevel() >= getLevel(SpecialSetType.Q))
//				return new SpecialSet(SpecialSetType.O);
//			else
//				return new SpecialSet(SpecialSetType.C, nonPositivity, positivity)
//						.symmetricDifference(this.clone());
	}

	public boolean contains(Object o) {
		if (!(o instanceof NumberLiteral)) // TODO if imaginary numbers
			return false;
		double value = ((NumberLiteral) o).getValue();
		return ((value % 1 == 0 || type.isContinuous)
				&& (value > 0 && containsPositives || value < 0 && containsNegatives || value == 0 && containsZero))
				&& (!(type.equals(SpecialSetType.P)) || isPrime(((NumberLiteral) o)));
	}

	private boolean isPrime(NumberLiteral number) {
		for (int i = 2; i <= Math.sqrt(number.doubleValue()); i++)
			if (number.doubleValue() / i % 1 != 0)
				return false;
		return true;
	}

	// @Override
	// public Set clone() {
	// 	return limitedPositivity ? new SpecialSet(type, nonPositivity, positivity) : new SpecialSet(type);
	// }

//	@Override
	public int compareTo(Set s) { // TODO use this? or delete
		return s instanceof SpecialSet ? getLevel() - getLevel(((SpecialSet) s).getType()) : 0;
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| (o instanceof SpecialSet
					&& ((SpecialSet) o).getType() == type
					&& ((SpecialSet) o).getLimitedPositivity() == limitedPositivity
					&& ((SpecialSet) o).getNonPositivity() == nonPositivity
					&& ((SpecialSet) o).getPositivity() == positivity)
				|| (o instanceof FiniteSet
					&& ((FiniteSet) o).size() == 1 && ((FiniteSet) o).contains(new NumberLiteral(0))
					&& !containsNegatives && containsZero && !containsPositives);
	}

	@Override
	public String toString() {
		if (!limitedPositivity)
			return type.toString();
		StringBuilder string = new StringBuilder(8);
		string.append(type.toString());
		if (nonPositivity)
			string.append(positivity ? " nonpos" : " nonneg");
		else
			string.append(positivity ? "+" : "-");
		return string.toString();
	}

	public String toFormalString() {
		if (!limitedPositivity)
			return type.formalName;
		StringBuilder string = new StringBuilder(27);
		if (nonPositivity)
			string.append("non");
		string.append(positivity ? "positive " : "negative ");
		string.append(type.formalName);
		return string.toString();
	}

}
