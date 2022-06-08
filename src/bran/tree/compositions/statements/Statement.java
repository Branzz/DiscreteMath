package bran.tree.compositions.statements;

import bran.exceptions.VariableExpressionException;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.ConditionalStatement;
import bran.tree.compositions.statements.special.quantifier.QuantifiedStatementArguments;
import bran.tree.compositions.statements.special.quantifier.UniversalStatement;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.Composition;
import bran.tree.Equivalable;
import bran.tree.Holder;

import java.util.List;

import static bran.tree.compositions.statements.operators.LogicalOperator.*;
import static java.util.Collections.emptyList;

public abstract class Statement extends Composition implements Equivalable<Statement>, Comparable<Statement> {

	private static byte comparisonType = 0; // 0: brute force table, 1: check full tree

	public Statement() { }

	protected abstract boolean isConstant();

	protected abstract boolean getTruth();

	public boolean truth() {
		return getTruth();
	}

	@Override
	public abstract String toFullString();

	@Override
	public String toString() {
		return toFullString();
	}

	public abstract List<VariableStatement> getVariables();

	@Override
	public abstract Statement simplified();

	// public abstract Statement clone();

	@Override
	public boolean equivalentTo(final Statement other) {
		return truth() == other.truth();
	}

	@Override
	public int compareTo(Statement statement) {
		if (comparisonType == 0)
			return logicallyEquivalentTableTo(statement) ? 0 : 1;
		else if (comparisonType == 1)
			return logicallyEquivalentTreeMatches(statement) ? 0 : 1;
		else {
			return logicallyEquivalentTreeMatches(statement) ? 0 : logicallyEquivalentTableTo(statement) ? 0 : 1;
		}
	}

	public static Statement empty() {
		return Statement.emptyStatement;
	}

	public abstract List<Statement> getChildren();

	@Override
	public void replaceAll(final Composition original, final Composition replacement) {
		// do nothing
	}

	public abstract void appendGodelNumbers(final GodelBuilder godelBuilder);

//	public boolean logicallyEquivalentTo(Statement statement) {
//		//check if structure matches
//		//then brute force / use laws
//		return false;
//	}

	public boolean logicallyEquivalentTreeMatches(Statement statement) {
		return this.equals(statement);
	}

	public boolean logicallyEquivalentTableTo(Statement statement) {
		List<VariableStatement> variables = getVariables(); // Get all possible variables used
		for (VariableStatement v : statement.getVariables())
			if (!variables.contains(v))
				variables.add(v);
		for (int i = 0; i < variables.size();) // Remove t or c 's
			if (variables.get(i).isConstant())
				variables.remove(i);
			else
				i++;
		for (int i = 0; i < Math.pow(2, variables.size()); i++) {
			for (int j = 0; j < variables.size(); j++)
				variables.get(variables.size() - j - 1).setValue((i >> j & 1) == 0);
			if (getTruth() != statement.getTruth())
				return false;
		}
		return true;
	}

