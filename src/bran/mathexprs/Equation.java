package bran.mathexprs;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.operators.OperatorExpression;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Composition;
import bran.tree.Equivalable;
import bran.tree.TreePart;

import java.util.List;
import java.util.Stack;

public class Equation<T extends Composition> extends Equivalence<T> {

	public Equation(T left, EquationType equivalenceType, T right) {
		super(left, equivalenceType, right);
	}

	public Equation(T left, T right) {
		this(left, EquationType.EQUAL, right);
	}

	@Override
	protected boolean getTruth() {
		// if (left instanceof Expression && right instanceof Expression)
		// 	OperatorExpression.factor() TODO
		return super.getTruth();
	}

	@Override
	public Statement simplified() {
		return new Equation<>(left.simplified(), (EquationType) equivalenceType, right.simplified());
	}

	@Override
	public boolean equals(final Object s) {
		return this == s || (s instanceof Equation e && ((left.equals(e.left) && right.equals(e.right)) || (right.equals(e.left) && left.equals(e.right))));
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		if (equivalenceType == EquationType.EQUAL) {
			left.appendGodelNumbers(godelNumbers, variables);
			godelNumbers.push(GodelNumberSymbols.EQUALS);
			right.appendGodelNumbers(godelNumbers, variables);
		}
		else {
			godelNumbers.push(GodelNumberSymbols.LOGICAL_NOT);
			godelNumbers.push(GodelNumberSymbols.LEFT);
			left.appendGodelNumbers(godelNumbers, variables);
			godelNumbers.push(GodelNumberSymbols.EQUALS);
			right.appendGodelNumbers(godelNumbers, variables);
			godelNumbers.push(GodelNumberSymbols.RIGHT);
		}
	}

	@Override
	public Statement negation() {
		return new Equation<>(left, (EquationType) equivalenceType.opposite(), right);
	}

	// @Override // TODO
	// public Statement clone() {
	// 	return null;
	// 	// return new Equation(left.clone(), right.clone());
	// }

}
