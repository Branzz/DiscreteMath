package bran.tree.compositions.statements;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.operators.UnaryStatementOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.inequality.Inequality;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.structure.MonoBranch;

import java.util.ArrayList;
import java.util.List;

import static bran.tree.compositions.statements.VariableStatement.CONTRADICTION;
import static bran.tree.compositions.statements.VariableStatement.TAUTOLOGY;
import static bran.tree.compositions.statements.operators.UnaryStatementOperator.CONSTANT;
import static bran.tree.compositions.statements.operators.UnaryStatementOperator.NOT;

public class UnaryStatement extends Statement implements MonoBranch<Statement, UnaryStatementOperator> { // One child

	private final UnaryStatementOperator unaryStatementOperator;
	private Statement child;

	public UnaryStatement(Statement child) {
		this.child = child;
		unaryStatementOperator = NOT;
	}

	// public LineStatement(Statement child, boolean value) {
	// 	this.child = child;
	// 	lineOperator = value ? CONSTANT : NOT;
	// }

	public UnaryStatement(UnaryStatementOperator unaryStatementOperator, Statement child) {
		this.child = child;
		this.unaryStatementOperator = unaryStatementOperator;
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
	public UnaryStatementOperator getOperator() {
		return unaryStatementOperator;
	}

	// @Override
	// public Branch<Statement, LineOperator> create(Statement treePart, LineOperator branchFunction) {
	// 	return new LineStatement(treePart, branchFunction);
	// }


	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		if (original.equals(child))
			child = (Statement) replacement;
		else
			child.replaceAll(original, replacement);
	}

	@Override
	public boolean equals(Object s) {
		return s instanceof UnaryStatement unaryStatement && unaryStatementOperator == unaryStatement.unaryStatementOperator && child.equals(unaryStatement.getChild());
	}

	@Override
	public Statement simplified() { // TODO child Special statement switch
		Statement child = this.child.simplified();
		if (unaryStatementOperator == CONSTANT)
			return child;
		// ### operator is NOT ###
		if (child instanceof UnaryStatement childLine) // Double Negation Law - since child's simplified, it is a NOT
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
		} else if (child instanceof StatementOperation childOperation) {
			LogicalOperator operator = childOperation.getOperator();
			if (operator == LogicalOperator.IMPLIES) {
				if (childOperation.getRight() instanceof UnaryStatement rightLine)
					return new StatementOperation(childOperation.getLeft(), LogicalOperator.AND, rightLine.getChild());
				else if (childOperation.getLeft() instanceof UnaryStatement leftLine)
					return new StatementOperation(leftLine.getChild(), LogicalOperator.NOR, childOperation.getRight());
				// else if (childOperation.getRight().equals(TAUTOLOGY)) TODO
				// 	return new OperationStatement(childOperation.getLeft(), Operator.AND, CONTRADICTION);
				// else if (childOperation.getRight().equals(CONTRADICTION))
				// 	return new OperationStatement(childOperation.getLeft(), Operator.AND, TAUTOLOGY);
				// else if (childOperation.getLeft().equals(TAUTOLOGY))
				// 	return new OperationStatement(CONTRADICTION, Operator.AND, childOperation.getRight());
				// else if (childOperation.getLeft().equals(CONTRADICTION))
				// 	return new OperationStatement(TAUTOLOGY, Operator.AND, childOperation.getRight());
			} else if (operator == LogicalOperator.REV_IMPLIES) {
				if (childOperation.getLeft() instanceof UnaryStatement leftLine)
					return new StatementOperation(leftLine.getChild(), LogicalOperator.AND, childOperation.getRight());
				else if (childOperation.getRight() instanceof UnaryStatement rightLine)
					return new StatementOperation(childOperation.getLeft(), LogicalOperator.NOR, rightLine.getChild());
			} else
				return new StatementOperation(childOperation.getLeft(), childOperation.getOperator().not(), childOperation.getRight());
		}
		return new UnaryStatement(child);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		if (unaryStatementOperator == NOT) {
			godelBuilder.push(GodelNumberSymbols.LOGICAL_NOT);
			boolean childIsVar = child instanceof VariableStatement;
			if (!childIsVar)
				godelBuilder.push(GodelNumberSymbols.LEFT);
			child.appendGodelNumbers(godelBuilder);
			if (!childIsVar)
				godelBuilder.push(GodelNumberSymbols.RIGHT);
		}
		else
			child.appendGodelNumbers(godelBuilder);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	protected boolean getTruth() {
		return unaryStatementOperator.operate(child.getTruth());
	}

	@Override
	public List<Composition> getChildren() {
		List<Composition> current = new ArrayList<>();
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
		return '(' + unaryStatementOperator.toString() + child.toFullString() + ')'; // TODO (parens around function?)
	}

	@Override
	public String toString() {
		return unaryStatementOperator.toString() + child.toString();
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
