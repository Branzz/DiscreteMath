package bran.tree.compositions.statements.special;

import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;

import java.util.List;

public abstract class SpecialStatement extends Statement {

	@Override
	protected boolean isConstant() {
		return false;
	}

	@Override
	public abstract List<Statement> getChildren();

	@Override
	public abstract List<VariableStatement> getVariables();

	public abstract Statement negation();

	@Override
	public Statement not() {
		return negation();
	}

}
