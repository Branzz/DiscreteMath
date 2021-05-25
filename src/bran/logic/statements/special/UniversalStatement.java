package bran.logic.statements.special;

import bran.combinatorics.Counter;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.sets.Set;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Equivalable;
import bran.tree.Holder;
import bran.sets.FiniteSet;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static bran.logic.statements.StatementDisplayStyle.*;

public class UniversalStatement<I, E extends Holder<I> & Equivalable<? super E>> extends QuantifiedStatement {

	private final int argumentSize;
	private final QuantifiedStatementArguments<E> statement; // TODO migrate to just a statement
	private final E[] variables;
	private final Set domain;
	private boolean proven;

	public static final String[] forAllSymbols = { "\u2200", "\u2200", "\u2200", "\u2200" };
	public static final String[] inSetSymbols  = { "\u2208", "\u2208", "\u2208", "\u2208" };

	public UniversalStatement(final QuantifiedStatementArguments<E> statement, final Set domain, final boolean proven, final E... variables) {
		this.argumentSize = variables.length;
		this.statement = statement;
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
				if (!statement.state(variables)
							  .truth())
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
					variables[i].set(choices.get(count[i]));
				sb.append(Arrays.stream(variables)
								.map(v -> v.toString() + " = " + v.get())
								.collect(Collectors.joining(", ", "for ", ", ")));
				Statement statement = this.statement.state(variables);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(switch (statementStyle) {
			case NAME -> "For all ";
			case LOWERCASE_NAME -> "for all ";
			default -> forAllSymbols[statementStyle.index()];
		});
		sb.append(Arrays.stream(variables).map(Object::toString).collect(Collectors.joining(",")));
		sb.append(switch (statementStyle) {
			case NAME, LOWERCASE_NAME -> " in the set of ";
			default -> inSetSymbols[statementStyle.index()];
		});
		return sb.append(domain).append(", ").append(statement.state(variables)).toString();
	}

	@Override
	public boolean equals(final Object s) {
		return false;
	}

	@Override
	public Statement simplified() {
		return new UniversalStatement<>(e -> statement.state(e).simplified(), domain, proven, variables );
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> godelNumbers, final GodelVariableMap variables) {
		statement.state(this.variables).not().simplified().appendGodelNumbers(godelNumbers, variables);
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
		return new ExistentialStatement<>(e -> statement.state(e).not(), domain, proven, variables);
	}

}
