package old;

import java.util.ArrayList;

import bran.logic.statements.Statement;

public class XVariable {

	private boolean value;

	public boolean getTruth() {
		return value;
	}

//	public ConstantVariable notOf(ConstantVariable s) {
//		VariableStatement notStatement = s.clone();
//		notStatement.notSelf();
//		return notStatement;
//	}
//
//	public ConstantVariable not() {
//		VariableStatement notStatement = this.clone();
//		notStatement.notSelf();
//		return notStatement;
//	}

	public ArrayList<Statement> getChildren() {
		ArrayList<Statement> current = new ArrayList<Statement>();
		// current.add(this);
		return current;
	}

	public ArrayList<Statement> getVariables() {
		ArrayList<Statement> variable = new ArrayList<Statement>();
		// variable.add(this);
		return variable;
	}

	public String toString() {
		return "name";
	}

}
