package bran.run;

import bran.tree.compositions.expressions.functions.ExpFunction;
import bran.tree.compositions.expressions.functions.FunctionExpression;
import bran.tree.compositions.expressions.operators.ArithmeticOperator;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.sets.UnarySet;
import bran.tree.compositions.sets.SetOperation;
import bran.tree.compositions.sets.Set;
import bran.tree.compositions.sets.operators.UnarySetOperator;
import bran.tree.compositions.sets.operators.SetOperator;
import bran.tree.compositions.sets.regular.FiniteNumberSet;
import bran.tree.compositions.statements.operators.UnaryStatementOperator;
import bran.tree.compositions.statements.operators.LogicalOperator;
import bran.tree.compositions.statements.special.BooleanSet;
import bran.tree.generators.StatementGenerator;
import bran.tree.compositions.statements.*;
import bran.tree.compositions.statements.special.quantifier.ExistentialStatement;
import bran.tree.compositions.statements.special.quantifier.UniversalStatement;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.values.Variable;
import bran.exceptions.IllegalArgumentAmountException;
import bran.parser.StatementParser;
import bran.tree.compositions.Definition;
import bran.tree.compositions.sets.regular.FiniteSet;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.godel.GodelNumberFactors;
import bran.tree.structure.TreePart;

import java.util.Arrays;
import java.util.List;

import static bran.tree.compositions.expressions.values.Constant.ZERO;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.LOG;
import static bran.tree.compositions.statements.Statement.forAll;
import static bran.tree.compositions.statements.Statement.thereExists;

public class Main {

	/*
	 * TODO:
	 * The other parser, generic parser, graph/equation/expression visual rep, function creation complete, details in readme-better examples-multiple display format
	 * contrapositive - proof by
	 * show exactly how it simplified steps
	 * deprecate SpecialStatement?
	 * strongly connected components within graphs and the corresponding algs
	 * test multi variable quantification statement godel number string
	 * graphs: all assgn 4 things
	 */
	public static void main(String[] args) throws Exception {

		// System.out.println(new Matrix("2,0,-1;0,1,0").multiply(new Matrix("1,2;5,-4;-3,3")));

		// mountainProof();
		// StartExpressionViewer.start();

		verify();

		// Variable x = new Variable("x");
		//
		// final Expression expression = x.pow(x.times(LOG.of(PI, x)));
		// System.out.println(expression);
		// System.out.println(expression.getDomainConditions());
		// System.out.println(expression.derive());
		// System.out.println(expression.derive().simplified());

		// final Expression exp = Constant.TWO.div(x.div(x.div(x.div(x))));
		// System.out.println(exp);
		// System.out.println(exp.simplified());
		// System.out.println(exp.simplified().getDomainConditions().simplified());
		// System.out.println(Constant.of(3).times(x.squared()).plus(SIN.of(x).times(x.squared())));
		// System.out.println(Constant.of(3).times(x.squared()).plus(SIN.of(x).times(x.squared())).simplified());

		// final Expression exp = x.squared().times(E.pow(of(-2).times(x)));
		// System.out.println("                              " + exp);
		// System.out.println("Derivative:                   " + exp.derive());
		// System.out.println("Simplified Derivative:        " + exp.derive().simplified());
		// System.out.println("Second Derivative:            " + exp.derive().simplified().derive());
		// System.out.println("Simplified Second Derivative: " + exp.derive().simplified().derive().simplified());

		// Variable a = new Variable("a");
		// Variable b = new Variable("b");
		// final Expression expression = ((OperatorExpression) ExpressionGenerator.generate(0L, 30, 1.0, .5, .1, .5)).getRight();
		// System.out.println(expression);
		// System.out.println(expression.simplified());
		// System.out.println(expression.derive());
		// System.out.println(expression.derive().simplified());
		// Statement x = StatementParser.parseStatement("!(a or b) and C or a and b");
		// System.out.println(TruthTable.getTable(x) + "\n" + TruthTable.getTable(x.simplified()));

		// Variable x = new Variable("x");
		// final Expression derive = x.squared()
		// 						   .div(x.inc())
		// 						   .minus(x.squared()
		// 								   .inc()
		// 								   .sqrt())
		// 						   .derive();
		// System.out.println(derive + "\n" + derive.simplified());

		// System.out.println(StatementParser.parseStatement("a == a"));
		// System.out.println(ExpressionParser.parseExpression("log(3, 27)"));

		// System.out.println(ExpressionParser.parseExpression("e^xy+yxe^xy").summaryString());

		// showcase();

	}

