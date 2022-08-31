package bran.tree.compositions.statements.special.quantifier;

import bran.combinatorics.Counter;
import bran.tree.compositions.sets.regular.var.WithVariableSet;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.Holder;
import bran.tree.compositions.sets.regular.FiniteSet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bran.tree.compositions.statements.StatementDisplayStyle.*;

public class UniversalStatement<I, E extends Holder<I>> extends QuantifiedStatement<I, E> { // TODO Equivalable???

	public static final String[] forAllSymbols = { "\u2200", "\u2200", "\u2200", "\u2200" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208" };

	public UniversalStatement(QuantifiedStatementArguments<E> universalStatement, Set<I> domain, boolean proven, E... variables) {
		super(universalStatement, domain, proven, variables);
	}

	public UniversalStatement(QuantifiedStatementArguments<E> universalStatement, WithVariableSet<I, E> domain, boolean proven, E... variables) {
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
				if (!statement.truth())
					return false;

			}
			return true;
		}
		return false;
	}

	public String exhaustiveProofString() {
		if (domain instanceof FiniteSet finiteSet) {
			List<I> choices = finiteSet.stream().toList();

			StringBuilder sb = new StringBuilder();
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
				if (!statement.truth())
					return sb.append(", which is false. (invalid)").toString();
				else if (counter.hasNext())
					sb.append(", which is true... continuing\n");
			}
			return sb.append("\nwhich are all true. (valid)").toString();
		}
		return "domain too large";
	}

	private String toString(Function<E, String> stringMapper, Function<Statement, String> statementStringMapper) {
		StringBuilder sb = new StringBuilder();
		return
		sb.append(switch (statementStyle) {
			case NAME -> "For all ";
			case LOWERCASE_NAME -> "for all ";
			default -> forAllSymbols[statementStyle.index()];
		})
		  .append(Arrays.stream(statementParameters).map(stringMapper).collect(Collectors.joining(",")))
		  .append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		})
		  .append(domain)
		  .append(", ")
		  .append(statementStringMapper.apply(statement)).toString();
	}

	@Override
	public String toFullString() {
		return toString(Object::toString, Statement::toFullString);
	}

	@Override
	public String toString() {
		return toString(Object::toString, Statement::toString);
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		// TODO return truth?
		return new UniversalStatement<>(e -> statement.simplified(), domain, proven, statementParameters);
	}

	@Override
	public void appendGodelNumbers(GodelBuilder godelBuilder) {
		LineOperator.NOT.of(negation()).appendGodelNumbers(godelBuilder); // No Godel Universal Symbol, so convert to Existential
	}

	@Override
	public Statement negation() {
		return new ExistentialStatement<>(e -> statement.not(), domain, proven, statementParameters);
	}

}
