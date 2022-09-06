package bran.tree.compositions.statements.special.proofs;

import java.util.ArrayList;
import java.util.List;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.SpecialStatement;
import bran.tree.compositions.godel.GodelBuilder;

public class Argument extends SpecialStatement {

	private ArrayList<Statement> premises;
	private Statement conclusion;

	public Argument(Statement... statements) {
		premises = new ArrayList<>();
		for (int i = 0; i < statements.length - 1; i++)
			premises.add(statements[i]);
		conclusion = statements[statements.length - 1];
	}

	@Override
	protected boolean getTruth() {
		return Statement.andOf((premises.toArray(new Statement[premises.size()]))).implies(conclusion).getTruth();
	}

	public Statement getConclusion() {
		return conclusion;
	}

	public ArrayList<Statement> getPremises() {
		return premises;
	}

	@Override
	public String toFullString() {
		String str = "";
		for (Statement s : premises)
			str += s + ",";
		str += "\u2234 " + conclusion.toString();
		return str;
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		return null;
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {

	}

	public String toValidInValidString() {
		String str = "";
		for (Statement s : premises)
			str += s + "\n";
		str += "\u2234 " + conclusion.toString();
		str += getTruth() ? "\nValid" : "\nInvalid";
		return str;
	}

	@Override
	public List<Composition> getChildren() {
		return null;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return null;
	}

	@Override
	public Statement negation() {
		return null;
	}

}
