package bran.tree.compositions.statements.special.quantifier;

import bran.combinatorics.Counter;
import bran.tree.compositions.sets.regular.var.WithVariableSet;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.sets.regular.FiniteSet;
import bran.tree.Holder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.tree.compositions.statements.StatementDisplayStyle.statementStyle;

public class ExistentialStatement<I, E extends Holder<I>> extends QuantifiedStatement<I, E> {

	public static final String[] thereExistsSymbols = { "\u2203", "\u2203", "\u2203", "\u2203" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208" };

	public ExistentialStatement(QuantifiedStatementArguments<E> universalStatement, Set<I> domain, boolean proven, E... variables) {
		super(universalStatement, domain, proven, variables);
	}

	public ExistentialStatement(QuantifiedStatementArguments<E> universalStatement, WithVariableSet<I, E> domain, boolean proven, E... variables) {
		super(universalStatement, (Set<I>) domain, proven, variables);
	}

	public boolean exhaustiveProof() {
		if (domain instanceof FiniteSet finiteSet) {
			List<I> choices = finiteSet.stream().toList();
			Counter counter = new Counter(argumentSize, choices.size());
			while (counter.hasNext()) {
				int[] count = counter.next();
				for (int i = 0; i < argumentSize; i++)
					statementParameters[i].set(choices.get(count[i]));
				if (statement.truth())
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
					statementParameters[i].set(choices.get(count[i]));
				sb.append(Arrays.stream(statementParameters)
											   .map(v -> v.toString() + " = " + v.get())
											   .collect(Collectors.joining(", ", "for ", ", ")));
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
			default -> thereExistsSymbols[statementStyle.index()];
		} + Arrays.stream(statementParameters)
				  .map(stringMapper)
				  .collect(Collectors.joining(",")) + switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		} + domain + "|" + statement;
	}

	@Override
	public String toFullString() { // TODO, check if it's a Composition
		return toString(Object::toString);
	}

	@Override
	public String toString() {
		return toString(Object::toString);
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
		for (final E variable : statementParameters) {
			godelBuilder.push(GodelNumberSymbols.LEFT);
			godelBuilder.push(GodelNumberSymbols.EACH);
			godelBuilder.push(godelBuilder.getVar(variable));
			godelBuilder.push(GodelNumberSymbols.RIGHT);
			godelBuilder.push(GodelNumberSymbols.LEFT);
		}
		statement.appendGodelNumbers(godelBuilder);
		for (int i = 0; i < statementParameters.length; i++)
			godelBuilder.push(GodelNumberSymbols.RIGHT);
	}

	@Override
	public Statement negation() {
		return new UniversalStatement<>(e -> statement.not(), domain, proven, statementParameters);
	}

}
