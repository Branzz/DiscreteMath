package bran.tree.generators;

import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.Composition;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CompositionGenerator {

	public static Composition generate(int size) {
		return generate(new Random(), size);
	}

	public static Composition generate(long seed, int size) {
		return generate(new Random(seed), size);
	}

	private static Composition generate(final Random rand, int size) {
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
	public static Composition generate(long seed, int size, double operationProb, double contProb, double tautProb, double newVariableProb, double leftSideProb) {
		return new Generator(new Random(seed), size, operationProb, contProb, tautProb, newVariableProb, leftSideProb).generate();
	}

	// Just a one-time use inner static class to make all the variables visible to each method
	private static class Generator {
		final Random rand;
		final int size;
		Composition composite;
		private final double operationProb, contProb, tautProb, newVariableProb, leftSideProb;
		private boolean generated;

		private static final LogicalOperator[] operators = LogicalOperator.values();

		Set<VariableStatement> variablePool = new HashSet<>();

		VariableStatement of(char nameChar) {
			String name = String.valueOf(nameChar);
			for (VariableStatement v : variablePool)
				if (v.toFullString().equals(name))
					return v;
			VariableStatement next = new VariableStatement(name);
			variablePool.add(next);
			return next;
		}

		// This could just be another method in the outer class, but it does exactly the same thing.
		Generator(final Random rand, final int size) {
			this(rand, size, 0.8, 0.0, 0.0, 0.5, 0.5);
		}

		Generator(final Random rand, final int size, final double operationProb,
				  final double contProb, final double tautProb, final double newVariableProb,
				  final double leftSideProb) {
			this.rand = rand;
			this.size = size;
			this.operationProb = operationProb;
			this.contProb = contProb;
			this.tautProb = tautProb;
			this.newVariableProb = newVariableProb;
			this.leftSideProb = leftSideProb;
			generated = false;
			generate();
			generated = true;
		}

		Composition generate() {
			if (generated)
				return composite;
			if (size <= 0)
				return composite = Composition.empty();
			final ExpressionGenerator.Generator expressionGen =
					new ExpressionGenerator.Generator(rand, size, operationProb, contProb, newVariableProb, leftSideProb);
			final StatementGenerator.Generator statementGen =
					new StatementGenerator.Generator(rand, size, operationProb, contProb, tautProb, newVariableProb, leftSideProb);
			return composite;
		}

		private void appendTails() {
		}

	}

}
