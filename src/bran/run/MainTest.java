package bran.run;

import bran.application.draw.StartViewer;
import bran.application.draw.exprs.StartExpressionViewer;
import bran.exceptions.ParseException;
import bran.lattice.graphs.Edge;
import bran.lattice.graphs.Graph;
import bran.lattice.graphs.Vertex;
import bran.lattice.matrix.DoubleGrid;
import bran.lattice.SystemOfEquations;
import bran.lattice.matrix.ExpressionMatrix;
import bran.parser.composition.CompositionParser;
import bran.tree.Holder;
import bran.tree.compositions.Composition;
import bran.tree.compositions.expressions.Expression;
import bran.tree.compositions.expressions.operators.ExpressionOperation;
import bran.tree.compositions.expressions.values.Constant;
import bran.tree.compositions.expressions.values.Variable;
import bran.tree.compositions.expressions.values.numbers.NumberLiteral;
import bran.tree.compositions.expressions.values.numbers.NumberToText;
import bran.tree.compositions.godel.GodelNumberFactors;
import bran.tree.compositions.sets.regular.*;
import bran.tree.compositions.sets.regular.var.WithVariableSet;
import bran.tree.compositions.statements.Statement;
import bran.tree.compositions.statements.StatementImpl;
import bran.tree.compositions.statements.TruthTable;
import bran.tree.compositions.statements.VariableStatement;
import bran.tree.compositions.statements.special.BooleanSet;
import bran.tree.compositions.statements.special.VerbalStatement;
import bran.tree.compositions.statements.special.equivalences.equation.Equation;
import bran.tree.compositions.statements.special.proofs.Argument;
import bran.tree.compositions.statements.special.quantifier.ExistentialStatement;
import bran.tree.compositions.statements.special.quantifier.QuantifiedStatementArguments;
import bran.tree.compositions.statements.special.quantifier.UniversalStatement;
import bran.tree.generators.ExpressionGenerator;
import bran.tree.generators.StatementGenerator;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static bran.tree.compositions.expressions.functions.MultiArgFunction.LOG;
import static bran.tree.compositions.expressions.functions.MultiArgFunction.SIN;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.DIV;
import static bran.tree.compositions.expressions.operators.ArithmeticOperator.MUL;
import static bran.tree.compositions.expressions.values.Constant.ZERO;
import static bran.tree.compositions.statements.Statement.forAll;
import static bran.tree.compositions.statements.Statement.thereExists;

public class MainTest {

	// TODO add delimit statements in parser like new line semicolon or auto ?? then switch to the desmos equation analyzer for declarations etc
	// TODO implement functions: evaluate (e->e), simplify(c->c), derive(e->e), range(e->b), domain(e->b), truth(t->t), inverse(e->e), etc
	public static void main(String[] args) {
		// System.out.println(CompositionParser.parseExpression("(x^5 * y^3) / (x^2 * y)").simplified());
		// System.out.println(CompositionTokens.compositionCompiler.compile("(x^5 * y^3) / (x^2 * y)"));
		// System.out.println(CompositionParser.parse("rng()"));
		// numberToStringTest();
		// SetsTest();
		// logicTest();
		// jamesGrimesPuzzleTest();
		// operationTest();
		// HamiltonianPathTestingVertices();
		// hamiltonianPathTestingEdges();
		// parseTest();
		// statementGeneratorTest();
		// expressionGeneratorTest();
		// Stream.of("111", "50").map(Main::solve).forEach(System.out::println);
		// expressionTest();
		// System.out.println(maxProfit(new int[] {2, 0, -1}));
		// System.out.println(Stream.of(0, 1).reduce(1, Math::multiplyExact));
		// visualTest();
		// godelTest();
		// factorTest();
		// powDomainTest();
		// substituteTest();
		// equivalenceSimplificationTest();
		// inverseTest();
		// compareTest();
//		tetrationTest();
// 		setOperationsTest();

		// System.out.println(ExpressionParser.parseExpression("1 / SQRT(x ^ 2 + 1)").getDomainConditions().simplified());
		// Variable a = new Variable("a");
		// LOG.ofS(a, a.pow(a)).div(a).greater(Constant.NEG_INFINITY).and(LOG.ofS(a, a.pow(a)).div(a).less((Constant.INFINITY)).and(Constant.ONE.greater(Constant.NEG_INFINITY)).and(Constant.ONE.less(Constant.INFINITY))).simplified();
		// equivalenceSimplificationTest();

		// System.out.println(x.equates(Constant.INFINITY).or(x.greater(Constant.ZERO)).simplified());
		// for (int i = 0; i <= 0b11111; i++)
		// 	System.out.println(i + " " + ((i >> 2) - 1));
		// System.out.println(new VariableStatement("he has a driver's license").not().revImplies(new VariableStatement("his age is less than 16")).not().getTable());
		// System.out.println(new VariableStatement("he has a driver's license").not().revImplies(new VariableStatement("his age is less than 16")).not().simplified().getTable());

	// = new CustomFunction(1, null, a -> {
	// 	return null;
	// }, null, null, "limit");

		// System.out.println(LIMIT.test(new LimitExpression(Variable.of("a"), Variable.of("x"), new GenericExpFunction("f")))
		// .replaceAll(", ", ",\n\t"));

		// System.out.println(CompositionParser.parse("LOG(2, x)").simplified());


		linearAlgebraTest();

	}

