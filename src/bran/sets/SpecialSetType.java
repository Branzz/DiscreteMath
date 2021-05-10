package bran.sets;

public enum SpecialSetType {
	C(0, "complex number", true, true, true, true), R(1, "real number", true, true, true, true), Q(2, "rational number", true, true, true, true),
	Z(3, "integer", false, true, true, true), W(4, "whole number", false, false, true, true), N(5, "natural number", false, false, false, true),
	P(6, "prime", false, false, false, true), O(7, "empty set", false, false, false, false);

	int level;
	String formalName;
	boolean isContinuous;
	boolean containsNegatives;
	boolean containsZero;
	boolean containsPositives;

	SpecialSetType(int level, String formalName, boolean isContinuous, boolean containsNegatives, boolean containsZero, boolean containsPositives) {
		this.level = level;
		this.formalName = formalName;
		this.isContinuous = isContinuous;
		this.containsNegatives = containsNegatives;
		this.containsZero = containsZero;
		this.containsPositives = containsPositives;
	}

	public String toString() {
		return name();
	}

}
