package bran.logic.statements;

import java.util.List;

import bran.exceptions.VariableExpressionException;
import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.Operator;
import bran.logic.statements.special.ConditionalStatement;
import bran.logic.tree.Equivalable;
import bran.logic.tree.TreePart;
import bran.mathexprs.Equation;

import static java.util.Collections.*;

public abstract class Statement implements Comparable<Statement>, TreePart, Equivalable<Statement> {

	private static byte comparisonType = 0; // 0: brute force table, 1: check full tree

	protected abstract boolean isConstant();

	protected abstract boolean getTruth();

	public boolean truth() {
		return getTruth();
	}

	public abstract List<Statement> getChildren();

	public abstract List<VariableStatement> getVariables();

	public abstract String toString();

	// public abstract Statement clone();

	@Override
	public boolean equivalentTo(final Statement other) {
		return truth() == other.truth();
	}

	public boolean equals(Object s) {
		return this == s;
	}

	public int compareTo(Statement statement) {
		if (statement == null)
			return -1;
		if (comparisonType == 0)
			return logicallyEquivalentTableTo(statement) ? 0 : 1;
		else if (comparisonType == 1)
			return logicallyEquivalentTreeMatches(statement) ? 0 : 1;
		else {
			return logicallyEquivalentTreeMatches(statement) ? 0 : logicallyEquivalentTableTo(statement) ? 0 : 1;
		}
	}

	public Statement() {}

	public void simplify() { // another variant where it compares statements with other expressions (p, ~p, t, c)
		boolean[] laws = new boolean[13];
		for (int i = 0; i < laws.length; i++)
			laws[i] = true;
		simplify(laws);
	}

	public void simplify(boolean... lawsInput) {
//		boolean equivalence (check if an entire statement is equal, not that it is written the same), boolean associative,
//		boolean distributive, boolean negation, boolean doubleNegation,
//		boolean identityIdempotent, boolean universalBound, boolean useDeMorgans, boolean absorption,
//		boolean negatateConstants;
		boolean[] laws = new boolean[9];
		for (int i = 0; i < lawsInput.length && i < laws.length; i++)
			laws[i] = lawsInput[i];

		 LineStatement root = new LineStatement(this);

		if (laws[4])
			root.checkDoubleNegationLaw();
		if (laws[0])
			root.checkNegateConstantsLaw();
		if (laws[1])
			root.checkNegationLaw();

		if (laws[6])
			root.checkAbsorptionLaw();
		if (laws[7])
			root.checkDistributiveLaw();

		if (laws[2])
			root.checkIdempotentLaw();
		if (laws[3])
			root.checkIdentityUniversalBoundLaw();

//		if (laws[5])
//			root.checkDeMorgansLaw();

		if (laws[8])
			root.checkAssociativeLaw(); //needs multi child statement?

		/*
		 * decide order:
		 * 
		 * doubleNegation negatateConstants
		 * negation
		 * 
		 * universalBound idempotent|identity
		 * 					
		 * useDeMorgans {
		 * absorption
		 * distribution
		 * } if same as before no effect (are there flip flop cycles?)
		 * associative
		 */
		
	}

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

//	public Statement doubleNegationStatement() {
//		return null;
//	}

	private static OperationStatement operationOf(Operator o, Statement... s) {
		if (s.length == 0)
			throw new VariableExpressionException();
		if (s.length == 1)
			return new OperationStatement(s[0], o, s[0]);
		OperationStatement combinedStatements = new OperationStatement(s[0], o, s[1]);
		for (int i = 2; i < s.length; i++)
			combinedStatements = new OperationStatement(combinedStatements, o, s[i]);
		return combinedStatements;
	}

	private OperationStatement operation(Operator o, Statement... s) {
		if (s.length < 0)
			throw new VariableExpressionException();
		if (s.length == 0)
			return new OperationStatement(this, o, this);
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
	
	public static OperationStatement andOf(Statement... s) {
		return operationOf(Operator.AND, s);
	}

	public static OperationStatement orOf(Statement... s) {
		return operationOf(Operator.OR, s);
	}

	public static OperationStatement nandOf(Statement... s) {
		return operationOf(Operator.NAND, s);
	}

	public static OperationStatement norOf(Statement... s) {
		return operationOf(Operator.NOR, s);
	}

	public static OperationStatement xorOf(Statement... s) {
		return operationOf(Operator.XOR, s);
	}

	public static OperationStatement xnorOf(Statement... s) {
		return operationOf(Operator.XNOR, s);
	}

	public static OperationStatement impliesOf(Statement r, Statement s) {
		return operationOf(Operator.IMPLIES, r, s);
	}

	public LineStatement not() {
		return new LineStatement(this, LineOperator.NOT);
	}

	public LineStatement self() {
		return new LineStatement(this, LineOperator.CONSTANT);
	}
	
	public OperationStatement and(Statement... s) {
		return operation(Operator.AND, s);
	}

	public OperationStatement or(Statement... s) {
		return operation(Operator.OR, s);
	}

	public OperationStatement nand(Statement... s) {
		return operation(Operator.NAND, s);
	}

	public OperationStatement nor(Statement... s) {
		return operation(Operator.NOR, s);
	}

	public OperationStatement xor(Statement... s) {
		return operation(Operator.XOR, s);
	}

	public OperationStatement xnor(Statement... s) {
		return operation(Operator.XNOR, s);
	}

	public OperationStatement implies(Statement s) {
		return operation(Operator.IMPLIES, s);
	}

	public Statement then(final Statement conclusion) {
		return new ConditionalStatement(this, conclusion);
	}

	public Statement equates(final Statement right) {
		return new Equation<>(this, right);
	}

	private static final Statement emptyStatement = new Statement() {
		@Override public boolean equivalentTo(final Statement other) { return this == other; }
		@Override protected boolean isConstant()					 { return false; }
		@Override protected boolean getTruth()						 { return false; }
		@Override public List<Statement> getChildren()				 { return emptyList(); }
		@Override public List<VariableStatement> getVariables()		 { return emptyList(); }
		@Override public String toString()							 { return "()"; }
	// @Override public Statement clone()							 { return emptyStatement; }
		@Override protected boolean checkNegateConstantsLaw()		 { return false; }
		@Override protected boolean checkNegationLaw()				 { return false; }
		@Override protected boolean checkIdempotentLaw()			 { return false; }
		@Override protected boolean checkIdentityUniversalBoundLaw() { return false; }
		@Override protected boolean checkDoubleNegationLaw()		 { return false; }
		@Override protected boolean checkDeMorgansLaw()				 { return false; }
		@Override protected boolean checkAbsorptionLaw()			 { return false; }
		@Override protected boolean checkExtendedAbsorptionLaw()	 { return false; }
		@Override protected boolean checkDistributiveLaw()			 { return false; }
		@Override protected boolean checkAssociativeLaw()			 { return false; }
	};

	public static Statement empty() {
		return emptyStatement;
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
