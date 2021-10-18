package bran.logic;

import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.LogicalOperator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StatementGenerator {

	public static Statement generate(int size) {
		return generate(new Random(), size);
	}

	public static Statement generate(long seed, int size) {
		return generate(new Random(seed), size);
	}

	private static Statement generate(final Random rand, int size) {
		return new Generator(rand, size).generate();
	}

	/**
	 * probabilities should be between 0.0 and 1.0
	 * @param operationProb		- probability of on operation being selected (over a NOT)
	 * @param contProb			- probability of contradictions being used instead of a random variable
	 * @param tautProb			- probability of tautologies being used instead of a random variable
	 * @param newVariableProb	- probability of creating a new random variable instead of using an old one
	 * @param leftSideProb		- probability that the next random statement is appended to the left of the operation
	 */
	public static Statement generate(long seed, int size, double operationProb, double contProb, double tautProb, double newVariableProb, double leftSideProb) {
		return new Generator(new Random(seed), size, operationProb, contProb, tautProb, newVariableProb, leftSideProb).generate();
	}

	// Just a one-time use inner static class to make all the variables visible to each method
	private static class Generator {
		final Random rand;
		final int size;
		char maxVariableName;
		Statement composite;
		private final double operationProb, contProb, tautProb, newVariableProb, leftSideProb;
		private boolean generated;

		private static final LogicalOperator[] operators = LogicalOperator.values();

		Set<VariableStatement> variablePool = new HashSet<>();

		VariableStatement of(char nameChar) {
			String name = String.valueOf(nameChar);
			for (VariableStatement v : variablePool)
				if (v.toString().equals(name))
					return v;
			VariableStatement next = new VariableStatement(name);
			variablePool.add(next);
			return next;
		}

		// This could just be another method in the outer class, but it does exactly the same thing.
		Generator(final Random rand, final int size) {
			this(rand, size, 0.8, 0.0, 0.0, 0.5, 0.5);
		}

		Generator(final Random rand, final int size, final double operationProb, final double contProb,
				  final double tautProb, final double newVariableProb, final double leftSideProb) {
			this.rand = rand;
			this.size = size;
			this.maxVariableName = 'A';
			this.operationProb = operationProb;
			this.contProb = contProb;
			this.tautProb = tautProb;
			this.newVariableProb = newVariableProb;
			this.leftSideProb = leftSideProb;
			generated = false;
			generate();
			generated = true;
		}

		Statement generate() {
			if (generated)
				return composite;
			if (size <= 0)
				return composite = Statement.empty();
			VariableStatement first = new VariableStatement(String.valueOf(maxVariableName));
			composite = first;
			variablePool.add(first);
			int i = 1;
			while (i < size - 2) {
				if (rand.nextDouble() < operationProb) {
					appendOperationStatement();
					i += 2;
				} else {
					appendLineStatement();
					i++;
				}
			}
			// if (i == size - 2)
			// 	appendOperationStatement();
			// else if (i == size - 1)
			// 	appendLineStatement();
			return composite;
		}

		private VariableStatement nextVariableStatement() {
			if (rand.nextDouble() < tautProb)
				return VariableStatement.TAUTOLOGY;
			else if (rand.nextDouble() < contProb)
				return VariableStatement.CONTRADICTION;
			else if (rand.nextDouble() < newVariableProb) {
				maxVariableName++;
				return of(maxVariableName);
			}
			else
				return of((char) (rand.nextInt(maxVariableName - 'A' + 1) + 'A'));
		}

		private void appendLineStatement() {
			composite = composite.not();
		}

		private void appendOperationStatement() {
			LogicalOperator nextOp = operators[rand.nextInt(operators.length)];
			boolean leftSide = rand.nextDouble() < leftSideProb;
			composite = leftSide ? new OperationStatement(composite, nextOp, nextVariableStatement())
								 : new OperationStatement(nextVariableStatement(), nextOp, composite);
		}

	}
}
