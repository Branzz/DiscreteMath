package bran.tree.compositions.statements;

import java.util.*;

import bran.tree.compositions.Composition;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.equivalences.equation.EquationType;
import bran.tree.compositions.statements.special.equivalences.Equivalence;
import bran.tree.compositions.statements.special.equivalences.EquivalenceType;
import bran.tree.compositions.statements.special.equivalences.inequality.InequalityType;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.godel.GodelNumberSymbols;
import bran.tree.compositions.godel.GodelBuilder;
import bran.tree.structure.MonoTypeFork;

import static bran.tree.compositions.statements.VariableStatement.*;
import static bran.tree.compositions.statements.operators.LogicalOperator.*;

public class StatementOperation extends Statement implements MonoTypeFork<Boolean, Statement, LogicalOperator, StatementOperation> {

	private Statement left;
	private LogicalOperator operator;
	private Statement right;

	public StatementOperation(Statement left, LogicalOperator operator, Statement right) {
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

	public boolean operate(boolean left, boolean right) {
		return operator.operate(left, right);
	}

	public static boolean operate(boolean left, LogicalOperator operator, boolean right) {
		return operator.operate(left, right);
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
	public List<Composition> getChildren() {
		ArrayList<Composition> current = new ArrayList<>();
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
	public void replaceAll(final Composition original, final Composition replacement) {
		if (original.equals(left))
			left = (Statement) replacement;
		else
			left.replaceAll(original, replacement);
		if (original.equals(right))
			right = (Statement) replacement;
		else
			right.replaceAll(original, replacement);
	}

	@Override
	public String toString() {
		String leftString = left.toString();
		String rightString = right.toString();
		if (left instanceof StatementOperation leftOperation
			&& leftOperation.getOperator().precedence() > operator.precedence())
			leftString = Composition.parens(leftString);
		if (right instanceof StatementOperation rightOperation
			&& rightOperation.getOperator().precedence() > operator.precedence())
			rightString = Composition.parens(rightString);
		return leftString + " " + operator.toString() + " " + rightString;
	}

	@Override
	public boolean equals(Object s) {
		return s instanceof StatementOperation s0 && s0.getOperator().equals(operator) && left.equals(s0.getLeft()) && right.equals(s0.getRight());
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
		return simplified == null ? new StatementOperation(leftSimplified, operator, rightSimplified) : simplified;
	}

	/**
	 * @return simplified or null if it couldn't simplify
	 */
	private Statement simplified(Statement left, LogicalOperator operator, Statement right, boolean commutativeSearch) {
		// System.out.print("" + left + operator + right + "\t->\t");
//TODO ~(a o b) -> a ~o b ; ~a demorg(o) ~b // how to decide between? inequalities want demorg ; try both? AI SEARCH?
		// return new OperationStatement(, operator, right.simplified());
		if (left.isConstant()) { // Universal Bound Law
			if (right.isConstant()) // basic logic case (not a law)
				return operator.operate(left.getTruth(), right.getTruth()) ? TAUTOLOGY : CONTRADICTION;
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

		/*

		o : { <, <=, >, >=, ==?, !=? }
		p:(a o b) op q:(c o d)
		let E = P:(b - a) * Q:(d - c)
		XOR		= 0 o E, !P opposite Q, P opposite !Q
		NOR		= 0 o.opposite E
		p & !q	= P*sqrt(Q) o 0
		NAND    = P*sqrt(Q) opposite 0, sqrt(P)*Q opposite 0, !P*sqrt(Q)
		!p & q	= sqrt(P)*Q o 0
		XNOR	= P*!Q < 0, !P*Q < 0, pq opposite 0
		AND		= sqrt(!P)*sqrt(!Q) opposite 0, sqrt(!P)*Q < 0
		 */
		if (commutativeSearch && operator.isCommutative()) {
			final Statement simplified = commutativeCrossSearch(left, operator, right);
			if (simplified != null)
				return simplified;
		}

		if (left instanceof Equivalence leftEq && right instanceof Equivalence rightEq) {
			if (leftEq.getLeft().equals(rightEq.getLeft()) && ((leftEq.getRight() instanceof Constant && rightEq.getRight() instanceof Constant) || leftEq.getRight().equals(rightEq.getRight()))) {
				return equivalenceSolver(leftEq.getLeft(), leftEq.getRight(), rightEq.getRight(), leftEq.getEquivalenceType(), rightEq.getEquivalenceType());
			}
			else if (leftEq.getLeft().equals(rightEq.getRight()) && ((leftEq.getRight() instanceof Constant && rightEq.getLeft() instanceof Constant) || leftEq.getRight().equals(rightEq.getLeft()))) {
				return equivalenceSolver(leftEq.getLeft(), leftEq.getRight(), rightEq.getLeft(), leftEq.getEquivalenceType(), rightEq.getEquivalenceType().flipped());
			}
			else if (leftEq.getRight().equals(rightEq.getLeft()) && ((leftEq.getLeft() instanceof Constant && rightEq.getRight() instanceof Constant) || leftEq.getLeft().equals(rightEq.getRight()))) {
				return equivalenceSolver(leftEq.getRight(), leftEq.getLeft(), rightEq.getRight(), leftEq.getEquivalenceType().flipped(), rightEq.getEquivalenceType());
			}
			else if (leftEq.getRight().equals(rightEq.getRight()) && ((leftEq.getLeft() instanceof Constant && rightEq.getLeft() instanceof Constant) || leftEq.getLeft().equals(rightEq.getLeft()))) {
				return equivalenceSolver(leftEq.getRight(), leftEq.getLeft(), rightEq.getLeft(), leftEq.getEquivalenceType().flipped(), rightEq.getEquivalenceType().flipped());
			}
		}
		else if (right instanceof StatementOperation rightO) {
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
		else if (left instanceof StatementOperation leftO) {
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

	private Statement equivalenceSolver(final Expression base, final Expression leftExp, final Expression rightExp,
										final EquivalenceType leftType, final EquivalenceType rightType) {
		return leftExp instanceof Constant // bLessA
			   && rightExp instanceof Constant
			   && leftExp.compareTo(rightExp) > 0
					  ? equivalenceSolver0(base, operator,			 leftExp, rightExp, leftType, rightType)
					  : equivalenceSolver0(base, operator.flipped(), rightExp, leftExp, rightType, leftType);
	}

	/**
	 * must be passed through in the form:
	 * (base leftType leftExp) operator (base rightType rightExp)
	 */
	private Statement equivalenceSolver0(final Expression base, final LogicalOperator operator, final Expression leftExp, final Expression rightExp,
										final EquivalenceType leftType, final EquivalenceType rightType) {
		int numLine = 0b00000;
		if(operator.operate(leftType.lesser(), rightType.lesser()))  numLine |= 0b10000;
		final boolean leftEqualsRight = leftExp.equals(rightExp);
		if (leftEqualsRight) {
			if(operator.operate(leftType.equal(), rightType.equal()))  numLine |= 0b01110;
		} else {
			if(operator.operate(leftType.equal(), rightType.lesser()))  numLine |= 0b01000;
			if(operator.operate(leftType.greater(), rightType.lesser()))  numLine |= 0b00100;
			if(operator.operate(leftType.greater(), rightType.equal()))   numLine |= 0b00010;
		}
		if (operator.operate(leftType.greater(), rightType.greater())) numLine |= 0b00001;

		if (numLine == 0b00000)
			return CONTRADICTION;
		if (numLine == 0b11111 || (leftEqualsRight && (numLine | 0b01110) == 0b01110))
			return TAUTOLOGY;

		final EquivalenceType[] types = { InequalityType.GREATER, EquationType.EQUAL, InequalityType.GREATER_EQUAL,
				InequalityType.LESS, EquationType.UNEQUAL, InequalityType.LESS_EQUAL };

		final int num8 = numLine % 8;
		if (num8 == 0 || num8 == 7) // b irrelevant
			return Equivalence.of(base, types[(numLine >> 2) - 1], leftExp);

		if (numLine <= 0b00011 || numLine >= 0b11100) // a irrelevant
			return Equivalence.of(base, types[num8 - 1], rightExp);

		LogicalOperator center = (numLine & 0b00100) == 0b00100 ? AND : OR;
		return new StatementOperation(Equivalence.of(base, types[(numLine >> 2) - 1], leftExp), center, Equivalence.of(base, types[num8 - 1], rightExp));
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
			StatementOperation combinedStatements = new StatementOperation(iterator.next(), operator, iterator.next());
			while (iterator.hasNext())
				combinedStatements = new StatementOperation(combinedStatements, operator, iterator.next());
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
		if (statement instanceof StatementOperation statementOperation
			&& statementOperation.getOperator() == operator) {
				commutativeSearch(statementOperation.getLeft(), operator, terms);
				commutativeSearch(statementOperation.getRight(), operator, terms);
		} else
			terms.add(statement);
	}

	@Override
	public void appendGodelNumbers(final GodelBuilder godelBuilder) {
		appendGodelOpBuffer(operator.buffer(left, right), godelBuilder);
	}

	private void appendGodelOpBuffer(Object[] buffer, final GodelBuilder godelBuilder) {
		for (Object o : buffer) {
			if (o instanceof GodelNumberSymbols gns)
				godelBuilder.push(gns);
			else if (o instanceof Statement statement)
				statement.appendGodelNumbers(godelBuilder);
			else if (o instanceof Object[] innerBuffer)
				appendGodelOpBuffer(innerBuffer, godelBuilder);
			else
				new Exception("this shouldn't happen").printStackTrace();
		}
	}

	/* the laws to check to know if it simplifies.
	   not technically necessary, but they do have names,
	   and now, one can check a law themself */

	public Statement deMorgans() {
		return new StatementOperation(left.not(), switch (operator) {
			case AND -> OR;
			case OR -> AND;
			case NAND -> NOR;
			case NOR -> NAND;
			case XOR -> XNOR;
			case XNOR -> XOR;
			// case IMPLIES -> REV_IMPLIES; //TODO other results
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
