package bran.run;

import bran.logic.statements.Statement;
import bran.logic.statements.StatementDisplayStyle;
import bran.logic.statements.special.ExistentialStatement;
import bran.logic.statements.special.UniversalStatement;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.parser.StatementParser;
import bran.sets.Definition;
import bran.sets.FiniteSet;
import bran.sets.numbers.NumberLiteral;
import bran.sets.numbers.godel.GodelNumberFactors;

import java.util.Arrays;
import java.util.List;

import static bran.mathexprs.treeparts.Constant.ZERO;
import static bran.mathexprs.treeparts.functions.MultivariableFunction.LOG;

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

		// Variable x = new Variable("x");

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

		showcase();

	}

	public static void showcase() {
		// DisplayStyle.displayStyle;
		String statementString = "a    and  ~ b or  !(c ^   t) implies b"; // where t is a tautology
		Statement statement = StatementParser.parseStatement(statementString);
		StatementDisplayStyle.statementStyle = StatementDisplayStyle.JAVA_LOGICAL;
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
																							 new FiniteSet<>(new NumberLiteral(9), new NumberLiteral(5)), true,
																							 new Variable("b")),
															   new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), true,
															   new Variable("a"))
						   + "\n" + Arrays.toString(ZERO.equates(ZERO).godelNumber().getGodelNumberArray())
						   + "\n" + new GodelNumberFactors(243_000_000L).symbols()
				// + "\n" +
		);
	}

	private static <T> T[] toArray(List<T> list) {
		T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
		for (int i = 0; i < list.size(); i++) {
			toR[i] = list.get(i);
		}
		return toR;
	}

}
