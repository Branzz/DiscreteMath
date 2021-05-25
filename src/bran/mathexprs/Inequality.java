package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.Expression;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Equivalable;
import bran.tree.TreePart;

import java.util.List;
import java.util.Stack;

public class Inequality<T extends TreePart & Equivalable<T> & Comparable<T>> extends Equivalence {

	private final T left;
	private final InequalityType inequalityType;
	private final T right;

	public Inequality(final T left, final InequalityType inequalityType, final T right) {
		this.left = left;
		this.inequalityType = inequalityType;
		this.right = right;
	}

	@Override
	protected boolean getTruth() {
		return inequalityType.evaluate(left, right);
	}

	// public boolean evaluate() {
	// 	return inequalityType.evaluate(left.evaluate(), right.evaluate());
	// }

	@Override
	public String toString() {
		return left + " " + inequalityType + " " + right;
	}

	@Override
	public boolean equals(final Object s) {
		// return s instanceof Inequality<T> i && ((left.equals(i.left) && right.equals(i.right) && inequalityType == i.inequalityType);
		// if (s instanceof Inequality) {
		// 	Inequality<T> inq = (Inequality<T>) s;
		// 	return ((left.equals(inq.left) && right.equals(inq.right) && inequalityType == inq.inequalityType)
		// 			|| (right.equals(inq.left) && left.equals(inq.right) && inequalityType == inequalityType.opposite()));
		// }
		// return false;
		return s instanceof Inequality i && ((left.equals(i.left) && right.equals(i.right) && inequalityType == i.inequalityType)
											 || (right.equals(i.left) && left.equals(i.right) && inequalityType == inequalityType.opposite()));
	}

	@Override
	public Statement simplified() {
		return left instanceof Statement l && right instanceof Statement r
			   ? new Equation<>(l.simplified(), r.simplified()) :
			   left instanceof Expression l && right instanceof Expression r
				   ? new Equation<>(l.simplified(), r.simplified())
				   : new Equation<>(left, right);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		left.appendGodelNumbers(godelNumbers, variables);
		godelNumbers.push(GodelNumberSymbols.EQUALS); // TODO not possible, but the user can do it
		right.appendGodelNumbers(godelNumbers, variables);
	}

	@Override
	public boolean equivalentTo(final Statement other) {
		return false;
	}

	@Override
	public List<Statement> getChildren() {
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

	@Override
	public Statement negation() {
		return null;
	}

	// @Override
	// public Statement clone() {
	// 	return new Equation(left.clone(), right.clone());
	// }

}



