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
	public abstract boolean equivalentTo(final Statement other);

	@Override
	public abstract List<Statement> getChildren();

	@Override
	public abstract List<VariableStatement> getVariables();

}