	private static void linearAlgebraTest() {
		Variable a = Variable.of("a");
		Variable b = Variable.of("b");
		Variable c = Variable.of("c");
		Variable d = Variable.of("d");
		SystemOfEquations t = new SystemOfEquations(
				Constant.of(3).times(a).plus(b).minus(c).equates(Constant.of(-5)),
				b.negate().equates(Constant.of(4)),
				d.equates(Constant.of(1).plus(a))
		);
		System.out.println(t);
		final ExpressionMatrix m1 = t.toMatrix();
		System.out.println(m1);
		System.out.println(m1.getColumn(2));

		Variable x = Variable.of("x");

		SystemOfEquations s = new SystemOfEquations(
				Constant.of(3).times(a.squared().sqrt()).plus(b.pow(Constant.ONE))
						.minus(c.div(Constant.of(5)).times(Constant.of(5))).equates(Constant.of(-5)));
		System.out.println(s);
		final ExpressionMatrix m2 = s.toMatrix();
		System.out.println(m2);

		System.out.println(m1.map(Expression::squared).map(Expression::simplified));
	}

	static class Property {}
	static class Element { Element(Property[] properties) {this.properties=properties;} Property[] properties; }

	// a is an element that is abstracted of a set of elements E iff for all elements e in E, a has a property which e contains
	// for element which holds a set of properties, a set of elements E,
	// an element A abstracts E ↔ ∀ properties p in A, ∀ elements e in E, ∃ a property q in e such that p = q
	// ∃ n in Z | n=0
	// !(∀ n in Z | !n=0)

	Element abstractAbstract(Element[] elements) {
		return new Element(Arrays.stream(elements).flatMap(e -> Arrays.stream(e.properties)).toArray(Property[]::new));
	}

