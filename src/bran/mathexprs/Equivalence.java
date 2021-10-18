package bran.mathexprs;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.special.SpecialStatement;

	@Override
	public String toString() {
		return left.toString() + " " + equivalenceType + " " + right.toString();
	}

	public abstract Statement simplified();

	@Override
	public boolean equivalentTo(final Statement other) {
		return false;
	}

	@Override
	public List<Statement> getChildren() {
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

}
