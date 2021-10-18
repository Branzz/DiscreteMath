package bran.logic.statements.special;

import bran.combinatorics.Counter;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.sets.Set;
import bran.sets.SpecialSet;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Composition;
import bran.tree.Equivalable;
import bran.sets.FiniteSet;
import bran.tree.Holder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.logic.statements.StatementDisplayStyle.statementStyle;

public class ExistentialStatement <I, E extends Composition & Holder<I> & Equivalable<? super E>> extends QuantifiedStatement {

	private final int argumentSize;
	private final QuantifiedStatementArguments<E> universalStatement;
	private final E[] variables;
	private final Set domain;
	private boolean proven;

	public static final String[] forAllSymbols = { "\u2203", "\u2203", "\u2203", "\u2203" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208" };

	public ExistentialStatement(final QuantifiedStatementArguments<E> universalStatement, final Set domain, final boolean proven, final E... variables) {
		this.argumentSize = variables.length;
		this.universalStatement = universalStatement;
		this.variables = variables;
		this.domain = domain;
		this.proven = proven;
	}

	@Override
	protected boolean getTruth() {
		// return proven;
		return exhaustiveProof();
	}

	public boolean exhaustiveProof() {
		if (domain instanceof FiniteSet finiteSet) {
			List<I> choices = finiteSet.stream().toList();
			Counter counter = new Counter(argumentSize, choices.size());
			while (counter.hasNext()) {
				int[] count = counter.next();
				for (int i = 0; i < argumentSize; i++)
					variables[i].set(choices.get(count[i]));
				if (universalStatement.state(variables).truth())
					return true;
			}
			return false;
		}
		else
			return false;
	}

	public String exhaustiveProofString() {
		if (domain instanceof FiniteSet finiteSet) {
			StringBuilder sb = new StringBuilder();
			List<I> choices = finiteSet.stream().toList();
			Counter counter = new Counter(argumentSize, choices.size());
			while (counter.hasNext()) {
				int[] count = counter.next();
				for (int i = 0; i < argumentSize; i++)
					variables[i].set(choices.get(count[i]));
				sb.append(Arrays.stream(variables)
											   .map(v -> v.toString() + " = " + v.get())
											   .collect(Collectors.joining(", ", "for ", ", ")));
				Statement statement = universalStatement.state(variables);
				if (statement instanceof QuantifiedStatement quantifiedStatement)
					sb.append("\u21b4\n\t").append(quantifiedStatement.exhaustiveProofString().replaceAll("\n", "\n\t"));
				else
					sb.append(statement);
				// sb.append("\n");
				if (statement.truth())
					return sb.append("\nwhich is true. (valid)").toString();
				else if (counter.hasNext())
					sb.append(", which is false... continuing\n");
			}
			return sb.append("\nwhich are all false (invalid)").toString();
		} else if (domain instanceof SpecialSet specialSet && specialSet.isZero()) { // TODO and for universal and for exhaustiveProof()
			// StringBuilder sb = new StringBuilder();
			// List<I> choices = Collections.singletonList();
			// Counter counter = new Counter(argumentSize, choices.size());
			// while (counter.hasNext()) {
			// 	int[] count = counter.next();
			// 	for (int i = 0; i < argumentSize; i++)
			// 		variables[i].set(choices.get(count[i]));
			// 	sb.append(Arrays.stream(variables)
			// 					.map(v -> v.toString() + " = " + v.get())
			// 					.collect(Collectors.joining(", ", "for ", ", ")));
			// 	Statement statement = universalStatement.state(variables);
			// 	if (statement instanceof QuantifiedStatement quantifiedStatement)
			// 		sb.append("\u21b4\n\t").append(quantifiedStatement.exhaustiveProofString().replaceAll("\n", "\n\t"));
			// 	else
			// 		sb.append(statement);
			// 	// sb.append("\n");
			// 	if (statement.truth())
			// 		return sb.append("\nwhich is true. (valid)").toString();
			// 	else if (counter.hasNext())
			// 		sb.append(", which is false... continuing\n");
			// return sb.append("\nwhich are all false (invalid)").toString();
		}
		return "domain too large";
	}

	public String toString(Function<E, String> stringMapper) {
		return switch (statementStyle) {
			case NAME -> "There exists ";
			case LOWERCASE_NAME -> "there exists ";
			default -> forAllSymbols[statementStyle.index()];
		} + Arrays.stream(variables)
				  .map(stringMapper)
				  .collect(Collectors.joining(",")) + switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		} + domain + "|" + universalStatement.state(variables);
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
		for (final E variable : variables) {
			godelNumbers.push(GodelNumberSymbols.LEFT);
			godelNumbers.push(GodelNumberSymbols.EACH);
			godelNumbers.push(variablesMap.get(variable));
			godelNumbers.push(GodelNumberSymbols.RIGHT);
			godelNumbers.push(GodelNumberSymbols.LEFT);
		}
		universalStatement.state(variables).appendGodelNumbers(godelNumbers, variablesMap);
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
		return new UniversalStatement<>(e -> universalStatement.state(e).not(), domain, proven, variables);
	}

}