	private static void setOperationsTest() {
		System.out.println(new UniversalStatement<NumberLiteral, Variable>(
				(QuantifiedStatementArguments<Variable>) (s -> s[0].mod(Constant.TWO).equates(Constant.ZERO)), //(Variable a, WithVariableSet<NumberLiteral, Variable> A)
				new WithVariableSet<NumberLiteral, Variable>(NumberLiteral.class, Variable.class, new NumberLiteral(2), new NumberLiteral(4)),
				true,
				Variable.of("a")).simplified());

		System.out.println(
				forAll(Variable.of("b")).in(new WithVariableSet<>(NumberLiteral.class, Variable.class, 1, 3))
										.thatVarSet((Variable b, WithVariableSet<NumberLiteral, Variable> B) -> // (Variable b, WithVariableSet B) ->
						thereExists(Variable.of("a")).in(new WithVariableSet<>(NumberLiteral.class, Variable.class, 0, 1, 2, 3))
													 .thatVarSet((Variable a, WithVariableSet<NumberLiteral, Variable> A) -> // (Variable a, WithVariableSet A) ->
								B.subset(A).iff(A.containsElement(a).and(B.containsElement(a)))
						).proven()
				).proven()
			.toString()
		);
		forAll(Variable.of("b")).in(new WithVariableSet<>(NumberLiteral.class, Variable.class, 1, 3))
								.itHolds(b -> b.equates(Constant.of(3)))
								.proven();
		forAll(Variable.of("b")).in(new WithVariableSet<>(NumberLiteral.class, Variable.class, 1, 3))
								.thatEach(b -> b[0].equates(Constant.of(3)))
								.proven();
		System.out.println(thereExists(new Variable("a"))
								   .in(new FiniteNumberSet(0, 1, 2))
								   .suchThat(a -> forAll(new Variable("b"))
														  .in(new FiniteNumberSet(9, 5))
														  .itHolds(b -> a.times(b).equates(ZERO))
														  .proven())
								   .proven());


		// System.out.println(
		// 		forAll(Var.of("person")).in()
		// );
	}

	private static void tetrationTest() {
//		Variable x = new Variable("x");
//		x.set(new NumberLiteral(2));
//		for (int i = 0; i < 100; i++) {
//			FunctionExpression f = TETRA.ofS(Constant.of(i), Constant.of(1.46));
//			System.out.println(i + ": " + f.evaluate());
//		}
//		System.out.println(TETRA.ofS(Constant.of(100), Constant.of(2)).evaluate());
		System.out.println(((Expression) CompositionParser.parse("tetra(2, ln(x))")).derive().simplified());
	}

	private static void compareTest() {
		Variable x = new Variable("x");
		// System.out.println(x.squared().less(x.squared().plus(Constant.ONE)).truth());
		// System.out.println(x.sqrt().div(x).getDomainConditions());
		new MultiRangedSet(List.of(x.less(Constant.of(4)), x.greater(Constant.of(2)))).getRanges().forEach(System.out::println);
	}

	private static void inverseTest() {
		Variable x = new Variable("x");
		Expression a = LOG.ofS(x.minus(x).plus(Constant.E), SIN.ofS(Constant.of(3).times(x.squared().plus(Constant.of(2)))));
		System.out.println(a + "\n" + a.inverse());
	}

	private static void substituteTest() { // TODO
		//
		// p == 0 || p % 1 == 0    one can substitute p == 0 in the right to get true, so it becomes p == 0 || t
		//
		Variable p = new Variable("p");
		System.out.println(p.equates(Constant.ZERO).or(p.mod(Constant.ONE).equates(Constant.ZERO)).simplified());
	}

	private static void equivalenceSimplificationTest() {
		Variable x = new Variable("x");
		Variable y = new Variable("y");
		// Arrays.stream(LogicalOperator.values())
		// 	  .map(o -> o.of(x.greaterEqual(Constant.ONE), x.less(Constant.of(2))))
		// 	  .forEach(s -> System.out.println(s + "\t->\t" + s.simplified()));
		// Stream.concat(Arrays.stream(InequalityType.values()), Arrays.stream(EquationType.values()))
		// 	  .map(t -> OR.of(x.less(Constant.of(2)), Equivalence.of(Constant.ONE, t, x)))
		// 	  .forEach(s -> System.out.println(s + "\t->\t" + s.simplified()));
		final Equation eq = x.squared()
							 .plus(x)
							 .minus(Constant.of(20))
							 .equates(x.plus(Constant.of(3)));
		System.out.println(eq + "\n" + eq.simplified());
	}

