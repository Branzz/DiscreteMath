package bran.test;

import bran.draw.StartViewer;
import bran.draw.exprs.StartExpressionViewer;
import bran.graphs.Edge;
import bran.graphs.Graph;
import bran.graphs.Vertex;
import bran.logic.Argument;
import bran.logic.StatementGenerator;
import bran.logic.TruthTable;
import bran.logic.statements.OperationStatement;
import bran.logic.statements.Statement;
import bran.logic.statements.VariableStatement;
import bran.logic.statements.operators.Operator;
import bran.mathexprs.ExpressionGenerator;
import bran.mathexprs.treeparts.Constant;
import bran.mathexprs.treeparts.Expression;
import bran.mathexprs.treeparts.Variable;
import bran.matrices.Matrix;
import bran.parser.StatementParser;
import bran.sets.FiniteSet;
import bran.sets.NumberToText;
import bran.sets.numbers.NumberLiteral;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
public class MainTest {

	public static void main(String[] args) {
		// NumberToStringTest();
		// SetsTest();
		// logicTest();
		// jamesGrimesPuzzleTest();
		// OperationTest();
		// HamiltonianPathTestingVertices();
		// hamiltonianPathTestingEdges();
		// parseTest();
		// statementGeneratorTest();
		// Stream.of("111", "50").map(Main::solve).forEach(System.out::println);
		// expressionTest();
		// System.out.println(maxProfit(new int[] {2, 0, -1}));
		// System.out.println(Stream.of(0, 1).reduce(1, Math::multiplyExact));
		visualTest();
	}

	public static void visualTest() {

		StartExpressionViewer.start();

		// char[] data = new char[129];
		// Arrays.fill(data, '.');
		// for (int scale = 128; scale >= 1; scale /= 2) {
		// 	if (scale == 128)
		// 		data[0] = 'X';
		// 	for (int i = scale; i < 129; i += 2 * scale)
		// 		data[i] = 'X';
		// 	System.out.println(Stream.of(data).map(String::valueOf).collect(Collectors.joining("")));
		// 	Arrays.fill(data, '.');
		// }

		// for (int scale = 128; scale >= 1; scale /= 2) {
		// 	for (int i = 0; i < 128; i++)
		// 		System.out.print(i % scale == 0 ? "X" : ".");
		// 	System.out.println();
		// }
		// StartViewer.startViewerGraph(new Graph(new Matrix(new double[][] {{1, 0, 0}, {0, 1, 1}, {0, 1, 0}})));
	}

	//TODO return tuple with exception test

	private static class Data {
		int buyIndex; // minimum before
		int profit;
	}

	public static int maxProfit(int[] nums) {
		return (int) IntStream.range(0, nums.length).mapToLong(i -> IntStream.range(i, nums.length).mapToLong(j -> Arrays.stream(nums, i + 1, j + 1).reduce(nums[i], (l, r) -> l * r)).max().getAsLong()).max().getAsLong();
	}

	public static void expressionTest() {
		// Variable x = new Variable("x");
		// System.out.println(Constant.ZERO.pow(Constant.ONE).simplified());

		// Variable x = new Variable("x");
		// x.respect();
		// // x.setValue(2.5);
		// System.out.println(TAN.of(x.times(Constant.of(2).pow(ASINH.of(x)))).deriveString());

		// Arrays.stream(MultivariableFunction.values()).filter(f -> f.derivable == null).forEach(System.out::println);
		long seed = 0L;
		// for (int s = 5; s <= 8; s++)
		// 	System.out.printf("Size: %d Statement: %s,   %s,   %s\n", s, ExpressionGenerator.generate(seed, s),
		// 					  ExpressionGenerator.generate(seed, s).derive(), ExpressionGenerator.generate(seed, s).derive().simplified());
		// System.out.println(ExpressionGenerator.generate(15));
		Expression exp = ExpressionGenerator.generate(seed, 8);
		// System.out.printf("Expression: %s\nSimplified: %s\nDerivative: %s\nDomain:     %s (==%s)", exp, exp.simplified(), exp.derive(), exp.getUniversalStatement(), exp.getUniversalStatement().truth());
		// System.out.println(new UniversalStatement<>(
		// 		new VariableStatement[] { new VariableStatement('a'), new VariableStatement('b') },
		// 		a -> a[0].equates(TAUTOLOGY).and(a[1].equates(TAUTOLOGY)), new FiniteSet<>(true, false),
		// 		true));
		// System.out.println(new ExistentialStatement<>(
		// 		new Variable[] { new Variable("a") },
		// 		a -> new UniversalStatement<>(new Variable[] { new Variable("b") },
		// 										b -> a[0].times(b[0]).equates(Constant.ZERO),
		// 										new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), true),
		// 		new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)),
		// 		true));
		// Statement.forAll(new Variable("a")).in(new FiniteSet<>(new NumberLiteral(0), new NumberLiteral(1), new NumberLiteral(2)), a -> a[0].equates(Constant.NEG_ONE).not());
		Variable x = new Variable("x");
		System.out.println(x.approaches(Constant.ZERO).of(x.plus(Constant.of(10))).evaluate());
	}

