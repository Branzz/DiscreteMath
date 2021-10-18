package bran.sets;

import bran.exceptions.VariableExpressionException;
import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.LogicalOperator;
import bran.tree.Leaf;

public interface Set extends Leaf
//Comparable<AbstractSet>,
 {

	boolean isSubsetOf(Set s);

	boolean isProperSubsetOf(Set s);

	boolean contains(Object o);

	// Object clone();

	// int compareTo(AbstractSet s);

	boolean equals(Object o);

	String toString();

	default OperationSet operation(LogicalOperator o, Set... s) {
		if (s.length < 0)
			throw new VariableExpressionException();
		if (s.length == 0)
			return new OperationSet(this, o, this);
		OperationSet combinedStatements = new OperationSet(this, o, s[0]);
		for (int i = 1; i < s.length; i++)
			combinedStatements = new OperationSet(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	default Set complement() {
		return new LineSet(LineOperator.NOT, this);
	}

//	LineStatement self() {
//		return new LineStatement(this.clone(), true);
//	}
	
	default OperationSet intersection(Set... s) {
		return operation(LogicalOperator.AND, s);
	}

	default OperationSet union(Set... s) {
		return operation(LogicalOperator.OR, s);
	}

//	default OperationSet nand(AbstractSet... s) {
//		return operation(Operator.NAND, s);
//	}
//
//	default OperationSet nor(AbstractSet... s) {
//		return operation(Operator.NOR, s);
//	}

	default OperationSet symmetricDifference(Set... s) {
		return operation(LogicalOperator.XOR, s);
	}

//	default OperationSet xnor(AbstractSet... s) {
//		return operation(Operator.XNOR, s);
//	}
//
//	default OperationSet implies(AbstractSet s) {
//		return operation(Operator.IMPLIES, s);
//	}

}
