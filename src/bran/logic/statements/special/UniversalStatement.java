package bran.logic.statements.special;

import bran.combinatorics.Combinator;
import bran.combinatorics.CombinatorList;
import bran.combinatorics.Combinatorics;
import bran.combinatorics.Counter;
import bran.logic.statements.Statement;
import bran.logic.tree.Equivalable;
import bran.logic.tree.Holder;
import bran.logic.tree.Leaf;
import bran.logic.tree.TreePart;
import bran.sets.FiniteSet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static bran.logic.statements.operators.DisplayStyle.*;

public class UniversalStatement <I, E extends Holder<I> & Equivalable<? super E>> extends SpecialStatement {

	private final int argumentSize;
	private final UniversalStatementArguments<E> universalStatement;
	private final E[] variables;
	private final FiniteSet<I> domain;
	private boolean proven;

	public static final String[] forAllSymbols = { "\u2200", "\u2200", "\u2200", "\u2200", "\u2200", "\u2200" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208", "\u2208", "\u2208" };

	public UniversalStatement(final E[] variables, final UniversalStatementArguments<E> universalStatement, final FiniteSet<I> domain, final boolean proven) {
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
			if (!universalStatement.state(variables).truth())
				return false;

		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (displayStyle) {
			case NAME -> "For all ";
			case LOWERCASE_NAME -> "for all ";
			default -> forAllSymbols[displayStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(Object::toString).collect(Collectors.joining(",")));
		sb.append(switch (displayStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[displayStyle.index()];
		});
		return sb.append(' ').append(domain).append(", ").append(universalStatement.state(variables)).toString();
	}

}