	public static int solve(String message) {
		char[] chars = message.toCharArray();
		Token[] tokens = new Token[message.length() + 1];
		tokens[0] = new Token(true, Collections.emptyList());

		for (int i = 1; i < message.length(); i++) {
			List<Integer> prefixes = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				if (tokens[j].splittable) {
					if (inRange(message.substring(i, j + 1))) {
						prefixes.add(j);
					}
				}
			}
			tokens[i] = new Token(!prefixes.isEmpty(), prefixes);
		}

		return tokens[message.length() - 1].prefixes.size();
	}

	private static boolean inRange(String word) {
		int c = Integer.parseInt(word);
		return c >= 1 && c <= 26;
	}

	private static class Token {
		boolean splittable;
		List<Integer> prefixes;
		Token(boolean splittable, List<Integer> prefixes) {

		}
	}

	private static void statementGeneratorTest() {
		// for (int s = 0; s <= 20; s++)
		// 	System.out.printf("Size: %d Statement: %s\n", s, StatementGenerator.generate(3L, s));
		System.out.println(StatementGenerator.generate(3L, 20, .8, .5, .5, 0, 1));
	}

	static void parseTest() {
		for (String s : new String[] { "a", "~!!!~not a", "!(a_1 & a_2)", "abc -> b & C | d ^ e", "a & !b", "a ^ (b^C   nand d) | a", "(a == z)", "a & (((b & d)))" })
			System.out.println(s + " ".repeat(25 - s.length()) + StatementParser.parseStatement(s));

		// System.out.println(Parser.parseStatement("a & (!a & a)"));
	}

	private static void operationTest() {
		VariableStatement a = new VariableStatement("a");
		VariableStatement b = new VariableStatement("b");
		ArrayList<Statement> demorg = new ArrayList<>();
		for (Operator o : Operator.values()) {
			demorg.add(new OperationStatement(a, o, b).not());
			demorg.add(new OperationStatement(a.not(), o, b.not())); //nand nor, xor xnor/eq
		}

		for (Statement statement : demorg)
			System.out.println(TruthTable.getTable(statement));

		ArrayList<Statement> abso = new ArrayList<>();
		for (Operator o : Operator.values())
			for (Operator o1 : Operator.values())
				abso.add(new OperationStatement(a, o, new OperationStatement(a, o1, b)));

		for (Statement statement : abso)
			System.out.println(TruthTable.getTable(statement));
	}

	private static void numberToStringTest() {
		// for (double i = .0001; i <= .1; i += .0001)
		double[] arr = { 1.1234567896, 0.005, 999888777, 2.1524, 3003003.3, 3333.333333 };
		for (int i = 0; i < arr.length; i++)
			System.out.println(arr[i] + ":" + NumberToText.numberToString(arr[i]));
	}

	private static void logicTest() {
		VariableStatement a = new VariableStatement("a");
		VariableStatement b = new VariableStatement("b");
		VariableStatement c = new VariableStatement("C");
		VariableStatement t = new VariableStatement("t");
		VariableStatement f = new VariableStatement("c");
		VariableStatement p = new VariableStatement("p");
		VariableStatement q = new VariableStatement("q");
		VariableStatement r = new VariableStatement("r");

		System.out.println(TruthTable.getTable(t.and(f).or(t.and(f.not()))));

		System.out.println(TruthTable.getTable(new Argument(p.or(q), p.implies(r), q.or(r.not()))));
		System.out.println((new Argument(p.or(q), p.implies(r), q.or(r.not()))).toFullString());
		System.out.println(TruthTable.getTable(p.nor(r).implies(q.nor(f)).xor(p.and(q.not()))));

		// OperationStatement st1 = OperationStatement.andOf(a, b);
		// OperationStatement st2 = OperationStatement.orOf(st1, c);
		// OperationStatement st3 = OperationStatement.andOf(st2, b);
		// OperationStatement st4 = OperationStatement.orOf(a, b);
		// OperationStatement st5 = OperationStatement.andOf(st3, st4);
		// OperationStatement f0 = s.andOf(s.andOf(s.orOf(s.andOf(s.notOf(a), b), c), b), s.orOf(a, b));
		// Statement f = a.or(taut.xor(c.not())).not().implies(b.nor(a));
		// System.out.println(TruthTable.getTable(f));
		// .xor()).not().implies(b.nor(a));

		Statement y = a.not().or(b.not());
		Statement z = a.and(b).not();
		System.out.println(TruthTable.getTable(y));
		System.out.println(TruthTable.getTable(z));
		System.out.println(TruthTable.getTable(p.implies(q.implies(r)).xnor(p.implies(q).implies(r))));
		System.out.println(y.compareTo(z));

		Argument arg = new Argument(a.nand(b), b.not(), a);
		System.out.println(arg.toString());

		System.out.println(f.toString());
		//∴ 	&#8756; 	&#x2234;
		//≡ 	&#8801; 	&#x2261;
	}

	private static void setsTest() {
		FiniteSet x = new FiniteSet();
		x.add(new NumberLiteral(1));
		x.add(new NumberLiteral(2));
		FiniteSet y = new FiniteSet();
		y.add(new NumberLiteral(1));
		y.add(new NumberLiteral(2));
		System.out.println(x.equals(y));
		System.out.println(x.isSubsetOf(y));
	}

	private static void jamesGrimesPuzzleTest() {
		HashMap<Vertex, List<Edge>> t = new HashMap<>();
		Vertex v0 = new Vertex("car");
		Vertex v1 = new Vertex("diamond");
		Vertex v2 = new Vertex("goat");
		Vertex v3 = new Vertex("bogeys");
		Vertex v4 = new Vertex("poke");
		Edge e0 = new Edge(v0, v1);
		Edge e1 = new Edge(v1, v2);
		//		Edge e2 = new Edge(v2, v3);
		//		Edge e3 = new Edge(v3);
		for (Edge e : new Edge[] { e0, e1} )
			for (Vertex v : e.getVertices() )
				v.addEdge(e);
		for (Vertex v : new Vertex[] { v0, v1, v2, v3, v4} )
			t.put(v, v.getEdges());
		//		Graph g = new Graph(new Matrix(new double[][] {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}));
		//		Graph g = new Graph(Matrix.getRandomIntMatrix(5, 5, 0, 5));
		Graph g = new Graph(t);
		System.out.println(g.toString());
		StartViewer.startViewerGraph(g);
	}

	private static void hamiltonianPathTestingEdges() {
		new ArrayList<Integer>();
		Graph graph = new Graph(new Matrix(new double[][]
												   { {0, 1, 1, 1, 0, 2},
														   {1, 0, 0, 1, 1, 2},
														   {1, 0, 0, 1, 0, 2},
														   {1, 1, 1, 0, 1, 1},
														   {0, 1, 0, 1, 0, 2},
														   {1, 1, 1, 1, 1, 0} }));
		System.out.println("For graph:\n" + graph);
		System.out.println("Is Euelerian trail? " + graph.isEulerianTrailDeepSearch());
		// System.out.println("Is Euelerian trail? " + graph.isEulerianTrail());
	}

	static void HamiltonianPathTestingVertices() {
		// Graph inner = new Graph(new Matrix(new double[][]
		//   { {0, 1, 1, 1, 0, 1},
		// 	{1, 0, 0, 1, 1, 1},
		// 	{1, 0, 0, 1, 0, 1},
		// 	{1, 1, 1, 0, 1, 1},
		// 	{0, 1, 0, 1, 0, 1},
		// 	{1, 1, 1, 1, 1, 0} }));
		Graph frame = new Graph(symmetricalize(new double[][]
													   /*0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15*/
													   /*0*/ { {-0, 1, 2, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1 }, /*0*/
				/*1*/	{ 0,-0, 1, 1, 2, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1 }, /*1*/
				/*2*/	{ 0, 0,-0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1 }, /*2*/
				/*3*/	{ 0, 0, 0,-0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 }, /*3*/
				/*4*/	{ 0, 0, 0, 0,-0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1 }, /*4*/
				/*5*/	{ 0, 0, 0, 0, 0,-0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0 }, /*5*/
				/*6*/	{ 0, 0, 0, 0, 0, 0,-0, 1, 0, 0, 1, 1, 0, 0, 1, 0 }, /*6*/
				/*7*/	{ 0, 0, 0, 0, 0, 0, 0,-0, 1, 0, 1, 1, 0, 0, 1, 0 }, /*7*/
				/*8*/	{ 0, 0, 0, 0, 0, 0, 0, 0,-0, 0, 0, 1, 1, 0, 0, 1 }, /*8*/
				/*9*/	{ 1, 1, 1, 0, 1, 0, 0, 0, 0,-0, 1, 0, 1, 2, 1, 1 }, /*9*/
				/*10*/	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-0, 1, 0, 0, 1, 0 }, /*10*/
				/*11*/	{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-0, 1, 0, 0, 1 }, /*11*/
				/*12*/	{ 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0,-0, 1, 1, 2 }, /*12*/
				/*13*/	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1,-0, 1, 1 }, /*13*/
				/*14*/	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1,-0, 1 }, /*14*/
				/*15*/	{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1,-0 } }));
		/*0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15*/
		System.out.println("For graph:\n" + frame);
		System.out.println("Is Hamiltonian path? " + frame.isHamiltonianPath());

	}

	private static Matrix symmetricalize(double[][] values) {
		return symmetricalize(values, Math::max);
	}

	private static Matrix symmetricalize(double[][] values, BiFunction<Double, Double, Double> biFunction) {
		for (int row = 0; row < values.length; row++)
			for (int col = 0; col < row; col++) {
				double decision = biFunction.apply(values[row][col], values[col][row]);
				values[row][col] = decision;
				values[col][row] = decision;
			}
		return new Matrix(values);
	}

}