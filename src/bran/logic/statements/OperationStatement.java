package bran.logic.statements;

import java.util.*;

import bran.logic.statements.operators.LogicalOperator;
import bran.sets.numbers.godel.GodelNumber;
import bran.sets.numbers.godel.GodelNumberSymbols;
import bran.sets.numbers.godel.GodelVariableMap;
import bran.tree.Fork;

import static bran.logic.statements.VariableStatement.*;
import static bran.logic.statements.operators.LogicalOperator.*;

public class OperationStatement extends Statement implements Fork<Statement, LogicalOperator, Statement> { // Two Child

	private final Statement left;
	private final LogicalOperator operator;
	private final Statement right;

	public OperationStatement(Statement left, LogicalOperator operator, Statement right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public Statement getLeft() {
		return left;
	}

	public LogicalOperator getOperator() {
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
//			throw new Var1iableExpressionException();
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

	public static boolean operate(boolean truth1, LogicalOperator operator, boolean truth2) {
		return operator.operate(truth1, truth2);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean getTruth() {
		return operate(left.getTruth(), right.getTruth());
	}

	@Override
	public List<Statement> getChildren() {
		ArrayList<Statement> current = new ArrayList<>();
		current.add(this);
		current.addAll(left.getChildren());
		current.addAll(right.getChildren());
		return current;
	}

	@Override
	public List<VariableStatement> getVariables() {
		ArrayList<VariableStatement> current = new ArrayList<>();
		current.addAll(left.getVariables());
		current.addAll(right.getVariables());
		return current;
	}

	@Override
	public String toFullString() {
		return '(' + left.toFullString() + " " + operator.toString() + " " + right.toFullString() + ')';
	}

	@Override
	public String toString() {
		String leftString = left.toString();
		String rightString = right.toString();
		if (left instanceof OperationStatement leftOperation
			&& leftOperation.getOperator().getOrder() < operator.getOrder())
			leftString = parens(leftString);
		if (right instanceof OperationStatement rightOperation
			&& rightOperation.getOperator().getOrder() < operator.getOrder())
			rightString = parens(rightString);
		return leftString + " " + operator.toString() + " " + rightString;
	}

	@Override
	public boolean equals(Object s) {
		return s instanceof OperationStatement s0 && s0.getOperator().equals(operator) && left.equals(s0.getLeft()) && right.equals(s0.getRight());
	}

	// @FunctionalInterface interface LawCheck { boolean check(); }
	// @FunctionalInterface interface LawGet { Statement morph(); }
	// private final Map<LawCheck, LawGet> lawCheckGets = Map.of(this::checkNegationLaw, this::negatedLawStatement);
	// return lawCheckGets.get(lawCheckGets.keySet().stream().filter(LawCheck::check).findFirst()).morph();

	@Override
	public Statement simplified() {
		final Statement leftSimplified = left.simplified();
		final Statement rightSimplified = right.simplified();
		final Statement simplified = simplified(leftSimplified, operator, rightSimplified, true);
		return simplified == null ? new OperationStatement(leftSimplified, operator, rightSimplified) : simplified;
	}

	/**
	 * @return simplified or null if it couldn't simplify
	 */
	private Statement simplified(Statement left, LogicalOperator operator, Statement right, boolean cummutativeSearch) {
		// System.out.print("" + left + operator + right + "\t->\t");

		// return new OperationStatement(, operator, right.simplified());
		if (left.isConstant()) { // Universal Bound Law
			if (right.isConstant()) // basic logic case (not a law)
				return getTruth() ? TAUTOLOGY : CONTRADICTION;
			if (left.getTruth())
				return switch (operator) { // double check these TODO
					case REV_IMPLIES, OR -> TAUTOLOGY;
					case NOR -> CONTRADICTION;
					case IMPLIES, AND, XNOR -> right;
					case NAND, XOR -> right.not();
					default -> null;
				};
			else
				return switch (operator) {
					case IMPLIES, NAND, REV_IMPLIES -> TAUTOLOGY;
					case AND -> CONTRADICTION;
					case OR, XOR -> right;
					case NOR, XNOR -> right.not();
					default -> null;
				};
		}
		else if (right.isConstant()) { // Universal Bound Law
			if (right.getTruth())
				return switch (operator) {
					case IMPLIES, OR -> TAUTOLOGY;
					case NOR -> CONTRADICTION;
					case REV_IMPLIES, AND, XNOR -> left;
					case NAND, XOR -> left.not();
					default -> null;
				};
			else
				return switch (operator) {
					case REV_IMPLIES, NAND -> TAUTOLOGY;
					case AND -> CONTRADICTION;
					case OR, XOR -> left;
					case IMPLIES, NOR, XNOR -> left.not();
					default -> null;
				};
		}

		if (left.equalsNot(right)) { // Negation Law
			return switch (operator) {
				case OR, NAND, XOR -> TAUTOLOGY;
				case AND, NOR, XNOR -> CONTRADICTION;
				case REV_IMPLIES -> left;
				case IMPLIES -> right;
				default -> null;
			};
		}
		else if (left.equals(right)) { // Idempotent Law
			return switch (operator) {
				case XNOR, IMPLIES, REV_IMPLIES -> TAUTOLOGY;
				case XOR -> CONTRADICTION;
				case AND, OR -> left;
				case NAND, NOR -> left.not();
				default -> null;
			};
		}
		// absorptionAAB
		// absorptionABA
		// absorptionABB
		// absorptionBAB
		// 	return deMorgans();
		// extended form of the Absorption Law (precomputed (static) operation combos)
		// OperationStatement rightO;

		/*
		>= is > or ==
		<= is < or ==
		> is not < and not ==
		< is not > and not ==
		 */

		if (cummutativeSearch && operator.isCommutative()) {
			final Statement simplified = commutativeCrossSearch(left, operator, right);
			if (simplified != null)
				return simplified;
		}
		if (right instanceof OperationStatement rightO) {
			// rightO = rightOp;
			// <--- TODO if left instanceof operation then you can factor
			if (rightO.getLeft().equals(left)) {
				return (!rightO.getOperator().isCommutative() ? absorptionAAB : absorptionRightOp)
							   .get(operator, rightO.getOperator()).absorb(left, rightO.getRight());
			} else if (rightO.getRight().equals(left))
				return (!rightO.getOperator().isCommutative() ? absorptionABA : absorptionRightOp)
							   .get(operator, rightO.getOperator()).absorb(left, rightO.getLeft());
		}
		// else if (right instanceof LineStatement rightL && rightL.getChild() instanceof OperationStatement rightLO) {
		// 	// rightO = rightLO.deMorgans();
		// }
		else if (left instanceof OperationStatement leftO) {
			if (leftO.getLeft().equals(right)) {
				return (!operator.isCommutative() ? absorptionBAB : absorptionLeftOp)
							   .get(leftO.getOperator(), operator).absorb(leftO.getRight(), right);
			} else if (leftO.getRight().equals(right))
				return (!operator.isCommutative() ? absorptionABB : absorptionLeftOp)
							   .get(leftO.getOperator(), operator).absorb(leftO.getLeft(), right);
		}
		// if left op and right op
		// if (checkDeMorgansLaw())
		return null;
	}

	// /**
	//  * @param operator - commutative operator to be searched along
	//  */
	// private Statement commutativeDeepSearch(OperationStatement statement, LogicalOperator operator) { // TODO
	// 	Collection<Statement> terms = new ArrayList<>();
	// 	commutativeDeepSearch(statement, operator, terms);
	//
	// 	// try to factor with all terms?
	// 	return expression;
	// }

	// private void commutativeDeepSearch(Statement statement, LogicalOperator operator, Collection<Statement> terms) {
	// 	if (statement instanceof OperationStatement operationStatement) {
	// 		if ((operationStatement.getOperator() == operator)) {
	// 			commutativeDeepSearch(operationStatement.getLeft(), operator, terms);
	// 			commutativeDeepSearch(operationStatement.getRight(), operator, terms);
	// 		}
	// 	} else
	// 		terms.add(statement);
	// }

	private Statement commutativeCrossSearch(Statement leftStatement, LogicalOperator operator, Statement rightStatement) {
		List<Statement> leftTerms = new ArrayList<>();
		List<Statement> rightTerms = new ArrayList<>();
		List<Statement> newTerms = new ArrayList<>();

		commutativeSearch(leftStatement, operator, leftTerms);
		commutativeSearch(rightStatement, operator, rightTerms);

		if (leftTerms.size() + rightTerms.size() < 3)
			return null;
		for (int i = 0; i < leftTerms.size(); i++) {
			for (int j = 0; j < rightTerms.size() && i < leftTerms.size(); j++) {
				Statement newTerm = simplified(leftTerms.get(i), operator, rightTerms.get(j), false);
				// System.out.println("\t" + newTerm);

				if (newTerm != null) { // check to see if any of the current terms can combine and accumulate further:
					leftTerms.remove(i);
					rightTerms.remove(j--);
					newTerm = accumulateTerms(operator, newTerms, newTerm);
					newTerm = accumulateTerms(operator, leftTerms, newTerm);
					newTerm = accumulateTerms(operator, rightTerms, newTerm);
					newTerms.add(newTerm);
				}
			}
		}
		if (newTerms.size() == 0)
			return null;
		else {
			newTerms.addAll(leftTerms);
			newTerms.addAll(rightTerms);
			final Iterator<Statement> iterator = newTerms.iterator();
			if (newTerms.size() == 1)
				return iterator.next();
			OperationStatement combinedStatements = new OperationStatement(iterator.next(), operator, iterator.next());
			while (iterator.hasNext())
				combinedStatements = new OperationStatement(combinedStatements, operator, iterator.next());
			return combinedStatements;
		}
	}

	private Statement accumulateTerms(final LogicalOperator operator, final List<Statement> terms, Statement newTerm) {
		for (int i = 0; i < terms.size(); i++) {
			Statement accNewTerm = simplified(terms.get(i), operator, newTerm, false); // (order doesn't matter because it's commutative)
			if (accNewTerm != null) {
				terms.remove(i--);
				newTerm = accNewTerm;
			}
		}
		return newTerm;
	}

	private void commutativeSearch(Statement statement, LogicalOperator operator, Collection<Statement> terms) {
		if (statement instanceof OperationStatement operationStatement
			&& operationStatement.getOperator() == operator) {
				commutativeSearch(operationStatement.getLeft(), operator, terms);
				commutativeSearch(operationStatement.getRight(), operator, terms);
		} else
			terms.add(statement);
	}

	@Override
	public void appendGodelNumbers(final Stack<GodelNumber> g, final GodelVariableMap vs) {
		appendGodelOpBuffer(operator.buffer(left, right), g, vs);
	}

	private void appendGodelOpBuffer(Object[] buffer, final Stack<GodelNumber> g, final GodelVariableMap vs) {
		for (Object o : buffer) {
			if (o instanceof GodelNumberSymbols gns)
				g.push(gns);
			else if (o instanceof Statement statement)
				statement.appendGodelNumbers(g, vs);
			else if (o instanceof Object[] innerBuffer)
				appendGodelOpBuffer(innerBuffer, g, vs);
			else
				new Exception("this shouldn't happen").printStackTrace();
		}
	}

	/* the laws to check to know if it simplifies.
	   not technically necessary, but they do have names,
	   and now, one can check a law themself */

	public Statement deMorgans() {
		return new OperationStatement(left.not(), switch (operator) {
			case AND -> OR;
			case OR -> AND;
			case NAND -> NOR;
			case NOR -> NAND;
			case XOR -> XNOR;
			case XNOR -> XOR;
			// case IMPLIES -> REV_IMPLIES; //TODO
			// case REV_IMPLIES -> IMPLIES;
			default -> null;
		}, right.not()).not();

		// if (switchedOperator == null)
		// 	return null;
		// Statement leftStatement = left;
		// if (left instanceof LineStatement)
		// 	if (((LineStatement) left).getChild() instanceof LineStatement && !((LineStatement) left).getChild().getTruth() && !getTruth())
		// 		leftStatement = new LineStatement(((LineStatement) left).getChild(), LineOperator.CONSTANT);
		// 	else
		// 		leftStatement = ((LineStatement) left).not();
		// Statement rightStatement = right;
		// if (right instanceof LineStatement)
		// 	if (((LineStatement) right).getChild() instanceof LineStatement && !((LineStatement) right).getChild().getTruth() && !getTruth())
		// 		rightStatement = new LineStatement(((LineStatement) right).getChild(), LineOperator.CONSTANT);
		// 	else
		// 		rightStatement = ((LineStatement) right).not();
		// return new OperationStatement(leftStatement, switchedOperator, rightStatement).not();
//Operator o = ops[(Arrays.search(ops, operator) + 4) % 8];

//		for (int i = 0;;) // just to see... yes; you can do this entire thing in 1 line
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

/*

		The next section is old; it tried to check each child and modify it rather than self modiy

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
	protected boolean checkIdentityUniversalBoundLaw() { // this is all wrong
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
			case AND -> OR;
			case OR -> AND;
			case NAND -> NOR;
			case NOR -> NAND;
			case XOR -> XNOR;
			case XNOR -> XOR;
			case IMPLIES -> REV_IMPLIES; //TODO
			case REV_IMPLIES -> IMPLIES;
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

//		for (int i = 0;;) // just to see... you can do this entire thing in 1 line
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
				&& (((OperationStatement) left).getOperator().equals(AND) && operator.equals(OR)
				|| (((OperationStatement) left).getOperator().equals(OR) && operator.equals(AND))))
		|| (right instanceof OperationStatement
			&& (((OperationStatement) right).getLeft().equals(left)
			|| ((OperationStatement) right).getRight().equals(left))
				&& (((OperationStatement) right).getOperator().equals(AND) && operator.equals(OR)
				|| (((OperationStatement) right).getOperator().equals(OR) && operator.equals(AND)))
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

	*//* the replacements for simplification *//*

	public Statement negatedLawStatement() {
		return switch (operator) {
			case AND, NOR, XNOR -> CONTRADICTION;
			case OR, NAND, XOR -> TAUTOLOGY;
			case REV_IMPLIES -> left;
			case IMPLIES -> right;
			default -> null;
		};
	}

	public Statement idempotentStatement() {
		return switch (operator) {
			case XNOR, IMPLIES, REV_IMPLIES -> TAUTOLOGY;
			case XOR -> CONTRADICTION;
			case AND, OR -> left;
			case NAND, NOR -> left.not();
			default -> null;
		};
	}

	public Statement identityUniversalBoundStatement() {
		if (left.isConstant() && left.getTruth() || right.isConstant() && right.getTruth())
			return switch (operator) {
				case IMPLIES -> left.isConstant() ? right : TAUTOLOGY;
				case REV_IMPLIES -> right.isConstant() ? right : TAUTOLOGY;
				case OR -> TAUTOLOGY;
				case NOR -> CONTRADICTION;
				case AND, XNOR -> left.isConstant() ? right : left;
				case NAND, XOR -> (left.isConstant() ? right : left).not();
				default -> null;
			};
		else
			return switch (operator) {
				case IMPLIES -> left.isConstant() ? TAUTOLOGY : left.not();
				case REV_IMPLIES -> right.isConstant() ? TAUTOLOGY : left.not();
				case NAND -> TAUTOLOGY;
				case AND -> CONTRADICTION;
				case OR, XOR -> left.isConstant() ? right : left;
				case NOR, XNOR -> (left.isConstant() ? right : left).not();
				default -> null;
			};
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

*/

}
