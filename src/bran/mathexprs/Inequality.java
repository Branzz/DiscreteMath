package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Expression;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelBuilder;
import bran.tree.Composition;

public class Inequality extends Equivalence {

	private final InequalityType inequalityType;

	public Inequality(final Expression left, final InequalityType inequalityType, final Expression right) {
		super(left, right);
		this.inequalityType = inequalityType;
	}

	// public boolean evaluate() {
	// 	return inequalityType.evaluate(left.evaluate(), right.evaluate());
	// }

	@Override
	public EquivalenceType getEquivalenceType() {
		return inequalityType;
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
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		left.appendGodelNumbers(godelBuilder);
		godelBuilder.push(GodelNumberSymbols.EQUALS); // TODO not possible, but the user can do it
		right.appendGodelNumbers(godelBuilder);
	}

	@Override
	public Statement negation() {
		return new Inequality(left, inequalityType.opposite(), right);
	}

	// @Override
	// public Statement clone() {
	// 	return new Equation(left.clone(), right.clone());
	// }

}



