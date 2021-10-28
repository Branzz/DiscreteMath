package bran.logic.statements;

import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.LogicalOperator;
import bran.mathexprs.*;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Branch;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static bran.logic.statements.VariableStatement.CONTRADICTION;
import static bran.logic.statements.VariableStatement.TAUTOLOGY;
import static bran.logic.statements.operators.LineOperator.CONSTANT;
import static bran.logic.statements.operators.LineOperator.NOT;

public class LineStatement extends Statement implements Branch<Statement, LineOperator> { // One child

	private final LineOperator lineOperator;
	private final Statement child;

	public LineStatement(Statement child) {
		this.child = child;
		lineOperator = NOT;
	}

	// public LineStatement(Statement child, boolean value) {
	// 	this.child = child;
	// 	lineOperator = value ? CONSTANT : NOT;
	// }

	public LineStatement(Statement child, LineOperator lineOperator) {
		this.child = child;
		this.lineOperator = lineOperator;
	}

	// /**
	//  * Bypass repeated not LineStatements
	//  */
//	public LineStatement realNot() {
//		return child.getClass().equals(LineStatement.class) && !child.getTruth() && !getTruth()
//				? new LineStatement(child, true)
//				: not();
//	}

	@Override
	public Statement getChild() {
		return child;
	}

	@Override
	public LineOperator getOperator() {
		return lineOperator;
	}

	// @Override
	// public Branch<Statement, LineOperator> create(Statement treePart, LineOperator branchFunction) {
	// 	return new LineStatement(treePart, branchFunction);
	// }

	@Override
	public boolean equals(Object s) {
		return s instanceof LineStatement lineStatement && lineOperator == lineStatement.lineOperator && child.equals(lineStatement.getChild());
	}

	@Override
	public Statement simplified() {
		Statement child = this.child.simplified();
		if (lineOperator == CONSTANT)
			return child;
		// ### operator is NOT ###
		if (child instanceof LineStatement childLine) // Double Negation Law - since child's simplified, it is a NOT
			return childLine.child;
		if (child instanceof VariableStatement childVariable) { // Negate Constants Law
			if (childVariable.equals(TAUTOLOGY))
				return CONTRADICTION;
			else if (childVariable.equals(CONTRADICTION))
				return TAUTOLOGY;
		} else if (child instanceof Inequality childEquivalence) {
			return new Inequality(childEquivalence.getLeft(), (InequalityType) childEquivalence.getEquivalenceType().opposite(), childEquivalence.getRight());
		} else if (child instanceof Equation childEquivalence) {
			return new Equation(childEquivalence.getLeft(), (EquationType) childEquivalence.getEquivalenceType().opposite(), childEquivalence.getRight());
		} else if (child instanceof OperationStatement childOperation) {
			LogicalOperator operator = childOperation.getOperator();
			if (operator == LogicalOperator.IMPLIES) {
				if (childOperation.getRight() instanceof LineStatement rightLine)
					return new OperationStatement(childOperation.getLeft(), LogicalOperator.AND, rightLine.getChild());
				else if (childOperation.getLeft() instanceof LineStatement leftLine)
					return new OperationStatement(leftLine.getChild(), LogicalOperator.NOR, childOperation.getRight());
				// else if (childOperation.getRight().equals(TAUTOLOGY)) TODO
				// 	return new OperationStatement(childOperation.getLeft(), Operator.AND, CONTRADICTION);
				// else if (childOperation.getRight().equals(CONTRADICTION))
				// 	return new OperationStatement(childOperation.getLeft(), Operator.AND, TAUTOLOGY);
				// else if (childOperation.getLeft().equals(TAUTOLOGY))
				// 	return new OperationStatement(CONTRADICTION, Operator.AND, childOperation.getRight());
				// else if (childOperation.getLeft().equals(CONTRADICTION))
				// 	return new OperationStatement(TAUTOLOGY, Operator.AND, childOperation.getRight());
			} else if (operator == LogicalOperator.REV_IMPLIES) {
				if (childOperation.getLeft() instanceof LineStatement leftLine)
					return new OperationStatement(leftLine.getChild(), LogicalOperator.AND, childOperation.getRight());
				else if (childOperation.getRight() instanceof LineStatement rightLine)
					return new OperationStatement(childOperation.getLeft(), LogicalOperator.NOR, rightLine.getChild());
			} else
				return new OperationStatement(childOperation.getLeft(), childOperation.getOperator().not(), childOperation.getRight());
		}
		return new LineStatement(child);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		if (lineOperator == NOT) {
			godelNumbers.push(GodelNumberSymbols.LOGICAL_NOT);
			godelNumbers.push(GodelNumberSymbols.LEFT);
			child.appendGodelNumbers(godelNumbers, variables);
			godelNumbers.push(GodelNumberSymbols.RIGHT);
		}
		else
			child.appendGodelNumbers(godelNumbers, variables);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	protected boolean getTruth() {
		return lineOperator.operate(child.getTruth());
	}

	@Override
	public List<Statement> getChildren() {
		List<Statement> current = new ArrayList<>();
		current.add(this);
		current.addAll(child.getChildren());
		return current;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return child.getVariables();
	}

	@Override
	public String toFullString() {
		return '(' + lineOperator.toString() + child.toFullString() + ')'; // TODO (parens around function?)
	}

	@Override
	public String toString() {
		return lineOperator.toString() + child.toString();
	}

	// @Override
	// public Statement clone() {
	// 	return new LineStatement(child.clone(), lineOperator);
	// }

	/*

	@Override
	protected boolean checkNegationLaw() {
		if (child.checkNegationLaw())
			child = ((OperationStatement) child).negatedLawStatement();
		return false;
	}

	@Override
	protected boolean checkIdempotentLaw() {
		if (child.checkIdempotentLaw())
			child = ((OperationStatement) child).identityUniversalBoundStatement();
		return false;
	}

	@Override
	protected boolean checkIdentityUniversalBoundLaw() {
		if (child.checkIdentityUniversalBoundLaw())
			child = ((OperationStatement) child).idempotentStatement();
		return false;
	}

	@Override
	protected boolean checkNegateConstantsLaw() {
		if (child.isConstant())
			return true;
		if (child.checkNegateConstantsLaw())
			child = ((LineStatement) child).getChild();
		return false;
	}

	@Override
	protected boolean checkDoubleNegationLaw() {
//		if (child.getClass().equals(LineStatement.class) && ((LineStatement) child).getChild().getClass().equals(LineStatement.class))
//			child = ((LineStatement) ((LineStatement) child).getChild()).getChild();
//		child.checkDoubleNegationLaw();
		if (child instanceof LineStatement && !child.getTruth() && !getTruth())
			return true;
		if (child.checkDoubleNegationLaw())
			child = ((LineStatement) ((LineStatement) child).getChild()).getChild();
		return false;
	}

	@Override
	protected boolean checkDeMorgansLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkAbsorptionLaw() {
		if (child.checkAbsorptionLaw())
			child = ((OperationStatement) child).absorptionStatement();
		return false;
	}

	@Override
	protected boolean checkExtendedAbsorptionLaw() {
		child.checkExtendedAbsorptionLaw();
		return false;
	}

	@Override
	protected boolean checkDistributiveLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkAssociativeLaw() {
		// TODO Auto-generated method stub
		return false;
	}

*/

}