	public static void showcase() {
		// DisplayStyle.displayStyle;
		String statementString = "a    and  ~ b or  !(c ^   t) implies b"; // where t is a tautology
		Statement statement = StatementParser.parse(statementString);
		StatementDisplayStyle.statementStyle = StatementDisplayStyle.MATH;
		Variable varA = new Variable("a", true);
		Expression expression = Expression.empty();
		try {
			expression = varA.pow(LOG.of(varA, varA.pow(varA))).minus(ZERO);
		} catch (IllegalArgumentAmountException ignored) { }
		System.out.println(statementString
						   + "\n" + statement
						   + "\n" + statement.getTable()
						   + "\n" + StatementGenerator.generate(3L, 14)
						   + "\n" + StatementGenerator.generate(3L, 14).simplified()
						   + "\n     " + expression
						   + "\ns:   " + expression.simplified()
						   + "\nd:   " + expression.derive()
						   + "\nds:  " + expression.derive().simplified()
						   + "\nsd:  " + expression.simplified().derive()
						   + "\nsds: " + expression.simplified().derive().simplified()
						   + "\n" + expression.derive().getUniversalStatement()
						   + "\n" + expression.derive().getUniversalStatement().simplified()
						   + "\n" + Definition.ODD.test(new Constant(5.0))
						   + "\n" + new ExistentialStatement<>(a -> new UniversalStatement<>(b -> a[0].times(b[0]).equates(ZERO),
																							 new FiniteSet(new NumberLiteral(9), new NumberLiteral(5)), true,
																							 new Variable("b")),
															   new FiniteSet(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), true,
															   new Variable("a"))
						   + "\n" + Arrays.toString(ZERO.equates(ZERO).godelNumber().getGodelNumberArray())
						   + "\n" + new GodelNumberFactors(243_000_000L).symbols()
				// + "\n" +
		);
		VariableStatement x = new VariableStatement('x');
		System.out.println(Statement.forAll(x)
									.in(new BooleanSet())
									.thatEach(args -> x.nand(new VariableStatement('y')))
									.proven()
									.implies(Constant.of(3).plus(new Variable("abc")).equates(new Variable("var")))
									.godelNumber());

		System.out.println(thereExists(new Variable("a"))
				.in(new FiniteNumberSet(0, 1, 2))
				.suchThat(a -> forAll(new Variable("b"))
								   .in(new FiniteNumberSet(9, 5))
								   .itHolds(b -> a.times(b).equates(ZERO))
								   .proven())
				.proven());
	}

	public static void verify() {
		System.out.println(TreePart.treeVerifier(Statement.class, Boolean.class, LogicalOperator.class,
				StatementOperation.class, UnaryStatementOperator.class, UnaryStatement.class, VariableStatement.class));

		System.out.println(TreePart.treeVerifier(Expression.class, Double.class, ArithmeticOperator.class,
				ExpressionOperation.class, ExpFunction.class, FunctionExpression.class, Variable.class));

		System.out.println(TreePart.treeVerifier(Set.class, Set.class, SetOperator.class,
				SetOperation.class, UnarySetOperator.class, UnarySet.class, null));
	}

	private static <T> T[] toArray(List<T> list) {
		T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
		for (int i = 0; i < list.size(); i++) {
			toR[i] = list.get(i);
		}
		return toR;
	}

}
