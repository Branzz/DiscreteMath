package bran.logic.statements;

import bran.exceptions.VariableExpressionException;
import bran.logic.TruthTable;
import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.LogicalOperator;
import bran.logic.statements.special.ConditionalStatement;
import bran.logic.statements.special.QuantifiedStatementArguments;
import bran.logic.statements.special.UniversalStatement;
import bran.mathexprs.Equation;
import bran.sets.Set;
import bran.sets.numbers.godel.GodelBuilder;
import bran.tree.Composition;
import bran.tree.Equivalable;
import bran.tree.Holder;

import java.util.List;

import static bran.logic.statements.operators.LogicalOperator.*;
import static java.util.Collections.emptyList;

public abstract class Statement extends Composition implements Equivalable<Statement> {

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
	public int compareTo(Composition composition) {
		if (composition instanceof Statement statement) {
			if (comparisonType == 0)
				return logicallyEquivalentTableTo(statement) ? 0 : 1;
			else if (comparisonType == 1)
				return logicallyEquivalentTreeMatches(statement) ? 0 : 1;
			else {
				return logicallyEquivalentTreeMatches(statement) ? 0 : logicallyEquivalentTableTo(statement) ? 0 : 1;
			}
		}
		return -1;
	}

	public static Statement empty() {
		return Statement.emptyStatement;
	}

	public abstract List<Statement> getChildren();

	public abstract void appendGodelNumbers(final GodelBuilder godelBuilder);

// 	@Deprecated
// 	public void simplify() { // another variant where it compares statements with other expressions (p, ~p, t, c)
// 		boolean[] laws = new boolean[13];
// 		for (int i = 0; i < laws.length; i++)
// 			laws[i] = true;
// 		simplify(laws);
// 	}
//
// 	@Deprecated
// 	public void simplify(boolean... lawsInput) {
// //		boolean equivalence (check if an entire statement is equal, not that it is written the same), boolean associative,
// //		boolean distributive, boolean negation, boolean doubleNegation,
// //		boolean identityIdempotent, boolean universalBound, boolean useDeMorgans, boolean absorption,
// //		boolean negatateConstants;
//
// 		// boolean[] laws = Arrays.copyOf(lawsInput, 9);
// 		boolean[] laws = new boolean[9];
// 		for (int i = 0; i < lawsInput.length && i < laws.length; i++)
// 			laws[i] = lawsInput[i];
//
// 		 LineStatement root = new LineStatement(this);
//
// 		if (laws[4])
// 			root.checkDoubleNegationLaw();
// 		if (laws[0])
// 			root.checkNegateConstantsLaw();
// 		if (laws[1])
// 			root.checkNegationLaw();
//
// 		if (laws[6])
// 			root.checkAbsorptionLaw();
// 		if (laws[7])
// 			root.checkDistributiveLaw();
//
// 		if (laws[2])
// 			root.checkIdempotentLaw();
// 		if (laws[3])
// 			root.checkIdentityUniversalBoundLaw();
//
// //		if (laws[5])
// //			root.checkDeMorgansLaw();
//
// 		if (laws[8])
// 			root.checkAssociativeLaw(); //needs multi child statement?
//
// 		/*
// 		 * decide order:
// 		 *
// 		 * doubleNegation negatateConstants
// 		 * negation
// 		 *
// 		 * universalBound idempotent|identity
// 		 *
// 		 * useDeMorgans {
// 		 * absorption
// 		 * distribution
// 		 * } if same as before no effect (are there flip flop cycles?)
// 		 * associative
// 		 */
//
// 	}

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

/*
	protected abstract boolean checkNegateConstantsLaw();

	protected abstract boolean checkNegationLaw();

	protected abstract boolean checkIdempotentLaw();

	protected abstract boolean checkIdentityUniversalBoundLaw();

	protected abstract boolean checkDoubleNegationLaw();

	protected abstract boolean checkDeMorgansLaw();

	protected abstract boolean checkAbsorptionLaw();

	protected abstract boolean checkExtendedAbsorptionLaw(); //return true if action happened? then loop until false? infinite loopable?

	protected abstract boolean checkDistributiveLaw();

	protected abstract boolean checkAssociativeLaw();
*/

//	public Statement doubleNegationStatement() {
//		return null;
//	}

	public static Statement operationOf(LogicalOperator o, Statement... s) {
		if (s.length == 0)
			throw new VariableExpressionException();
		if (s.length == 1)
			return s[0];
		OperationStatement combinedStatements = new OperationStatement(s[0], o, s[1]);
		for (int i = 2; i < s.length; i++)
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
		return new LineStatement(s, LineOperator.NOT);
	}

	public static LineStatement selfOf(Statement s) {
		return new LineStatement(s, LineOperator.CONSTANT);
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
		return new LineStatement(this, LineOperator.NOT);
	}

