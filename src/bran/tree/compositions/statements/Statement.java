package bran.tree.compositions.statements;

import bran.exceptions.ArrayUnpopulatedException;
import bran.tree.compositions.sets.regular.SpecialSet;
import bran.tree.compositions.sets.regular.SpecialSetType;
import bran.tree.compositions.sets.regular.var.WithVariableSet;
import bran.tree.compositions.statements.operators.LineOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.ConditionalStatement;
import bran.tree.compositions.statements.special.quantifier.ExistentialStatement;
import bran.tree.compositions.statements.special.quantifier.QuantifiedStatement;
import bran.tree.compositions.statements.special.quantifier.QuantifiedStatementArguments;
import bran.tree.compositions.statements.special.quantifier.UniversalStatement;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.compositions.Composition;
import bran.tree.Equivalable;
import bran.tree.Holder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static bran.tree.compositions.statements.operators.LogicalOperator.*;
import static java.util.Collections.emptyList;

public abstract class Statement implements Composition, Equivalable<Statement>, Comparable<Statement> {

	private static byte comparisonType = 0; // 0: brute force table, 1: check full tree

	public Statement() { }

	public boolean isConstant() {
		return false;
	}

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
			throw new ArrayUnpopulatedException();
		Statement combinedStatements = s[0];
		for (int i = 1; i < s.length; i++)
			combinedStatements = new OperationStatement(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	public OperationStatement operation(LogicalOperator o, Statement... s) {
		if (s.length == 0)
			throw new ArrayUnpopulatedException();
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

	public Statement bijection(Statement s) {
		return xnorOf(s);
	}

	public Statement iff(Statement s) {
		return xnorOf(s);
	}

	public static Statement impliesOf(Statement r, Statement s) {
		return operationOf(IMPLIES, r, s);
	}

	public Statement not() {
		return new LineStatement(LineOperator.NOT, this);
	}

	// public Statement realNot() {
	// 	return new LineStatement(this).realNot();
	// }

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
		@Override public boolean equivalentTo(Statement other) { return this == other; }
		@Override public boolean equals(Object s)				 { return this == s; }
		@Override public Statement simplified()						 { return empty(); }
		@Override public void appendGodelNumbers(GodelBuilder godelBuilder) { }
		@Override public boolean isConstant()						 { return false; }
		@Override protected boolean getTruth()						 { return false; }
		@Override public List<Statement> getChildren()				 { return emptyList(); }
		@Override public List<VariableStatement> getVariables()		 { return emptyList(); }
		@Override public String toFullString()						 { return "()"; }
	// @Override public Statement clone()							 { return emptyStatement; }
	};

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

	// Imperative style assistance classes

	// Quantified Statement

	public static <I, E extends Holder<I>, S extends Set<I>>
	QuantifiedBuilder<I, E, UniversalStatement<I, E>, S>.UniversalStatementSet forAll(E... variables) {
		return new QuantifiedBuilder<I, E, UniversalStatement<I, E>, S>(variables).new UniversalStatementSet();
	}

	public static <I, E extends Holder<I>, S extends Set<I>>
	QuantifiedBuilder<I, E, ExistentialStatement<I, E>, S>.ExistentialStatementSet thereExists(E... variables) {
		return new QuantifiedBuilder<I, E, ExistentialStatement<I, E>, S>(variables).new ExistentialStatementSet();
	}

	public static class QuantifiedBuilder<I, E extends Holder<I>, T extends QuantifiedStatement<I, E>, S extends Set<I>> {

		protected Supplier<T> quantifierSupplier;
		protected E[] variables;
		protected S domain;
		protected boolean proven;
		protected WithVariableSet<I, E> varSetDomain;
		protected QuantifiedStatementArguments<E> statement;

		QuantifiedBuilder(E[] variables) {
			this.variables = variables;
		}

		@FunctionalInterface
		private interface QuantifierSupplier<I, E extends Holder<I>, T extends QuantifiedStatement<I, E>, S extends Set<I>> {
			T create(QuantifiedStatementArguments<E> statement, S domain, boolean proven, E[] variables);
		}

		public class UniversalStatementSet {
			public UniversalStatementThat in(S domain) {
				QuantifiedBuilder.this.domain = domain;
				return new UniversalStatementThat();
			}
			public UniversalStatementVarSetThat in(WithVariableSet<I, E> domain) {
				QuantifiedBuilder.this.varSetDomain = domain;
				return new UniversalStatementVarSetThat();
			}
			public UniversalStatementThat in(SpecialSetType specialSetType) {
				QuantifiedBuilder.this.domain = (S) new SpecialSet(specialSetType);
				return new UniversalStatementThat();
			}
		}

		public class ExistentialStatementSet {
			public ExistentialStatementThat in(S domain) {
				QuantifiedBuilder.this.domain = domain;
				return new ExistentialStatementThat();
			}
			public ExistentialStatementVarSetThat in(WithVariableSet<I, E> domain) {
				QuantifiedBuilder.this.varSetDomain = domain;
				return new ExistentialStatementVarSetThat();
			}
			public ExistentialStatementThat in(SpecialSetType specialSetType) {
				QuantifiedBuilder.this.domain = (S) new SpecialSet(specialSetType);
				return new ExistentialStatementThat();
			}
		}

		public abstract class QuantifiedStatementThat {
			public QuantifiedStatementProven thatEach(QuantifiedStatementArguments<E> statement) {
				QuantifiedBuilder.this.statement = statement;
				quantifierSupplier = getQuantifierSupplier();
				return new QuantifiedStatementProven();
			}
			protected QuantifiedStatementProven that(Function<E, Statement> oneArgQSA) {
				return thatEach(es -> oneArgQSA.apply(es[0]));
			}
			protected QuantifiedStatementProven that(BiFunction<E, S, Statement> oneArgQSAWithSet) {
				return thatEach(es -> oneArgQSAWithSet.apply(es[0], domain));
			}
			protected abstract Supplier<T> getQuantifierSupplier();
		}

		public abstract class QuantifiedStatementVarSetThat extends QuantifiedStatementThat {
			public QuantifiedStatementProven thatVarSet(BiFunction<E, WithVariableSet<I, E>, Statement> oneArgQSAWithSet) {
				return thatEach(es -> oneArgQSAWithSet.apply(es[0], varSetDomain));
			}
		}

		public abstract class QuantifiedStatementSetThat extends QuantifiedStatementThat {
		}

		public class ExistentialStatementThat extends QuantifiedStatementSetThat {
			@Override protected Supplier<T> getQuantifierSupplier() {
				return () -> (T) new ExistentialStatement<>(statement, domain, proven, variables);
			}
			public QuantifiedStatementProven suchThat(Function<E, Statement> oneArgQSA) {
				return that(oneArgQSA);
			}
			public QuantifiedStatementProven suchThat(BiFunction<E, S, Statement> oneArgQSAWithSet) {
				return that(oneArgQSAWithSet);
			}
		}

		public class UniversalStatementThat extends QuantifiedStatementSetThat {
			@Override protected Supplier<T> getQuantifierSupplier() {
				return () -> (T) new ExistentialStatement<>(statement, domain, proven, variables);
			}
			public QuantifiedStatementProven itHolds(Function<E, Statement> oneArgQSA) {
				return that(oneArgQSA);
			}
			public QuantifiedStatementProven itHolds(BiFunction<E, S, Statement> oneArgQSAWithSet) {
				return that(oneArgQSAWithSet);
			}
		}

		public class ExistentialStatementVarSetThat extends QuantifiedStatementVarSetThat {
			@Override protected Supplier<T> getQuantifierSupplier() {
				return () -> (T) new ExistentialStatement<>(statement, varSetDomain, proven, variables);
			}
			public QuantifiedStatementProven suchThat(Function<E, Statement> oneArgQSA) {
				return that(oneArgQSA);
			}
			public QuantifiedStatementProven suchThat(BiFunction<E, S, Statement> oneArgQSAWithSet) {
				return that(oneArgQSAWithSet);
			}
		}

		public class UniversalStatementVarSetThat extends QuantifiedStatementVarSetThat {
			@Override protected Supplier<T> getQuantifierSupplier() {
				return () -> (T) new UniversalStatement<>(statement, varSetDomain, proven, variables);
			}
			public QuantifiedStatementProven itHolds(Function<E, Statement> oneArgQSA) {
				return that(oneArgQSA);
			}
			public QuantifiedStatementProven itHolds(BiFunction<E, S, Statement> oneArgQSAWithSet) {
				return that(oneArgQSAWithSet);
			}
		}

		public class QuantifiedStatementProven {
			public T proven() { return create(true); }
			public T unProven() { return create(false); }
			protected T create(boolean proven) {
				QuantifiedBuilder.this.proven = proven;
				return quantifierSupplier.get();
			}
		}

	}

	private static class Test {
		public static void main(String[] args) {
			new X<String, EVS>(new EVS());

		}
		static class W<T> {
			W(A<T> a) {

			}
		}
		static class X<T, Z extends A<T>> {
			X(Z z) {
				new W<>(z);
			}
		}
		static class A<T> { void method(T t) {} }
		static class VS<T> extends A<T> {}
		static class EVS extends VS<String> {}
	}

}
