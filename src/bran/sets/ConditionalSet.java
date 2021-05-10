// package bran.sets;
//
// import bran.sets.numbers.NumberLiteral;
//
// public class ConditionalSet implements Set {
//
// 	private final ConditionalExpression conditionalExpression;
//
// 	public ConditionalSet(final ConditionalExpression conditionalExpression) {
// 		super();
// 		this.conditionalExpression = conditionalExpression;
// 	}
//
// 	@Override
// 	public boolean isSubsetOf(final Set s) {
// 		return false;
// 	}
//
// 	@Override
// 	public boolean isProperSubsetOf(final Set s) {
// 		return false;
// 	}
//
// 	@Override
// 	public boolean contains(final Object o) {
// 		return o instanceof NumberLiteral n && conditionalExpression.conditioned(n);
// 	}
//
// 	@Override
// 	public Object clone() {
// 		return null;
// 	}
//
// 	@Override
// 	public boolean equals(final Object o) {
// 		return false;
// 	}
//
// 	@Override
// 	public String toString() {
// 		return null;
// 	}
//
// }
