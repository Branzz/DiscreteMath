package bran.tree.compositions.statements.special.quantifier;

import bran.tree.Holder;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.SpecialStatement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <I> what E holds
 * @param <E> element type
 */
public abstract class QuantifiedStatement<I, E extends Holder<I>> extends SpecialStatement {

	protected final int argumentSize;
	// protected final QuantifiedStatementArguments<E> statement; // TODO migrate to just a statement
	protected final Statement statement;
	protected final E[] statementParameters;
	protected final Set<I> domain;
	protected boolean proven;

	public QuantifiedStatement(Statement statement, Set<I> domain, boolean proven, E... variables) {
		this.argumentSize = variables.length;
		this.statement = statement;
		this.statementParameters = variables;
		this.domain = domain;
		this.proven = proven;
	}

	public QuantifiedStatement(QuantifiedStatementArguments<E> statement, Set<I> domain, boolean proven, E... variables) {
		this(statement.state(variables), domain, proven, variables);
	}

	@Override
	protected boolean getTruth() {
		// return proven;
		return exhaustiveProof();
	}

	public abstract boolean exhaustiveProof();

	public abstract String exhaustiveProofString();

	@Override
	public List<Statement> getChildren() {
		List<Statement> current = new ArrayList<>();
		current.add(this);
		current.addAll(statement.getChildren());
		return current;
	}

	@Override
	public List<VariableStatement> getVariables() {
		return statement.getVariables();
	}

}
