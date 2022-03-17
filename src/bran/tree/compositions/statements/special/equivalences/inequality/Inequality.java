package bran.tree.compositions.statements.special.equivalences.inequality;

import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.regular.RangedSet;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.sets.regular.SpecialSetType;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.equivalences.Equivalence;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;

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
	public Set toSet() {
		boolean leftIsVar = left instanceof Variable;
		boolean rightIsVar = right instanceof Variable;
		boolean leftIsConst = left instanceof Constant;
		boolean rightIsConst = right instanceof Constant;
		if ((!leftIsVar && !rightIsVar) || (!leftIsConst && !rightIsConst)) {
			final Statement simplified = simplified();
			if (simplified instanceof Inequality simplIneq) {
				this.left = simplIneq.left;
				this.right = simplIneq.right;
				leftIsVar = left instanceof Variable;
				rightIsVar = right instanceof Variable;
				leftIsConst = left instanceof Constant;
				rightIsConst = right instanceof Constant;
				if ((!leftIsVar && !rightIsVar) || (!leftIsConst && !rightIsConst))
					return null;
			} else if (simplified instanceof VariableStatement var) {
				return null;
				// if (var.equals(VariableStatement.TAUTOLOGY))
				// 	return new SpecialSet(SpecialSetType.R);
				// else if (var.equals(VariableStatement.CONTRADICTION))
				// 	return new SpecialSet(SpecialSetType.O);
			}
		}
		if (leftIsVar && rightIsConst) {
			if (inequalityType.lesser()) {
				return new RangedSet(new SpecialSet(SpecialSetType.R),
									 false, NumberLiteral.NEG_INFINITY,
									 inequalityType.equal(), ((Constant) right).value());
			} else {
				return new RangedSet(new SpecialSet(SpecialSetType.R),
									 inequalityType.equal(), ((Constant) right).value(),
									 false, NumberLiteral.INFINITY);
			}
		} else if (rightIsVar && leftIsConst) {
			if (inequalityType.lesser()) {
				return new RangedSet(new SpecialSet(SpecialSetType.R),
									 inequalityType.equal(), ((Constant) left).value(),
									 false, NumberLiteral.INFINITY);
			} else {
				return new RangedSet(new SpecialSet(SpecialSetType.R),
									 false, NumberLiteral.NEG_INFINITY,
									 inequalityType.equal(), ((Constant) left).value());
			}
		}
		return null;
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



