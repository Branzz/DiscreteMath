package bran.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;

public class TruthTable {

	private final static byte CAPS_STYLE = 0, BINARY_STYLE = 1;
	private static byte tableStyle = BINARY_STYLE;
	private static String trueString = tableStyle == CAPS_STYLE ? "T" : tableStyle == BINARY_STYLE ? "1" : "E";
	private static String falseString = tableStyle == CAPS_STYLE ? "F" : tableStyle == BINARY_STYLE ? "0" : "E";
	private static boolean truesFirst = false;
//	private static byte detailLevel = 0;

	public static String getTable(Statement... separateStatements) {
		if (separateStatements.length == 0)
			return "";

		ArrayList<Statement> allStatements = new ArrayList<>(); // Remove repeat Statements from all statements
		for (Statement statement : separateStatements)
			allStatements.addAll(statement.getChildren());
		ArrayList<Statement> statements = new ArrayList<>();
		while(allStatements.size() != 0) {
			statements.add(allStatements.get(0));
			allStatements.removeAll(Collections.singletonList(allStatements.get(0)));
		}

		Statement statementRoot = separateStatements[0];
		for (int i = 1; i < separateStatements.length; i++)
			statementRoot = statementRoot.and(separateStatements[i]); //TODO statementRoot = statementRoot.clone().and(separateStatements[i]);
		List<VariableStatement> allVariables = statementRoot.getVariables(); // Remove repeat Variables from all variables
		List<VariableStatement> variables = new ArrayList<>();
		while(allVariables.size() != 0) {
			variables.add(allVariables.get(0));
//			allVariables.removeAll(Collections.singletonList(allVariables.get(0)));
			for (int i = 0; i < allVariables.size();)
				if (allVariables.get(i).toString().equals(variables.get(variables.size() - 1).toString())) // Based on name of variable TODO doesn't need to be
					allVariables.remove(i);
				else
					i++;
		}

		StringBuilder table = new StringBuilder();

		ArrayList<String> statementStrings = new ArrayList<>();
		ArrayList<String> statementStringLeftSpacers = new ArrayList<>();
		ArrayList<String> statementStringRightSpacers = new ArrayList<>();

//		statements.addAll(variables);

		for (VariableStatement v : variables)
			statements.add(0, v);

		variables.removeIf(VariableStatement::isConstant);

		Comparator<Statement> compareStringLengthFirst = Comparator.comparingInt((Statement s) -> s.toString().length())
																   .thenComparing(Statement::toString);
		statements.sort(compareStringLengthFirst);
		variables.sort(compareStringLengthFirst);

//		for (Statement v : variables) // Remove negative parts? (Depends on depth)
//			if (!v.positivity)
//				variables.remove(v);

		for (Statement s : statements) { // Draw header
			statementStrings.add(" " + s.toString() + " ");
			table.append(statementStrings.get(statementStrings.size() - 1))
				 .append("\u2502"); // Get column spaces
		}

		table.deleteCharAt(table.length() - 1);
		table.append("\u2502\n"); // Draw horizontal break
		for (String statementString: statementStrings) {
			String spacer = " ".repeat(statementString.length() / 2);
			statementStringLeftSpacers.add(statementString.length() % 2 == 0 ? spacer.substring(0, spacer.length() - 1) : spacer);
			statementStringRightSpacers.add(spacer);
		}

		StringBuilder bottom = new StringBuilder("\u2514");
		StringBuilder top = new StringBuilder("\u250c");
		table.append("\u251c");
		for (int i = 0; i < statements.size(); i++) {
			for (int j = 0; j < statementStrings.get(i).length(); j++) {
				top.append("\u2500");
				table.append("\u2500");
				bottom.append("\u2500");
			}
			if (i == statements.size() - 1)
				break;
			top.append("\u252c");
			table.append("\u253c");
			bottom.append("\u2534");
		}
		top.append("\u2510\n\u2502");
		table.append("\u2524\n\u2502");
		bottom.append("\u2518");

		for (int i = 0; i < Math.pow(2, variables.size()); i++) {
			for (int j = 0; j < variables.size(); j++)
				variables.get(variables.size() - j - 1).setValue((i >> j & 1) == (truesFirst ? 0 : 1));
			for (int j = 0; j < statements.size(); j++) {
				table.append(statementStringLeftSpacers.get(j))
					 .append(statements.get(j).truth() ? trueString : falseString)
					 .append(statementStringRightSpacers.get(j))
					 .append("\u2502");
			}
			table.deleteCharAt(table.length() - 1);
			table.append("\u2502\n\u2502");
		}


		return top.toString() + table.deleteCharAt(table.length() - 1).toString() + bottom.toString();
	} //U+2573 x symbol

	public static String getTable(Argument argument) {
		ArrayList<Statement> statements = new ArrayList<>(argument.getPremises());
		statements.add(argument.getConclusion());
		Statement[] statementsArray = new Statement[statements.size()];
		for (int i = 0; i < statements.size(); i++)
			statementsArray[i] = statements.get(i);
		return getTable(statementsArray);
	}

	// static class CompareStrings implements Comparator<Statement> {
	//
	// 	@Override
	// 	public int compare(Statement o1, Statement o2) {
	// 		int lengthDifference = o1.toString().length() - o2.toString().length();
	// 		if (lengthDifference == 0)
	// 			return o1.toString().compareTo(o2.toString());
	// 		return lengthDifference;
	// 	}
	//
	// }

	public static String getLastColumn(OperationStatement statementRoot) {
		String trueString;
		String falseString;
		switch (tableStyle) {
		case CAPS_STYLE:
			trueString = "T";
			falseString = "F";
		case BINARY_STYLE:
		default:
			trueString = "1";
			falseString = "0";
		}

		List<VariableStatement> allVariables = statementRoot.getVariables();
		List<VariableStatement> variables = new ArrayList<>();
		while(allVariables.size() != 0) {
			variables.add(allVariables.get(0));
			allVariables.removeAll(Collections.singletonList(allVariables.get(0)));
		}

		StringBuilder table = new StringBuilder(statementRoot.toString() + "\n");

		for (int j = 0; j < statementRoot.toString().length(); j++)
			table.append("-");
		table.append("\n");

		String spacer = " ".repeat(statementRoot.toString().length() / 2);

		for (int i = 0; i < Math.pow(2, variables.size()); i++)
				table.append(spacer)
					 .append(statementRoot.getTruth() ? trueString : falseString)
					 .append("\n");

		return table.toString();
	}

}

//for (Statement s : statements) {
//statementStrings.add(" " + s.toString() + " ");
//table += statementStrings.get(statementStrings.size() - 1) + "|";
//}
//for (int j = 0; j < variables.size(); j++)
//table += "---+";
//for (int j = 0; j < variables.size(); j++)
//table += " " + (variables.get(j).getTruth() ? trueString : falseString) + " |";
