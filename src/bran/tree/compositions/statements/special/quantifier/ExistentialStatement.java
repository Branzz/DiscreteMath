package bran.tree.compositions.statements.special.quantifier;

import bran.combinatorics.Counter;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.Composition;
import bran.tree.Equivalable;
import bran.tree.compositions.sets.regular.FiniteSet;
import bran.tree.Holder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

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
											   .map(v -> v.toFullString() + " = " + v.get())
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
		return null;
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		for (final E variable : variables) {
			godelBuilder.push(GodelNumberSymbols.LEFT);
			godelBuilder.push(GodelNumberSymbols.EACH);
			godelBuilder.push(godelBuilder.getVar(variable));
			godelBuilder.push(GodelNumberSymbols.RIGHT);
			godelBuilder.push(GodelNumberSymbols.LEFT);
		}
		universalStatement.state(variables).appendGodelNumbers(godelBuilder);
		for (int i = 0; i < variables.length; i++)
			godelBuilder.push(GodelNumberSymbols.RIGHT);
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
