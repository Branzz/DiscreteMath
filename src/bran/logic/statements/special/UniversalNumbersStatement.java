package bran.logic.statements.special;

import bran.logic.statements.Statement;
import bran.mathexprs.treeparts.Variable;
import bran.sets.Set;
import bran.sets.SpecialSet;

import java.util.Arrays;
import java.util.stream.Collectors;

import static bran.logic.statements.StatementDisplayStyle.*;

public class UniversalNumbersStatement extends SpecialStatement {

	private final Variable[] variables;
	private final Set domain;
	private final Statement statement;

	public static final String[] forAllSymbols = {"\u2200", "\u2200", "\u2200", "\u2200" };
	public static final String[] inSetSymbols  = {"\u2208", "\u2208", "\u2208", "\u2208" };

	public UniversalNumbersStatement(final Set domain, final Statement statement, final Variable... variables) {
		this.variables = variables;
		this.domain = domain;
		this.statement = statement;
	}

	@Override
	protected boolean getTruth() {
		return statement.truth();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (statementStyle) {
			case NAME -> "For all ";
			case LOWERCASE_NAME -> "for all ";
			default -> forAllSymbols[statementStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(Variable::toString).collect(Collectors.joining(",")));
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		});
		if (domain instanceof SpecialSet && (statementStyle == NAME || statementStyle == LOWERCASE_NAME))
			sb.append(((SpecialSet) domain).toFormalString());
		else
			sb.append(domain);
		return sb.append(" ").append(statement).toString();
	}

	// @Override
	// public Statement clone() {
	// 	return new UniversalNumbersStatement((Set) domain.clone(), statement.clone(), variables.clone());
	// }

}
