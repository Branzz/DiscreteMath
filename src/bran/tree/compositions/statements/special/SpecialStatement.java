package bran.tree.compositions.statements.special;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;

import java.util.List;

public abstract class SpecialStatement extends Statement {

	@Override
	public abstract List<? extends Composition> getChildren();

	@Override
	public abstract List<VariableStatement> getVariables();

	public abstract Statement negation();

	@Override
	public Statement not() {
		return negation();
	}

}
