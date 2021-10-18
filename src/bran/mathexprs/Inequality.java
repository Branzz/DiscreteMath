package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Composition;
import bran.tree.Equivalable;
import bran.tree.TreePart;

import java.util.Stack;

public class Inequality<T extends Composition> extends Equivalence<T> {

	public Inequality(final T left, final InequalityType equivalenceType, final T right) {
		super(left, equivalenceType, right);
	}

	// public boolean evaluate() {
	// 	return inequalityType.evaluate(left.evaluate(), right.evaluate());
	// }

	@Override
	public Statement simplified() {
		return new Inequality<>(left.simplified(), (InequalityType) equivalenceType, right.simplified());
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
		return s instanceof Inequality i && ((left.equals(i.left) && right.equals(i.right) && equivalenceType == i.equivalenceType)
											 || (right.equals(i.left) && left.equals(i.right) && equivalenceType == equivalenceType.opposite()));
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		left.appendGodelNumbers(godelNumbers, variables);
		godelNumbers.push(GodelNumberSymbols.EQUALS); // TODO not possible, but the user can do it
		right.appendGodelNumbers(godelNumbers, variables);
	}

	@Override
	public Statement negation() {
		return new Equation<>(left, (EquationType) equivalenceType.opposite(), right);
	}

	// @Override
	// public Statement clone() {
	// 	return new Equation(left.clone(), right.clone());
	// }

}



