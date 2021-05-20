package bran.run;

import bran.draw.StartViewer;
import bran.graphs.Edge;
import bran.graphs.Graph;
import bran.graphs.Vertex;
import bran.logic.Argument;
import bran.logic.StatementGenerator;
import bran.logic.TruthTable;
import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.StatementDisplayStyle;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.Operator;
import bran.logic.statements.special.ExistentialStatement;
import bran.logic.statements.special.UniversalStatement;
import bran.mathexprs.ExpressionGenerator;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.mathexprs.treeparts.functions.IllegalArgumentAmountException;
import bran.matrices.Matrix;
import bran.parser.StatementParser;
import bran.sets.Definition;
import bran.sets.FiniteSet;
import bran.sets.NumberToText;
import bran.sets.numbers.NumberLiteral;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static bran.mathexprs.treeparts.functions.MultivariableFunction.LOG;

public class Main {

	/*
	 * TODO:
	 * The other parser, generic parser, graph/equation/expression visual rep, function creation complete, details in readme-better examples-multiple display format
	 * contrapositive - proof by
	 * show exactly how it simplified steps
	 */
	public static void main(String[] args) {

		showcase();

	}


	public static void showcase() {
		// DisplayStyle.displayStyle;
		String statementString = "a    and  ~ b or  !(c ^   t) implies b"; // where t is a tautology
		Statement statement = StatementParser.parseStatement(statementString);
		StatementDisplayStyle.statementStyle = StatementDisplayStyle.JAVA_LOGICAL;
		Variable varA = new Variable("a", true);
		Expression expression = null;
		try {
			expression = varA.pow(LOG.of(varA, varA.pow(varA))).minus(Constant.ZERO);
		} catch (IllegalArgumentAmountException ignored) { }
		System.out.println(statementString
				+ "\n" + statement
				+ "\n" + statement.getTable()
				+ "\n" + Definition.ODD.test(new Constant(5.0))
				+ "\n" + new ExistentialStatement<>(a -> new UniversalStatement<>(b -> a[0].times(b[0]).equates(Constant.ZERO),
														 new FiniteSet<>(new NumberLiteral(9), new NumberLiteral(5)), true,
														 new Variable("b")),
										   new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), true,
										   new Variable("a"))
				+ "\n" + expression
				+ "\n" + expression.simplified()
				+ "\n" + expression.derive()
				+ "\n" + expression.simplified().derive()
				+ "\n" + expression.derive().simplified());
	}


	private static <T> T[] toArray(List<T> list) {
		T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0).getClass(), list.size());
		for (int i = 0; i < list.size(); i++) {
			toR[i] = list.get(i);
		}
		return toR;
	}

}
