package bran.tree.compositions.sets;

import bran.exceptions.VariableExpressionException;
import bran.tree.compositions.Composition;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.sets.regular.SpecialSetType;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.structure.TreePart;

public abstract class Set extends Composition implements TreePart
//Comparable<AbstractSet>,
 {

	public abstract boolean isSubsetOf(Set s);

	public abstract boolean isProperSubsetOf(Set s);

	public abstract boolean contains(Object o);

	// Object clone();

	// int compareTo(AbstractSet s);

	// abstract boolean equals(Object o);

	// public abstract String toString();

	 @Override
	 public void replaceAll(final Composition original, final Composition replacement) {
		 // do nothing
	 }

	 public OperationSet operation(LogicalOperator o, Set... s) {
		if (s.length < 0)
			throw new VariableExpressionException();
		if (s.length == 0)
			return new OperationSet(this, o, this);
		OperationSet combinedStatements = new OperationSet(this, o, s[0]);
		for (int i = 1; i < s.length; i++)
			combinedStatements = new OperationSet(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	public Set complement() {
		return new LineSet(LineOperator.NOT, this);
	}

//	LineStatement self() {
//		return new LineStatement(this.clone(), true);
//	}
	
	public OperationSet intersection(Set... s) {
		return operation(LogicalOperator.AND, s);
	}

	public OperationSet union(Set... s) {
		return operation(LogicalOperator.OR, s);
	}

//	default OperationSet nand(AbstractSet... s) {
//		return operation(Operator.NAND, s);
//	}
//
//	default OperationSet nor(AbstractSet... s) {
//		return operation(Operator.NOR, s);
//	}

	public OperationSet symmetricDifference(Set... s) {
		return operation(LogicalOperator.XOR, s);
	}

	 @Override
	 public String toFullString() {
		 return toString();
	 }

	 @Override
	 public String toString() {
		 return null;
	 }

	 @Override
	 public boolean equals(final Object s) {
		 return this == s;
	 }

	 @Override
	 public Composition simplified() {
		 return null;
	 }

	 // @Override
	 // public int compareTo(final Composition statement) {
		//  return 0;
	 // }

	 @Override
	 public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		 godelBuilder.push(GodelNumberSymbols.SYNTAX_ERROR);
	 }

	 private static final Set emptySet = new SpecialSet(SpecialSetType.O); // TODO weird warning

	 public static Set empty() {
		 return emptySet;
	 }

	 //	default OperationSet xnor(AbstractSet... s) {
//		return operation(Operator.XNOR, s);
//	}
//
//	default OperationSet implies(AbstractSet s) {
//		return operation(Operator.IMPLIES, s);
//	}

}