	public LineStatement self() {
		return new LineStatement(this, LineOperator.CONSTANT);
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
		@Override public boolean equals(final Object s)				 { return false; }
		@Override public Statement simplified()						 { return empty(); }
		@Override public void appendGodelNumbers(final GodelBuilder godelBuilder) { }
		@Override protected boolean isConstant()					 { return false; }
		@Override protected boolean getTruth()						 { return false; }
		@Override public List<Statement> getChildren()				 { return emptyList(); }
		@Override public List<VariableStatement> getVariables()		 { return emptyList(); }
		@Override public String toFullString()							 { return "()"; }
		@Override public String toString()				 { return "()"; }
	// @Override public Statement clone()							 { return emptyStatement; }
	// 	@Override protected boolean checkNegateConstantsLaw()		 { return false; }
	// 	@Override protected boolean checkNegationLaw()				 { return false; }
	// 	@Override protected boolean checkIdempotentLaw()			 { return false; }
	// 	@Override protected boolean checkIdentityUniversalBoundLaw() { return false; }
	// 	@Override protected boolean checkDoubleNegationLaw()		 { return false; }
	// 	@Override protected boolean checkDeMorgansLaw()				 { return false; }
	// 	@Override protected boolean checkAbsorptionLaw()			 { return false; }
	// 	@Override protected boolean checkExtendedAbsorptionLaw()	 { return false; }
	// 	@Override protected boolean checkDistributiveLaw()			 { return false; }
	// 	@Override protected boolean checkAssociativeLaw()			 { return false; }
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

	//	public Statement realNot() {
//		return new LineStatement(this).realNot();
//	}

//	public Statement(String statement) {
//	// start a list of variables
//	// recursive? by the paren? add that back to a overall list. goal is to make an object
//	// multi symbol support? leftness in Operator symbols determines priority operation type?
//	ArrayList<VariableStatement> variables = new ArrayList<VariableStatement>();
//	int depth = 0;
//	String part = "";
////	Operator operator;
//	for (int i = 0; i < statement.length(); i++) {
//		
//		switch (statement.charAt(i)) {
//		case '(':
//			depth += 2; //++
//		case ')':
//			depth--;
//		case ' ':
//			int j1 = 0;
//			while (j1 < Operator.values().length && !part.equals(Operator.values()[j1].toString()))
//				j1++;
//			int j2 = 0;
//			if (j1 == Operator.values().length)
//				while (j2 < variables.size() && !part.equals(variables.get(j2).name))
//					j2++;
//			if (j2 == variables.size())
//				variables.add(new VariableStatement(part));
//
//			part = "";
//			break;
//		default:
//			part += statement.charAt(i);
//		}
//		}
//
//	}
//	public Statement(String statement) {
//	// start a list of variables
//	// recursive? by the paren? add that back to a overall list. goal is to make an object
//	// multi symbol support? leftness in Operator symbols determines priority operation type?
//	ArrayList<VariableStatement> variables = new ArrayList<VariableStatement>();
//	int depth = 0;
//	String part = "";
////	Operator operator;
//	for (int i = 0; i < statement.length(); i++) {
//		
//		switch (statement.charAt(i)) {
//		case '(':
//			depth += 2; //++
//		case ')':
//			depth--;
//		case ' ':
//			int j1 = 0;
//			while (j1 < Operator.values().length && !part.equals(Operator.values()[j1].toString()))
//				j1++;
//			int j2 = 0;
//			if (j1 == Operator.values().length)
//				while (j2 < variables.size() && !part.equals(variables.get(j2).name))
//					j2++;
//			if (j2 == variables.size())
//				variables.add(new VariableStatement(part));
//
//			part = "";
//			break;
//		default:
//			part += statement.charAt(i);
//		}
//		}
//
//	}
//	private Statement getStatement(String statement, ArrayList<VariableStatement> variables) {
//		boolean expectingVariable = true;
//		String part = "";
//		for (int i = 0; i < statement.length(); i++) {
//			switch (statement.charAt(i)) {
//			case ' ':
//				int j1 = 0;
//				while (j1 < Operator.values().length && !part.equals(Operator.values()[j1].toString()))
//					j1++;
//				int j2 = 0;
//				if (j1 == Operator.values().length) // If it's not an operator
//					while (j2 < variables.size() && !part.equals(variables.get(j2).name))
//						j2++;
//				else // It is an operator
//					if (expectingVariable)
//						; // Exception
//					else
//						;
//				if (j2 == variables.size()) // Variable did not exist yet
//					variables.add(new VariableStatement(part));
//
//				part = "";
//				break;
//			default:
//				part += statement.charAt(i);
//			}
//		}
//
////		if (statements.length == 0)
////			throw new VariableExpressionException();
////		if (statements.length == 1) {
////			left = statements[0];
////			right = statements[0];
////		}
////		else {
////		OperationStatement combinedStatements = new OperationStatement(statements[0], operator, statements[1]);
////		for (int i = 2; i < statements.length; i++)
////			combinedStatements = new OperationStatement(combinedStatements.clone(), operator, statements[i]);
////		 combinedStatements;
//
//		return null;
//	}
//		boolean symbolsLeft = false;
//		int j = 0;
//		while (symbolsLeft) {
//			for (Operator o : Operator.values()) {
//				
//			}
//			j++;
//		}

}
