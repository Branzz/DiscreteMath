package bran.logic.statements.special;

import bran.combinatorics.Counter;
import bran.logic.tree.Equivalable;
import bran.logic.tree.Holder;
import bran.sets.FiniteSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static bran.logic.statements.operators.DisplayStyle.displayStyle;

public class ExistentialStatement <I, E extends Holder<I> & Equivalable<? super E>> extends SpecialStatement {

	private final int argumentSize;
	private final UniversalStatementArguments<E> universalStatement;
	private final E[] variables;
	private final FiniteSet<I> domain;
	private boolean proven;

	public static final String[] forAllSymbols = { "\u2203", "\u2203", "\u2203", "\u2203", "\u2203", "\u2203" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208", "\u2208", "\u2208" };

	public ExistentialStatement(final E[] variables, final UniversalStatementArguments<E> universalStatement, final FiniteSet<I> domain, final boolean proven) {
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (displayStyle) {
			case NAME -> "There exists ";
			case LOWERCASE_NAME -> "there exists ";
			default -> forAllSymbols[displayStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(Object::toString).collect(Collectors.joining(",")));
		sb.append(switch (displayStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[displayStyle.index()];
		});
		return sb.append(' ').append(domain).append(" such that ").append(universalStatement.state(variables)).toString();
	}

}
