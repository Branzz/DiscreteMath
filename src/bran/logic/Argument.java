package bran.logic;

import java.util.ArrayList;

import bran.logic.statements.Statement;

public class Argument {

	private ArrayList<Statement> premises;
	private Statement conclusion;

	public Argument(Statement... statements) {
		premises = new ArrayList<Statement>();
		for (int i = 0; i < statements.length - 1; i++)
			premises.add(statements[i]);
		conclusion = statements[statements.length - 1];
	}

	protected boolean getTruth() {
		return Statement.andOf((premises.toArray(new Statement[premises.size()]))).implies(conclusion).getTruth();
	}

	public String toString() {
		String str = "";
		for (Statement s : premises)
			str += s + ",";
		str += "\u2234 " + conclusion.toString();
		return str;
	}

	public String toFullString() {
		String str = "";
		for (Statement s : premises)
			str += s + "\n";
		str += "\u2234 " + conclusion.toString();
		str += getTruth() ? "\nValid" : "\nInvalid";
		return str;
	}

	public Statement getConclusion() {
		return conclusion;
	}

	public ArrayList<? extends Statement> getPremises() {
		return premises;
	}

}
