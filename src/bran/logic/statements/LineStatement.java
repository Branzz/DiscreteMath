package bran.logic.statements;

import java.util.ArrayList;
import java.util.List;

import bran.logic.statements.operators.LineOperator;
import bran.tree.Branch;

import static bran.logic.statements.operators.LineOperator.NOT;

public class LineStatement extends Statement implements Branch<Statement, LineOperator> { // One child

	private final LineOperator lineOperator;
	private Statement child;

	public LineStatement(Statement child) { // NOT preferred
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
	public String toString() {
		return '(' + lineOperator.toString() + child.toString() + ')'; //TODO
	}

	// @Override
	// public Statement clone() {
	// 	return new LineStatement(child.clone(), lineOperator);
	// }

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

}
