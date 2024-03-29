package bran.tree.compositions.statements.special;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.StatementOperation;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;

import java.util.Collections;
import java.util.List;

public class ConditionalStatement extends SpecialStatement {

	private final StatementOperation conditional;

	public ConditionalStatement(final Statement hypothesis, final Statement conclusion) {
		conditional = new StatementOperation(hypothesis, LogicalOperator.IMPLIES, conclusion);
	}

	@Override
	protected boolean getTruth() {
		return conditional.getTruth();
	}

	@Override
	public String toFullString() {
		return "if " + conditional.getLeft() + " then " + conditional.getRight();
	}

	@Override
	public String toString() {
		return "if " + conditional.getLeft().toString() + " then " + conditional.getRight().toString();
	}

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	@Override
	public Statement simplified() {
		final Statement simplifiedConditional = conditional.simplified();
		if (simplifiedConditional instanceof StatementOperation statementOperation && statementOperation.getOperator() == LogicalOperator.IMPLIES)
			return new ConditionalStatement(statementOperation.getLeft(), statementOperation.getRight());
		else
			return simplifiedConditional;
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		conditional.getLeft().appendGodelNumbers(godelBuilder);
		godelBuilder.push(GodelNumberSymbols.IF_THEN);
		conditional.getRight().appendGodelNumbers(godelBuilder);
	}

	@Override
	public List<Composition> getChildren() {
		return Collections.singletonList(conditional);
	}

	@Override
	public List<VariableStatement> getVariables() {
		return conditional.getVariables();
	}

	@Override
	public Statement negation() {
		return conditional.getLeft().or(conditional.getRight().not());
	}

	// @Override
	// public Statement clone() {
	// 	return new ConditionalStatement(conditional.getLeft().clone(), conditional.getRight().clone());
	// }

}