	private static void powDomainTest() {
		Expression[] parts = { Constant.ZERO, Constant.INFINITY, Constant.NEG_INFINITY, Constant.of(.5), Constant.of(-.5) };
		Variable x = new Variable("x");
		Variable p = new Variable("p");
		Arrays.stream(parts).map(e -> x.pow(e)).forEach(pow -> System.out.println(pow + " -> " + pow.getDomainConditions().simplified()));
		Arrays.stream(parts).map(e -> e.pow(p)).forEach(pow -> System.out.println(pow + " -> " + pow.getDomainConditions().simplified()));
		// final OperatorExpression pow = SINH.ofS(LN.ofS(x)).pow(x.pow(x));
		// System.out.println(pow + " -> " + pow.getDomainConditions().simplified());
	}

	private static record FactorTest(Expression exp, Expression simplified, String expString, String simplifiedString) {}

	public static void factorTest() {
		final Constant great = Constant.of(5);
		final Constant less = Constant.of(2);
		final Variable y = new Variable("y");
		final Expression exp1 = SIN.ofS(y);
		final Expression exp2 = Constant.of(2).times(y);
		final Expression exp3 = Constant.of(5).times(y);
		final List<FactorTest> tests = Stream.of(new Expression[] {
				get(less, great, false, false), get(less, exp1, false, false), get(exp1, less, false, false),
				get(less, exp2, false, false), get(exp2, less, false, false), get(exp2, exp3, false, false),
				get(less, great, false, true), get(less, exp1, false, true), get(exp1, less, false, true),
				get(less, exp2, false, true), get(exp2, less, false, true), get(exp2, exp3, false, true),
				get(less, great, true, false), get(less, exp1, true, false), get(exp1, less, true, false),
				get(less, exp2, true, false), get(exp2, less, true, false), get(exp2, exp3, true, false),
				get(less, great, true, true), get(less, exp1, true, true), get(exp1, less, true, true),
				get(less, exp2, true, true), get(exp2, less, true, true), get(exp2, exp3, true, true),
		}).map(e -> {
			final Expression simplified = e.simplified();
			return new FactorTest(e, simplified, e.toString(), simplified.toString());
		}).collect(Collectors.toList());
		int maxLen = tests.stream().map(FactorTest::expString).map(String::length).max(Integer::compareTo).orElse(0);
		int maxLenSimp = tests.stream().map(FactorTest::simplifiedString).map(String::length).max(Integer::compareTo).orElse(0);
		for (FactorTest fT : tests)
			System.out.println(fT.expString/*.substring(1, fT.expString.length() - 1)*/ + " ".repeat(maxLen + 1 - fT.expString.length()) + "-> "
							   + fT.simplifiedString/*.substring(1, fT.simplifiedString.length() - 1)*/
							   + (fT.exp.equals(fT.simplified) ? "" : " ".repeat(maxLenSimp + 1 - fT.simplifiedString.length()) + "<-"));
	}

	private static Expression get(final Expression leftPower, final Expression rightPower, boolean leftInv, boolean rightInv) {
		final Variable a = new Variable("a");
		final Variable b = new Variable("b");
		final Variable x = new Variable("x");
		return new ExpressionOperation(a, leftInv ? DIV : MUL, x.pow(leftPower)).div(new ExpressionOperation(b, rightInv ? DIV : MUL, x.pow(rightPower)));
	}

	public static void uniTest() {
		final Variable x = new Variable("x");
		final Variable y = new Variable("y");
		final UniversalStatement<NumberLiteral, Variable> test
				= forAll(x).in(new SpecialSet(SpecialSetType.Z))
						   .itHolds(e0 -> forAll(y)
												  .in(new SpecialSet(SpecialSetType.Z))
												  .itHolds(e1 -> e0.plus(e1).equates(e1.plus(e0))).proven())
						   .proven();
		System.out.println(test + "\n" + test.not());

	}

