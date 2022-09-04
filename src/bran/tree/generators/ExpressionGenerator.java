package bran.tree.generators;

import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Value;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.exceptions.IllegalArgumentAmountException;
import bran.tree.compositions.expressions.functions.MultiArgFunction;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.operators.ExpressionOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bran.tree.compositions.expressions.operators.ArithmeticOperator.MOD;

public class ExpressionGenerator {

	public static Expression generate(int size) {
		return generate(new Random(), size);
	}

	public static Expression generate(long seed, int size) {
		return generate(new Random(seed), size);
	}

	private static Expression generate(final Random rand, int size) {
		return new ExpressionGenerator.Generator(rand, size).generate();
	}

	/**
	 * probabilities should be between 0.0 and 1.0
	 * @param operationProb		- probability of on operation being selected (over a function)
	 * @param constantProb		- probability of the next value being a number
	 * @param newVariableProb	- probability of creating a new random variable instead of using an old one
	 * @param leftSideProb		- probability that the next random expression is appended on the left side
	 */
	public static Expression generate(long seed, int size, double operationProb, double constantProb, double newVariableProb, double leftSideProb) {
		return new ExpressionGenerator.Generator(new Random(seed), size, operationProb, constantProb, newVariableProb, leftSideProb).generate();
	}

	public static double DEEP_CHANCE;

	static class Generator {

		final Random rand;
		final int size;
		char maxVariableName;
		Expression composite;
		private final double operationProb, constantProb, newVariableProb, leftSideProb;
		private boolean generated;
		List<Variable> variables;

		private static final ArithmeticOperator[] ARITHMETIC_OPERATORS = ArithmeticOperator.values();
		private static final MultiArgFunction[] functions = MultiArgFunction.values();

		// This could just be another method in the outer class, but it does exactly the same thing.
		Generator(final Random rand, final int size) {
			//(double) operators.length / (operators.length + functions.length)
			this(rand, size, .8, 0.5, 0.5, 0.5);
		}

		Generator(final Random rand, final int size, final double operationProb, final double constantProb, final double newVariableProb, final double leftSideProb) {
			this.rand = rand;
			this.size = size;
			this.constantProb = constantProb;
			this.maxVariableName = 'a';
			this.operationProb = operationProb;
			this.newVariableProb = newVariableProb;
			this.leftSideProb = leftSideProb;
			variables = new ArrayList<>();
			variables.add(new Variable(String.valueOf(maxVariableName), true));
			generated = false;
			generate();
			generated = true;
		}

		Expression generate() {
			if (generated)
				return composite;
			if (size <= 0)
				return composite = Expression.empty();
			composite = nextValue();
			int i = 1;
			while (i < size - 2) {
				if (rand.nextDouble() < operationProb) {
					appendOperationExpression();
					i += 1;
				} else {
					i += appendFunctionExpression();
				}
			}
			// if (i == size - 2)
			// 	appendOperationStatement();
			// else if (i == size - 1)
			// 	appendLineStatement();
			return composite;
		}

		private Value nextValue() {
			if (rand.nextDouble() < constantProb)
				return new Constant(rand.nextInt(32));
			else if (rand.nextDouble() < newVariableProb) {
				maxVariableName++;
				variables.add(new Variable(String.valueOf(maxVariableName), true));
				return variables.get(variables.size() - 1);
			}
			else {
				// System.out.println(variables.size() + " " + variables.get(variables.size() - 1).hashCode());
				return variables.get(rand.nextInt(variables.size()));
			}
		}

		private int appendFunctionExpression() {
			MultiArgFunction nextFunc = functions[rand.nextInt(functions.length)];
			int arguments = nextFunc.getArgAmount();
			// boolean leftSide = rand.nextDouble() < leftSideProb;
			if (arguments <= 0)
				return 0;
			else if (arguments == 1) {
				try {
					composite = new FunctionExpression(nextFunc, composite);
				} catch (IllegalArgumentAmountException ignored) {
				}
			}
			// else if (arguments == 2)
			// 	composite = leftSide ? new FunctionExpression(nextFunc, composite, nextVariable())
			// 					: new FunctionExpression(nextFunc, nextVariable(), composite);
			else {
				int index = (int) (arguments * truncate(rand.nextDouble() - leftSideProb));
				Expression[] args = new Expression[arguments];
				for (int i = 0; i < args.length; i++)
					args[i] = i == index ? composite : nextValue();
				try {
					composite = new FunctionExpression(nextFunc, args);
				} catch (IllegalArgumentAmountException ignored) {
				}
			}
			return arguments;
		}

		private static double truncate(double value) {
			return Math.max(Math.min(value, 1.0), 0.0);
		}

		private void appendOperationExpression() {
			ArithmeticOperator nextOp = ARITHMETIC_OPERATORS[rand.nextInt(ARITHMETIC_OPERATORS.length)];
			if (nextOp == MOD) // TODO ???
				return;
			boolean leftSide = rand.nextDouble() < leftSideProb;
			Value nextVal = nextValue();
			if (composite instanceof ExpressionOperation opExp) {
				if (rand.nextDouble() < DEEP_CHANCE) {
					composite = leftSide ? new ExpressionOperation(new ExpressionOperation(nextVal, nextOp, opExp.getLeft()),
																  opExp.getOperator(), opExp.getRight())
										: new ExpressionOperation(opExp.getLeft(), opExp.getOperator(),
																 new ExpressionOperation(opExp.getRight(), nextOp, nextVal));
				}
			}
			else {
				composite = leftSide ? new ExpressionOperation(composite, nextOp, nextVal)
									: new ExpressionOperation(nextVal, nextOp, composite);
			}
		}
	}

}
