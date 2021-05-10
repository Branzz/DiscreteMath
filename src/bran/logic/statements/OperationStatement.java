package bran.logic.statements;

import java.util.ArrayList;
import java.util.List;

import bran.logic.statements.operators.LineOperator;
import bran.logic.statements.operators.Operator;
import bran.logic.tree.Fork;

public class OperationStatement extends Statement implements Fork<Statement, Operator, Statement> { // Two Child

	private Statement left;
	private Operator operator;
	private Statement right;

	public OperationStatement(Statement left, Operator operator, Statement right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public boolean equals(Object s) {
		if (!(s instanceof OperationStatement))
			return false;
		OperationStatement s0 = (OperationStatement) s;
		return s0.getOperator().equals(operator) && left.equals(s0.getLeft()) && right.equals(s0.getRight());
	}

	public Statement getLeft() {
		return left;
	}

	public Operator getOperator() {
		return operator;
	}

	public Statement getRight() {
		return right;
	}

	// @Override
	// public OperationStatement create(final Statement left, final Operator function, final Statement right) {
	// 	return new OperationStatement(left, function, right);
	// }

	//	public OperationStatement(Operator operator, Statement... statements) {
//		if (statements.length == 0)
//			throw new VariableExpressionException();
//		if (statements.length == 1) {
//			left = statements[0];
//			right = statements[0];
//		}
//		else {
//		OperationStatement combinedStatements = new OperationStatement(statements[0], operator, statements[1]);
//		for (int i = 2; i < statements.length; i++)
//			combinedStatements = new OperationStatement(combinedStatements.clone(), operator, statements[i]);
//		 combinedStatements;
//
//		}
//		this.operator = operator;
//	}

//	public OperationStatement(boolean positivity, Statement left, Operator operator, Statement right) {
//		if (!positivity); //TODO
//		this.left = left;
//		this.operator = operator;
//		this.right = right;
//	}

	public boolean operate(boolean truth1, boolean truth2) {
		return operator.operate(truth1, truth2);
	}

	public static boolean operate(boolean truth1, Operator operator, boolean truth2) {
		return operator.operate(truth1, truth2);
	}

	public boolean isConstant() {
		return false;
	}

	public boolean getTruth() {
		return operate(left.getTruth(), right.getTruth());
	}

	public List<Statement> getChildren() {
		ArrayList<Statement> current = new ArrayList<>();
		current.add(this);
		current.addAll(left.getChildren());
		current.addAll(right.getChildren());
		return current;
	}

	public List<VariableStatement> getVariables() {
		ArrayList<VariableStatement> current = new ArrayList<>();
		current.addAll(left.getVariables());
		current.addAll(right.getVariables());
		return current;
	}

	public String toString() {
		return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
	}

	// @Override
	// public OperationStatement clone() {
	// 	return new OperationStatement(left.clone(), operator, right.clone());
	// }

	public boolean checkNegationLaw() {
		//see if self needs to be changed, if so, return true and parent will handle it
		//if not: check in children need to be handled, and handle them
		if (left.compareTo(right.not()) == 0)
			return true;
		if (left.checkNegationLaw())
			left = ((OperationStatement) left).negatedLawStatement();
		if (right.checkNegationLaw())
			right = ((OperationStatement) right).negatedLawStatement();
		return false;
	}

	@Override
	protected boolean checkIdempotentLaw() {
		if (left.isConstant() ^ right.isConstant())
			return true;
		if (left.checkNegationLaw())
			left = ((OperationStatement) left).negatedLawStatement();
		if (right.checkNegationLaw())
			right = ((OperationStatement) right).negatedLawStatement();
		return false;
	}

	@Override
	protected boolean checkIdentityUniversalBoundLaw() { //TODO this is all wrong
		if (left.compareTo(right) == 0)
			return true;
		if (left.checkNegationLaw())
			left = ((OperationStatement) left).negatedLawStatement();
		if (right.checkNegationLaw())
			right = ((OperationStatement) right).negatedLawStatement();
		return false;
	}

	@Override
	protected boolean checkNegateConstantsLaw() {
		if (left.checkNegateConstantsLaw())
			left = ((LineStatement) left).getChild();
		if (right.checkNegateConstantsLaw())
			right = ((LineStatement) right).getChild();
		return false;
	}

	@Override
	protected boolean checkDoubleNegationLaw() {
		if (left.checkDoubleNegationLaw())
			left = ((LineStatement) ((LineStatement) left).getChild()).getChild();
		if (right.checkDoubleNegationLaw())
			right = ((LineStatement) ((LineStatement) right).getChild()).getChild();
		return false;
	}

	@Override
	protected boolean checkDeMorgansLaw() {
		return true; // TODO
	}

//	private static final HashMap<Operator, Operator> operatorPairs;
//	static {
//		operatorPairs = new HashMap<Operator, Operator>();
//		operatorPairs.put(Operator.AND, Operator.OR);
//	}

	protected LineStatement deMorgans() {
		Operator switchedOperator = switch (operator) {
			case AND -> Operator.OR;
			case OR -> Operator.AND;
			case NAND -> Operator.NOR;
			case NOR -> Operator.NAND;
			case XOR -> Operator.XNOR;
			case  XNOR -> Operator.XOR;
			case IMPLIES -> Operator.REV_IMPLIES; //TODO
			case REV_IMPLIES -> Operator.IMPLIES;
			default -> null;
		};
		if (switchedOperator == null)
			return null;
		Statement leftStatement = left;
		if (left instanceof LineStatement)
			if (((LineStatement) left).getChild() instanceof LineStatement && !((LineStatement) left).getChild().getTruth() && !getTruth())
				leftStatement = new LineStatement(((LineStatement) left).getChild(), LineOperator.CONSTANT);
			else
				leftStatement = ((LineStatement) left).not();
		Statement rightStatement = right;
		if (right instanceof LineStatement)
			if (((LineStatement) right).getChild() instanceof LineStatement && !((LineStatement) right).getChild().getTruth() && !getTruth())
				rightStatement = new LineStatement(((LineStatement) right).getChild(), LineOperator.CONSTANT);
			else
				rightStatement = ((LineStatement) right).not();
		return new OperationStatement(leftStatement, switchedOperator, rightStatement).not();


//Operator o = ops[(Arrays.search(ops, operator) + 4) % 8];
		
//		for (int i = 0;;)
//			if (new Operator[] { Operator.XNOR, Operator.OR, Operator.NOR, Operator.IMPLIES, Operator.XOR, Operator.AND,
//					Operator.NAND, Operator.REV_IMPLIES, Operator.EQUIVALENT }[i++].equals(operator))
//				return new OperationStatement(
//						!left.getClass().equals(LineStatement.class) ? left
//								: ((LineStatement) left).getChild().getClass().equals(LineStatement.class)
//										&& !((LineStatement) left).getChild().getTruth() && !getTruth()
//												? new LineStatement(((LineStatement) left).getChild(), true)
//												: ((LineStatement) left).not(),
//						new Operator[] { Operator.XNOR, Operator.OR, Operator.NOR, Operator.IMPLIES, Operator.XOR,
//								Operator.AND, Operator.NAND, Operator.REV_IMPLIES, Operator.EQUIVALENT }[(i + 3) % 8],
//						!right.getClass().equals(LineStatement.class) ? right
//								: ((LineStatement) right).getChild().getClass().equals(LineStatement.class)
//										&& !((LineStatement) right).getChild().getTruth() && !getTruth()
//												? new LineStatement(((LineStatement) left).getChild(), true)
//												: ((LineStatement) right).not()).not();
	}

	@Override
	protected boolean checkAbsorptionLaw() { //if left statement matches one of the children of the right //do this law from the top down? // TODO depends on if absolute or literal checking equal
		if ((left instanceof OperationStatement
			&& (((OperationStatement) left).getLeft().equals(right)
			|| ((OperationStatement) left).getRight().equals(right))
				&& (((OperationStatement) left).getOperator().equals(Operator.AND) && operator.equals(Operator.OR)
				|| (((OperationStatement) left).getOperator().equals(Operator.OR) && operator.equals(Operator.AND))))
		|| (right instanceof OperationStatement
			&& (((OperationStatement) right).getLeft().equals(left)
			|| ((OperationStatement) right).getRight().equals(left))
				&& (((OperationStatement) right).getOperator().equals(Operator.AND) && operator.equals(Operator.OR)
				|| (((OperationStatement) right).getOperator().equals(Operator.OR) && operator.equals(Operator.AND)))
				))
			return true;
		if (right.checkAbsorptionLaw())
			right = ((OperationStatement) right).absorptionStatement();
		if (left.checkAbsorptionLaw())
			left = ((OperationStatement) left).absorptionStatement();
		return false;
	}

	@Override
	protected boolean checkExtendedAbsorptionLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkDistributiveLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean checkAssociativeLaw() {
		// TODO Auto-generated method stub
		return false;
	}

	public Statement negatedLawStatement() {
		switch (operator) {
		case AND: case NOR: case XNOR:
			return new VariableStatement("c");
		case OR: case NAND: case XOR:
			return new VariableStatement("t");
		case REV_IMPLIES:
			return left;
		case IMPLIES:
			return right;
		default:
			return null;
		}
	}

	public Statement idempotentStatement() {
		switch (operator) {
		case XNOR: case IMPLIES: case REV_IMPLIES:
			return new VariableStatement("t");
		case XOR:
			return new VariableStatement("c");
		case AND: case OR:
			return left;
		case NAND: case NOR:
			return left.not();
		default:
			return null;
		}
	}

	public Statement identityUniversalBoundStatement() {
		if (left.isConstant() && left.getTruth() || right.isConstant() && right.getTruth())
			switch (operator) {
			case IMPLIES:
				return left.isConstant() ? right : new VariableStatement("t");
			case REV_IMPLIES:
				return right.isConstant() ? right : new VariableStatement("t");
			case OR:
				return new VariableStatement("t");
			case NOR:
				return new VariableStatement("c");
			case AND: case XNOR:
				return left.isConstant() ? right : left;
			case NAND: case XOR:
				return (left.isConstant() ? right : left).not();
			default:
				return null;
			}
		else
			switch (operator) {
			case IMPLIES:
				return left.isConstant() ? new VariableStatement("t") : left.not();
			case REV_IMPLIES:
				return right.isConstant() ? new VariableStatement("t") : left.not();
			case NAND:
				return new VariableStatement("t");
			case AND:
				return new VariableStatement("c");
			case OR: case XOR:
				return left.isConstant() ? right : left;
			case NOR: case XNOR:
				return (left.isConstant() ? right : left).not();
			default:
				return null;
			}

	}

	public Statement absorptionStatement() {
		if (left instanceof OperationStatement) {
			if (((OperationStatement) left).getLeft().equals(right))
				return right;
			else if (((OperationStatement) left).getRight().equals(right))
				return right;
			return null;
		}
		if (((OperationStatement) right).getLeft().equals(left))
			return left;
		else if (((OperationStatement) right).getRight().equals(left))
			return left;
		return null; // Should not happen
	}

	public Statement extendedAbsorptionStatement() { //TODO
		Operator switchedOperator;
		Statement primary;
		Statement secondary;
		if (left instanceof OperationStatement) {
			if (((OperationStatement) left).getLeft().equals(right))
				return right;
			else if (((OperationStatement) left).getRight().equals(right))
				return right;
			return null;
		}
		if (right instanceof OperationStatement)
			if (((OperationStatement) right).getLeft().equals(left))
				return left;
			else if (((OperationStatement) right).getRight().equals(left))
				return left;
		// return primary;
		return null;
	}

}

//if (left.getClass().equals(VariableStatement.class))
//current.add(this);
//else
//current.addAll(left.getChildren());
//if (right.getClass().equals(VariableStatement.class))
//current.add(this);
//else
//current.addAll(right.getChildren());
