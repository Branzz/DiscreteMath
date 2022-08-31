package bran.tree.compositions.statements.special.equivalences.equation;

import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.special.equivalences.Equivalence;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;

import static bran.tree.compositions.statements.special.equivalences.equation.EquationType.EQUAL;

public class Equation extends Equivalence {

	private final EquationType equationType;

	public Equation(Expression left, EquationType equationType, Expression right) {
		super(left, right);
		this.equationType = equationType;
	}

	public Equation(Expression left, Expression right) {
		this(left, EQUAL, right);
	}

	@Override
	public EquivalenceType getEquivalenceType() {
		return equationType;
	}

	@Override
	protected boolean getTruth() {
		// if (left instanceof Expression && right instanceof Expression)
		// 	OperatorExpression.factor() TODO
		return super.getTruth();
	}

	@Override
	public boolean equals(final Object s) {
		return this == s || (s instanceof Equation e && ((left.equals(e.left) && right.equals(e.right)) || (right.equals(e.left) && left.equals(e.right))));
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		if (equationType == EQUAL) {
			left.appendGodelNumbers(godelBuilder);
			godelBuilder.push(GodelNumberSymbols.EQUALS);
			right.appendGodelNumbers(godelBuilder);
		} else {
			godelBuilder.push(GodelNumberSymbols.LOGICAL_NOT);
			godelBuilder.push(GodelNumberSymbols.LEFT);
			left.appendGodelNumbers(godelBuilder);
			godelBuilder.push(GodelNumberSymbols.EQUALS);
			right.appendGodelNumbers(godelBuilder);
			godelBuilder.push(GodelNumberSymbols.RIGHT);
		}
	}

	@Override
	public Statement negation() {
		return new Equation(left, equationType.opposite(), right);
	}

	// @Override // TODO
	// public Statement clone() {
	// 	return null;
	// 	// return new Equation(left.clone(), right.clone());
	// }

}