	public static Statement operationOf(LogicalOperator o, Statement... s) {
		if (s.length == 0)
			throw new VariableExpressionException();
		Statement combinedStatements = s[0];
		for (int i = 1; i < s.length; i++)
			combinedStatements = new OperationStatement(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	public OperationStatement operation(LogicalOperator o, Statement... s) {
		if (s.length == 0)
			throw new VariableExpressionException();
		OperationStatement combinedStatements = new OperationStatement(this, o, s[0]);
		for (int i = 1; i < s.length; i++)
			combinedStatements = new OperationStatement(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	public static LineStatement notOf(Statement s) {
		return new LineStatement(LineOperator.NOT, s);
	}

	public static LineStatement selfOf(Statement s) {
		return new LineStatement(LineOperator.CONSTANT, s);
	}
	
	public static Statement andOf(Statement... s) {
		return operationOf(AND, s);
	}

	public static Statement orOf(Statement... s) {
		return operationOf(OR, s);
	}

	public static Statement nandOf(Statement... s) {
		return operationOf(NAND, s);
	}

	public static Statement norOf(Statement... s) {
		return operationOf(NOR, s);
	}

	public static Statement xorOf(Statement... s) {
		return operationOf(XOR, s);
	}

	public static Statement xnorOf(Statement... s) {
		return operationOf(XNOR, s);
	}

	public static Statement impliesOf(Statement r, Statement s) {
		return operationOf(IMPLIES, r, s);
	}

	public Statement not() {
		return new LineStatement(LineOperator.NOT, this);
	}

	public LineStatement self() {
		return new LineStatement(LineOperator.CONSTANT, this);
	}
	
	public OperationStatement and(Statement... s) {
		return operation(AND, s);
	}

	public OperationStatement or(Statement... s) {
		return operation(OR, s);
	}

	public OperationStatement nand(Statement... s) {
		return operation(NAND, s);
	}

	public OperationStatement nor(Statement... s) {
		return operation(NOR, s);
	}

	public OperationStatement xor(Statement... s) {
		return operation(XOR, s);
	}

	public OperationStatement xnor(Statement... s) {
		return operation(XNOR, s);
	}

	public OperationStatement implies(Statement... s) {
		return operation(IMPLIES, andOf(s));
	}

	public OperationStatement revImplies(Statement s) {
		return operation(REV_IMPLIES, s);
	}

	public Statement then(final Statement conclusion) {
		return new ConditionalStatement(this, conclusion);
	}

	// public Statement equates(final Statement right) {
	// 	return new Equation(this, right);
	// }

	private static final Statement emptyStatement = new Statement() {
		@Override public boolean equivalentTo(final Statement other) { return this == other; }
		@Override public boolean equals(final Object s)				 { return this == s; }
		@Override public Statement simplified()						 { return empty(); }
		@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { }
		@Override protected boolean isConstant()					 { return false; }
		@Override protected boolean getTruth()						 { return false; }
		@Override public List<Statement> getChildren()				 { return emptyList(); }
		@Override public List<VariableStatement> getVariables()		 { return emptyList(); }
		@Override public String toFullString()							 { return "()"; }
		@Override public String toString()				 { return "()"; }
	// @Override public Statement clone()							 { return emptyStatement; }
	};

	// Imperative style assistance classes

	public static <I, E extends Composition & Holder<I> & Equivalable<? super E>> UniversalStatementVariables<I, E> forAll(E... variables) {
		return new UniversalStatementVariables<>(variables);
	}

	public String getFullTable() {
		return TruthTable.getFullTable(this);
	}

	public String getTable() {
		return TruthTable.getTable(this);
	}

	public boolean equalsNot(final Statement other) {
		return this instanceof LineStatement thisL && thisL.getChild().equals(other)
				|| other instanceof LineStatement otherL && otherL.getChild().equals(this);
	}

	public static record UniversalStatementVariables<I, E extends Composition & Holder<I> & Equivalable<? super E>> (E... variables) {
		public UniversalStatementDomainStatement<I, E> in(Set domain, QuantifiedStatementArguments<E> statement) {
			return new UniversalStatementDomainStatement<>(domain, statement, variables);
		}
		// public UniversalStatementDomainStatement<I, E> in(SpecialSet domain, QuantifiedStatementArguments<E> statement) {
		// 	return new UniversalStatementDomainStatement<>(domain, statement, variables);
		// }
	}

	public static record UniversalStatementDomainStatement<I, E extends Composition & Holder<I> & Equivalable<? super E>>
			(Set domain, QuantifiedStatementArguments<E> statement, E... variables) {
		public UniversalStatement<I, E> proven() {
			return new UniversalStatement<>(statement, domain, true, variables);
		}
		public UniversalStatement<I, E> unProven() {
			return new UniversalStatement<>(statement, domain, false, variables);
		}
	}

	// public Statement realNot() {
	// 	return new LineStatement(this).realNot();
	// }

}