	public static void godelTest() {
		VariableStatement x = new VariableStatement('x');
		Statement s = forAll(x).in(new BooleanSet()).thatEach(args -> x.nand(new VariableStatement('y'))).proven()
							   .implies(Constant.of(3).plus(new Variable("abc")).equates(new Variable("var")));
		System.out.println(s.godelNumber());
		System.out.println(new GodelNumberFactors(s.godelNumber().getNumber())); // 25 30
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
		// System.out.println(TAN.of(x.times(Constant.TWO.pow(ASINH.of(x)))).deriveString());

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
		// VariableStatement a = new VariableStatement("a");
		for (long seed = 0; seed <= 10; seed++)
			for (int s = 15; s <= 15; s++)
				System.out.printf("%s %d,%d\n%s\n", StatementGenerator.generate(seed, s), seed, s, StatementGenerator.generate(seed, s).simplified());
		// System.out.println(StatementGenerator.generate(3L, 20, .8, .5, .5, 0, 1));
	}

	private static void expressionGeneratorTest() {
		int seedOffset = 0;
//		final int sampleSize = 30;
//		for (double i = 0.0; i < 1.01; i += 0.05) {
//			ExpressionGenerator.DEEP_CHANCE = i;
//			double avg = 0;
//			for (long seed = 0; seed < sampleSize; seed++) {
//				final Expression generation = ExpressionGenerator.generate(seed, 20, 1, 0.5, 0.5, 0.5);
//				avg += generation.depthProb();
//			}
//			System.out.println(i + " : " + avg / sampleSize);
//		}
//		ExpressionGenerator.DEEP_CHANCE = .35;
		for (long seed = 0; seed < 5; seed++) {
			final Expression generation = ExpressionGenerator.generate(seed + seedOffset++, 20, 1, 0.5, 0.5, 0.5);
			System.out.printf("%s" + " ".repeat(Math.max(1, 15)) + "%s\n", generation.toFullString(), generation.simplified());
		}
		// System.out.println((((PI.times(PI)).times(PI.times(PI))).times(((PI.times(PI)).times(PI.times(PI)))))
		// 				.times((((PI.times(PI)).times(PI.times(PI))).times(((PI.times(PI)).times(PI.times(PI)))))).depthProb());
		// System.out.println(PI.times(PI.times(PI.times(PI.times(PI.times(PI.times(PI.times(PI.times(
		// 		PI.times(PI.times(PI.times(PI.times(PI.times(PI.times(PI.times(PI))))))))))))))).depthProb());
	}

	private static double truncate(double d) {
		return Math.floor((d) * 100) / 100;
	}

	static void parseTest() {
		// System.out.println(ExpressionParser.parse(" Sin (x * Log(2, 9)) - 6 * 0 + 1 * 8 / 7 - 6")
		// 								   .simplified());
		// for (String s : new String[] { "a", "~!!!~not a", "!(a_1 & a_2)", "abc -> b & C | d ^ e", "a & !b", "a ^ (b^C   nand d) | a", "(a == z)", "a & (((b & d)))" })
		// 	System.out.println(s + " ".repeat(25 - s.length()) + StatementParser.parseStatement(s));

		// System.out.println(Parser.parseStatement("a & (!a & a)"));

		// for (String s : new String[] {
		// 		"4", "2 * 6", "-7.598/1/8", "(1 + 2) * (4 - 3)", " LOG  (2 ) ", "( 4 ) + (   2* ( 55))",
		// "sin(3 + asinh(.5)", "2 + (3))", "()", "((", "()", ")", "(", "))", "()()", "2 ** 3", "2 . 5",
		// 			"Sin(3)", "sin(3)", "SIN(3)", "sIN(3)", "si(3)",
		// "4 - -3", "SiN()", "cos(sin)", "atan3", ""
		// })
		// 	System.out.println(s + "\t" + ExpressionParser.parseAndExcept(s));
		// for (;;) {
		// 	String s = ExpressionGenerator.generate(10).toFullString();
		// 	try { ExpressionParser.parseExpression(s); }
		// 	catch (Exception e) {
		// 		System.out.println(s + "\t" + e.getMessage());
		// 		break;
		// 	}
		// }

		// for (String s : new String[] {
		// 	"!t", "t&c&   t", "t & c", "t & (c)", "!t", "!(t)", "(t) & (c)",
		// 		"t &( c & t | ( t & c ) ) | t",
		// 		"(   )", "(  (", "(  )", ")", "( ", ") )", "( ) ( )", "",
		// 	"t OR c AND ((5 = 6) | (2 = 2))"
		// })
		int runningStrMax = 0;
		for (int i = 0; i < 10; i++) {
			String s = CompositionParser.generate(5);
			if (s.length() > runningStrMax - 2)
				runningStrMax = s.length() + 4;
			try {
				final Composition parse = CompositionParser.parse(s);
				String spaces = " ".repeat(10);
				try {

					String ext = parse instanceof Expression exp
										 ? exp.getDomainConditions().toString() + spaces + exp.getDomainConditions().simplified().toString()
										 : "";

					System.out.println(s + spaces + parse + spaces + ext);
				} catch (Exception e) {

				}
			} catch (ParseException e) {
				i--;
			}
		}
	}

