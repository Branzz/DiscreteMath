package bran.logic.statements.special;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Variable;
import bran.sets.Set;
import bran.sets.SpecialSet;

import java.util.Arrays;
import java.util.stream.Collectors;

import static bran.logic.statements.StatementDisplayStyle.*;
import static bran.logic.statements.StatementDisplayStyle.statementStyle;

public class ExistentialNumbersStatement extends SpecialStatement {

	private final Variable[] variables;
	private final Set domain;
	private final Statement statement;

	public static final String[] forEachSymbols = {"\u2203", "\u2203", "\u2203", "\u2203" };
	public static final String[] inSetSymbols   = {"\u2208", "\u2208", "\u2208", "\u2208" };

	public ExistentialNumbersStatement(final Set domain, final Statement statement, final Variable... variables) {
		this.variables = variables;
		this.domain = domain;
		this.statement = statement;
	}

	@Override
	protected boolean getTruth() {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> "there exists a ";
			default -> forEachSymbols[statementStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(Variable::toString).collect(Collectors.joining(",")))
		  .append(", ");
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> "in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		});
		if (domain instanceof SpecialSet && (statementStyle == NAME || statementStyle == LOWERCASE_NAME))
			sb.append(((SpecialSet) domain).toFormalString());
		else
			sb.append(domain);
		return sb.append(" such that ").append(statement).toString();
	}

	// @Override
	// public Statement clone() {
	// 	return null;
	// }

}
