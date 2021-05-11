package bran.logic.statements.special;

import bran.combinatorics.Counter;
import bran.logic.statements.Statement;
import bran.tree.Equivalable;
import bran.sets.FiniteSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static bran.logic.statements.operators.DisplayStyle.displayStyle;

public class ExistentialStatement <I, E extends bran.tree.Holder<I> & Equivalable<? super E>> extends QuantifiedStatement {

	private final int argumentSize;
	private final QuantifiedStatementArguments<E> universalStatement;
	private final E[] variables;
	private final FiniteSet<I> domain;
	private boolean proven;

	public static final String[] forAllSymbols = { "\u2203", "\u2203", "\u2203", "\u2203", "\u2203", "\u2203" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208", "\u2208", "\u2208" };

	public ExistentialStatement(final QuantifiedStatementArguments<E> universalStatement, final FiniteSet<I> domain, final boolean proven, final E... variables) {
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
		List<I> choices = domain.stream().toList();
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

	public String exhaustiveProofString() {
		StringBuilder sb = new StringBuilder();
		List<I> choices = domain.stream().toList();
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
	}

	@Override
	public String toString() {
		return switch (displayStyle) {
			case NAME -> "There exists ";
			case LOWERCASE_NAME -> "there exists ";
			default -> forAllSymbols[displayStyle.index()];
		} + Arrays.stream(variables)
				  .map(Object::toString)
				  .collect(Collectors.joining(",")) + switch (displayStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[displayStyle.index()];
		} + domain + "|" + universalStatement.state(variables);
	}

}