	private static void operationTest() {
		VariableStatement a = new VariableStatement("a");
		VariableStatement b = new VariableStatement("b");
		LongStream.range(0L, 100L).boxed() // cherry pick an example
				  .collect(Collectors.toMap(l -> l, s -> StatementGenerator.generate(s, 15))).entrySet().stream()
				  .sorted(Comparator.comparingInt(s -> s.getValue().toFullString().length() - s.getValue().simplified().toFullString().length()))
				  .forEach(s -> System.out.println(s + " -> " + s.getValue().simplified()));

// 		for (long seed = 0L; seed <= 5; seed++) {
// 			Statement y = StatementGenerator.generate(seed, 15);
// 			// System.out.println(y.getTable());
// 			// System.out.println(y.simplified().getTable());
// 			System.out.println(y);
// 			System.out.println(y.simplified());
// 			// System.out.println((y.xnor(y.simplified())).getTable());
// //(not (not (not ((B or (not ((not (A and (not (not (not (not A)))))) xnor A))) rev_implies B)))) xnor c
// 			// break;
// 		}

		// for (Operator o : Operator.values()) {
		// 	for (Operator p : Operator.values()) {
		// 		if (p == Operator.IMPLIES || p == Operator.REV_IMPLIES) {
		// 			Statement s = new OperationStatement(new OperationStatement(b, o, a), p, b);
		// 			System.out.println(s.getTable());
		// 			System.out.println(Operator.absorptionBAB.get(o, p)
		// 													 .absorb(a, b)
		// 													 .getTable());
		// 			System.out.println(s.simplified().getTable());
		// 			System.out.println("- - -");
		// 		}
		// 	}
		// }

		// Map<Boolean[], Operator> defaults = Arrays.stream(Operator.values()).collect(Collectors.toMap(o ->
		// 								IntStream.range(0, 4).mapToObj(i -> o.operate((i & 2) == 2, (i & 1) == 1)).toArray(Boolean[]::new), Function.identity()));
		// Map<Boolean[], Operator> Stream.of().collect(Collectors.toMap(o ->
		// 				IntStream.range(0, 4).mapToObj(i -> o.operate((i & 2) == 2, (i & 1) == 1)).toArray(Boolean[]::new), Function.identity()));
		// Arrays.stream(Operator.values()) // a o (b p a)
		// 	  .map(o -> Arrays.stream(Operator.values())
		// 		  .map(p -> defaults.get(defaults.keySet().stream().filter(a -> Arrays.equals(a,
		// 		  		IntStream.range(0, 4).mapToObj(i -> o.operate((i & 2) == 2, p.operate((i & 2) == 2, (i & 1) == 1))).toArray(Boolean[]::new)))
		// 			   .findFirst().get())
		// 					  ))
		// statement -> new OperationStatement(statement.getLeft(), o, statement.getRight())
		// 	  .toArray();
		// for (Statement statement : statements)
		// 	System.out.println(TruthTable.getTable(statement));

		// System.out.println(new OperationStatement(a, REV_IMPLIES, b).xnor(new OperationStatement(a.not(), IMPLIES, b.not()).not()).getTable());
		// ArrayList<Statement> abso = new ArrayList<>();
		// for (Operator o : Operator.values())
		// 	for (Operator o1 : Operator.values())
		// 		abso.add(new OperationStatement(a, o, new OperationStatement(a, o1, b)));
		//
		// for (Statement statement : abso)
		// 	System.out.println(TruthTable.getTable(statement));
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
		System.out.println(arg.toFullString());

		System.out.println(f.toFullString());
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
		System.out.println(x.subsetImpl(y));
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
		Graph graph = new Graph(new DoubleGrid(new double[][]
												   		 { {0.0, 1.0, 1.0, 1.0, 0.0, 2.0},
														   {1.0, 0.0, 0.0, 1.0, 1.0, 2.0},
														   {1.0, 0.0, 0.0, 1.0, 0.0, 2.0},
														   {1.0, 1.0, 1.0, 0.0, 1.0, 1.0},
														   {0.0, 1.0, 0.0, 1.0, 0.0, 2.0},
														   {1.0, 1.0, 1.0, 1.0, 1.0, 0.0} }));
		System.out.println("For graph:\n" + graph);
		System.out.println("Is Eulerian trail? " + graph.isEulerianTrailDeepSearch());
		// System.out.println("Is Eulerian trail? " + graph.isEulerianTrail());
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

	private static DoubleGrid symmetricalize(double[][] values) {
		return symmetricalize(values, Math::max);
	}

	private static DoubleGrid symmetricalize(double[][] values, BiFunction<Double, Double, Double> biFunction) {
		for (int row = 0; row < values.length; row++)
			for (int col = 0; col < row; col++) {
				double decision = biFunction.apply(values[row][col], values[col][row]);
				values[row][col] = decision;
				values[col][row] = decision;
			}
		return new DoubleGrid(values);
	}

	private static void mountainProof() {
		class Mountain extends VerbalStatement {
			private boolean borderSeparatesReligon;
			public Mountain(final String name, final boolean borderSeparatesReligion) {
				super(name);
				this.borderSeparatesReligon = borderSeparatesReligion;
			}
			@Override public boolean getTruth() {
				return borderSeparatesReligon;
			}
			@Override
			public String toString() {
				return super.toString() + ", " + (borderSeparatesReligon ? "Separates" : "Doesn't Separate");
			}
		}

		class MountainVariable extends StatementImpl implements Holder<Mountain> {
			private String name;
			private Mountain mountain;

			public MountainVariable(final String name, final Mountain mountain) {
				this.name = name;
				this.mountain = mountain;
			}
			@Override
			public Mountain get() {
				return mountain;
			}
			@Override
			public void set(final Mountain mountain) {
				this.mountain = mountain;
			}
			@Override public boolean getTruth() {
				return mountain.borderSeparatesReligon;
			}
			@Override
			public String toFullString() {
				return name;
			}
		}

		final MountainVariable m = new MountainVariable("Mountain", null);
		System.out.println(forAll(m).in(
				new FiniteSet<>(Map.of("Caucus", true, "Tibet", true, "Alps", true, "Rocky", false).entrySet().stream()
								   		.map(e -> new Mountain(e.getKey(), e.getValue()))
								   		.toArray(Mountain[]::new)))
									.thatEach(args1 -> m)
									.unProven()
									.exhaustiveProofString());

		System.out.println(new ExistentialStatement<>(args1 -> m,
													  new FiniteSet<>(Map.of("Caucus", true, "Tibet", true, "Alps", true, "Rocky", false).entrySet().stream()
																		 .map(e -> new Mountain(e.getKey(), e.getValue()))
																		 .toArray(Mountain[]::new)),
													  false, m).exhaustiveProofString());
	}

}
