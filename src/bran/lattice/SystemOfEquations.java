package bran.lattice;

import bran.Cache;
import bran.exceptions.IllegalMatrixSizeException;
import bran.lattice.matrix.ExpressionMatrix;
import bran.lattice.vector.VectorSet;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.sets.regular.FiniteSet;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SystemOfEquations extends FiniteSet<Equation> implements DefaultLattice<SystemOfEquations> {

	private final Cache<EquationSystem> equationSystem;
	private final Cache<ExpressionMatrix> inner;

	public SystemOfEquations(Equation... equations) {
		super(new ArrayList<>(Arrays.asList(equations)));

		equationSystem = new Cache<EquationSystem>() {
			private EquationSystem inner;
			@Override public EquationSystem access() { return inner; }
			@Override public EquationSystem create() { return inner = new EquationSystem(set); }
		};

		inner = new Cache<ExpressionMatrix>() {
			private ExpressionMatrix inner;
			@Override public ExpressionMatrix access() { return inner; }
			@Override public ExpressionMatrix create() { return inner = createMatrix(); }
		};
	}

	@Override
	public Lattice inner() {
		return inner.get();
	}

	@Override
	public SystemOfEquations toSystemOfEquations(List<Expression> variables) {
		if (equationSystem.get().allVariables.size() != variables.size())
			throw new IllegalMatrixSizeException();
		equationSystem.get().allVariables = variables;
		return this;
	}

	@Override
	public VectorSet toVectorSet() {
		return toMatrix().toVectorSet();
	}

	/**
	 * minor necessary simplification
	 * call simplified() beforehand for simplest system
	 * will treat 1/x or sin(z) as individual "variables"
	 */
	@Override
	public ExpressionMatrix toMatrix() {
		return inner.get();
	}

	private ExpressionMatrix createMatrix() {
		return new ExpressionMatrix(equationSystem.get().equationTerms.stream()
						.map(e -> Stream.concat(IntStream.range(0, equationSystem.get().allVariables.size())
												   .map(i -> e.variablePlacement.indexOf(i))
												   .mapToObj(p -> (Expression) (p == -1 ? Constant.ZERO : e.coefficients.get(p))),
										        Stream.of(e.rightSide))
								        .collect(Collectors.toList()))
						.collect(Collectors.toList()), true);
	}

	@Override
	public SystemOfEquations solved() {
		return null;
	}

	@Override
	public <R extends Lattice<R>> boolean equivalent(Lattice<R> lattice) {
		return false;
	}

	static class EquationSystem {

		List<EquationTerms> equationTerms;
		List<Expression> allVariables;

		public EquationSystem(List<Equation> equations) {
			equationTerms = equations.stream().map(EquationTerms::new).collect(Collectors.toList());
			allVariables = equationTerms.get(0).variables;
			equationTerms.get(0).variablePlacement = IntStream.range(0, equationTerms.get(0).variables.size()).boxed().collect(Collectors.toList());
			for (int i = 1; i < equationTerms.size(); i++) {
				for (int j = 0; j < equationTerms.get(i).variables.size(); j++) {
					final Expression variable = equationTerms.get(i).variables.get(j);
					int placement = allVariables.indexOf(variable);
					if (placement == -1) {
						allVariables.add(variable);
						equationTerms.get(i).variablePlacement.add(allVariables.size() - 1);
					} else {
						equationTerms.get(i).variablePlacement.add(placement);
					}
				}
			}
		}

		private static class EquationTerms {
			List<Constant> coefficients = new ArrayList<>();
			List<Expression> variables = new ArrayList<>();
			List<Integer> variablePlacement = new ArrayList<>();
			// assert coeff.size = variables.size = variablePlacement.size
			Constant rightSide;

			public EquationTerms(Equation equation) {
				List<ExpressionOperation.Term> terms = ExpressionOperation.commutativeSearch(equation.getLeft().minus(equation.getRight()), false, ArithmeticOperator.ADD);
				List<ExpressionOperation.Term> constantTerms = terms.stream().filter(t -> t.expression() instanceof Constant).toList();
				terms.removeAll(constantTerms);
				rightSide = new Constant(-constantTerms.stream()
												   .map(t -> t.expressionInvertCorrected(ArithmeticOperator.ADD))
												   .reduce(Constant.ZERO, Expression::plus)
												   .evaluate());
				for (ExpressionOperation.Term term : terms) {
					List<ExpressionOperation.Term> factors = ExpressionOperation.commutativeSearch(term.expressionInvertCorrected(ArithmeticOperator.ADD), false, ArithmeticOperator.MUL);
					List<ExpressionOperation.Term> constantFactors = factors.stream().filter(t -> t.expression() instanceof Constant).toList();
					factors.removeAll(constantFactors);
					coefficients.add(new Constant(constantFactors.stream()
																 .map(t -> t.expressionInvertCorrected(ArithmeticOperator.MUL))
														   		 .reduce(Constant.ONE, Expression::times)
														   		 .evaluate()));
					List<Expression> varFactors = factors.stream().map(t -> t.expressionInvertCorrected(ArithmeticOperator.MUL)).toList();
					if (varFactors.size() == 0) { // possible if simplification was done badly
						variables.add(Constant.ZERO);
					} else {
						Expression var = varFactors.get(0);
						for (int i = 1; i < varFactors.size(); i++) {
							var = var.times(varFactors.get(1));
						}
						variables.add(var);
					}

				}
			}

		}
	}

}
