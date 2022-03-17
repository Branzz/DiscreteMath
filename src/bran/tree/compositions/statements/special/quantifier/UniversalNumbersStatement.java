package bran.tree.compositions.statements.special.quantifier;

import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.SpecialStatement;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.Composition;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.tree.compositions.statements.StatementDisplayStyle.*;

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

	public String toString(Function<Composition, String> stringMapper) {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (statementStyle) {
			case NAME -> "For all ";
			case LOWERCASE_NAME -> "for all ";
			default -> forAllSymbols[statementStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(stringMapper).collect(Collectors.joining(",")));
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		});
		if (domain instanceof SpecialSet && (statementStyle == NAME || statementStyle == LOWERCASE_NAME))
			sb.append(((SpecialSet) domain).toFormalString());
		else
			sb.append(domain);
		return sb.append(" ").append(stringMapper.apply(statement)).toString();
	}


	@Override
	public String toFullString() {
		return toString(Composition::toFullString);
	}

	@Override
	public String toString() {
		return toString(Composition::toString);
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		return new UniversalNumbersStatement(domain, statement.simplified(), variables);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		negation().appendGodelNumbers(godelBuilder); // TODO negation??
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
		return new ExistentialNumbersStatement(domain, statement.not(), variables);
	}

	// @Override
	// public Statement clone() {
	// 	return new UniversalNumbersStatement((Set) domain.clone(), statement.clone(), variables.clone());
	// }

}
