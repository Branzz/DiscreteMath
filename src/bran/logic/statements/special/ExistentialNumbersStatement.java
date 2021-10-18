package bran.logic.statements.special;

import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.mathexprs.treeparts.Variable;
import bran.sets.Set;
import bran.sets.SpecialSet;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariable;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Composition;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
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

	public String toString(Function<Composition, String> stringMapper) {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> "there exists a ";
			default -> forEachSymbols[statementStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(stringMapper).collect(Collectors.joining(",")))
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

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		return null;
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variablesMap) {
		for (final Variable variable : variables) {
			godelNumbers.push(GodelNumberSymbols.LEFT);
			godelNumbers.push(GodelNumberSymbols.EACH);
			godelNumbers.push(variablesMap.get(variable));
			godelNumbers.push(GodelNumberSymbols.RIGHT);
			godelNumbers.push(GodelNumberSymbols.LEFT);
		}
		statement.appendGodelNumbers(godelNumbers, variablesMap);
		for (int i = 0; i < variables.length; i++)
			godelNumbers.push(GodelNumberSymbols.RIGHT);
	}

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

	@Override
	public Statement negation() {
		return new UniversalNumbersStatement(domain, statement.not(), variables);
	}

	// @Override
	// public Statement clone() {
	// 	return null;
	// }

}
