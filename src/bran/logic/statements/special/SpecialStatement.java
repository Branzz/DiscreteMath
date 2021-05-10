package bran.logic.statements.special;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;

import java.util.List;

public abstract class SpecialStatement extends Statement {

	@Override
	protected boolean isConstant() {
		return false;
	}

	@Override
	public boolean equivalentTo(final Statement other) {
		return false;
	}

	@Override
	public List<Statement> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean checkNegateConstantsLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkNegationLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkIdempotentLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkIdentityUniversalBoundLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkDoubleNegationLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkDeMorgansLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkAbsorptionLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkExtendedAbsorptionLaw() {
		// TODO Auto-generated method stub
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
