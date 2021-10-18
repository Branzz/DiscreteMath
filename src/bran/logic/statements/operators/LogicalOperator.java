package bran.logic.statements.operators;

import bran.logic.statements.*;
import bran.tree.Associativity;
import bran.tree.ForkOperator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static bran.logic.statements.StatementDisplayStyle.statementStyle;
import static bran.logic.statements.StatementOperatorType.*;
import static bran.logic.statements.VariableStatement.CONTRADICTION;
import static bran.logic.statements.VariableStatement.TAUTOLOGY;
import static bran.logic.statements.operators.LogicalOperator.AbsorbedOperationStatement.ofAbsorbed;
import static bran.sets.SetDisplayStyle.setStyle;
import static bran.sets.numbers.godel.GodelNumberSymbols.*;
import static java.util.stream.Collectors.toMap;

public enum LogicalOperator implements ForkOperator {
	OR		   ((l, r) -> l || r,	 ORS,	  true,  (l, r) -> new Object[] { l, LOGICAL_OR, r }, "\u22c1", "v", "||", "|", "union", "\u2229", "or"),
	NOR 	   ((l, r) -> !(l || r), ORS,	  true,  (l, r) -> new Object[] { LOGICAL_NOT, LEFT, OR.buffer(l, r), RIGHT } , "\u22bd", "nor", "nor", "nor"),
	IMPLIES	   ((l, r) -> !l || r,	 IMPLY,	  false, (l, r) -> new Object[] { l, IF_THEN, r }, "\u21d2", "->", "implies", "implies"),
	REV_IMPLIES((l, r) -> l || !r, 	 REVERSE, false, (l, r) -> new Object[] { r, IF_THEN, l }, "\u21d0", "<-", "implied by", "implied by", "reverse implies"),
	NAND	   ((l, r) -> !(l && r), ANDS,	  true,  (l, r) -> new Object[] { LOGICAL_NOT, LEFT, l, RIGHT, LOGICAL_OR, LOGICAL_NOT, LEFT, r, RIGHT}, "\u22bc", "nand", "nand", "nand"),
	AND		   ((l, r) -> l && r,	 ANDS,	  true,  (l, r) -> new Object[] { LOGICAL_NOT, LEFT, NAND.buffer(l, r), RIGHT}, "\u22c0", "n", "&&", "&", "intersection", "\u222a", "and"),
	XOR		   ((l, r) -> l ^ r,	 XORS,	  true,  (l, r) -> new Object[] { LEFT, IMPLIES.buffer(l, r), RIGHT, LOGICAL_OR, LEFT, REV_IMPLIES.buffer(l, r), RIGHT }, "\u22bb", "xor", "^", "^", "symmetric difference", "!="),
	XNOR	   ((l, r) -> l == r,	 XORS,	  true,  (l, r) -> new Object[] { LOGICAL_NOT, LEFT, XOR.buffer(l, r), RIGHT }, "\u2299", "xnor", "==", "==");
	// NOT(-1, "\u00ac", "~", "!", "~", "complement", "\\", "not"),
	// EQUIVALENT(-1, "\u8801", "=", "equivalent to", "equivalent to", "equals"),

	/*
	ONLY_LEFT((l, r) -> l, ANDS, null, ""),
	ONLY_RIGHT((l, r) -> r, ANDS, null, ""),
	NOT_LEFT((l, r) -> !l, ANDS, null, ""),
	NOT_RIGHT((l, r) -> !r, ANDS, null, ""),
	TAUT((l, r) -> true, ANDS, null, ""),
	CONT((l, r) -> false, ANDS, null, ""),
	NOT_IMPLIES((l, r) -> l && !r, IMPLY, null, ""),
	NOT_REVERSE_IMPLIES((l, r) -> !l && r, REVERSE, null, "");
	*/

	public static final int MIN_ORDER = 2;
	public static final int MAX_ORDER = 5;

	private final String[] symbols;
	private final StatementOperatorType operatorType;
	private final Operable operable;
	private final boolean commutative;
	private final GodelBuffer godelBuffer;

	private LogicalOperator inverse;

	static {
		OR.inverse = NOR;
		NOR.inverse = OR;
		// IMPLIES.inverse = NOT_IMPLIES;
		// REV_IMPLIES.inverse = NOT_REVERSE_IMPLIES;
		NAND.inverse = AND;
		AND.inverse = NAND;
		XOR.inverse = XNOR;
		XNOR.inverse = XOR;
	}

	// private static final AbsorptionOperationStatement[][] absorptionOperators =
	// 		new Operator[][] {
	// 	{ }
	// };

	LogicalOperator(Operable operable, StatementOperatorType operatorType, boolean commutative, GodelBuffer godelBuffer, String... symbols) {
		this.operable = operable;
		this.operatorType = operatorType;
		this.commutative = commutative;
		this.godelBuffer = godelBuffer;
		this.symbols = symbols;
	}

	public String[] getSymbols() {
		return symbols;
	}

	public int getOrder() {
		return operatorType.precedence();
	}

	@Override
	public int maxOrder() {
		return MAX_ORDER;
	}

	@Override
	public int minOrder() {
		return MIN_ORDER;
	}

	@Override
	public Associativity getDirection() {
		return operatorType.associativity();
	}

	public LogicalOperator not() {
		return inverse;
	}

	private String getSymbol(int index) {
		try {
			return symbols[index];
		} catch (IndexOutOfBoundsException e) {
			return name();
		}
	}

	public String toString() {
		return switch (statementStyle) {
			case NAME -> name();
			case LOWERCASE_NAME -> name().toLowerCase();
			default -> getSymbol(statementStyle.index());
		};
	}

	public String toString(boolean sets) {
		if (!sets)
			return toString();
		return switch (setStyle) {
			case NAME -> name();
			case LOWERCASE_NAME -> name().toLowerCase();
			default -> getSymbol(setStyle.index());
		};
	}

	public boolean isCommutative() {
		return commutative;
	}

	@FunctionalInterface
	interface Operable {
		boolean operate(boolean left, boolean right);
	}

	public boolean operate(boolean left, boolean right) {
		return operable.operate(left, right);
	}

	@FunctionalInterface
	interface GodelBuffer {
		Object[] get(Statement left, Statement right);
	}

	public Object[] buffer(Statement left, Statement right) {
		return godelBuffer.get(left, right);
	}

	@FunctionalInterface public interface AbsorbedOperationStatement {
		Statement absorb(Statement a, Statement b);
		static AbsorbedOperationStatement ofAbsorbed(final Statement A, final Statement B, Statement x) {
			return x instanceof OperationStatement xO ? (a, b) -> new OperationStatement(a, xO.getOperator(), b)
						: x == A ? ((a, b) -> a) : x == B ? ((a, b) -> b)
						: x instanceof LineStatement xL ? xL.getChild() instanceof OperationStatement xLC ? (a, b) -> new OperationStatement(a, xLC.getOperator(), b).not()
						: (a, b) -> (xL.getChild() == A ? a : b).not()
						: x == TAUTOLOGY ? (a, b) -> TAUTOLOGY : (a, b) ->  CONTRADICTION;
		}
	}

	/*
	 *  A o (A p B)
	 *  A o (B p A)
	 * (A o B) p B
	 * (B o A) p B
	 */
	// for implies / rev implies 2nd arg
	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionAAB = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());
	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionABA = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());
	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionABB = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());
	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionBAB = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());

	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionLeftOp = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());
	public static final MultiEnumMap<LogicalOperator, AbsorbedOperationStatement> absorptionRightOp = new MultiEnumMap<>(LogicalOperator.class, LogicalOperator.values());

	static {
		VariableStatement A = new VariableStatement("a");
		VariableStatement B = new VariableStatement("b");

		Map<Byte, Statement> ops =
			Stream.concat(Arrays.stream(LogicalOperator.values()).map(o -> new OperationStatement(A, o, B)), // every possible logic outcome
						  Stream.of(A, B, A.not(), B.not(), TAUTOLOGY, CONTRADICTION, new OperationStatement(A, IMPLIES, B).not(), new OperationStatement(A, REV_IMPLIES, B).not()))
				  .collect(toMap(s -> { // converted to just 4 bits
					  byte truth = 0b0000;
					  for (int n = 0; n <= 3; n++) {
						  A.setValue((n & 2) == 2);
						  B.setValue((n & 1) == 1);
						  if (s.truth())
							  truth |= 1 << n;
					  }
					  return truth;
				  }, s -> s));
		// Arrays.stream(Operator.values())
		// 	  .forEach(o -> Arrays.stream(Operator.values())
		// 						  .forEach(IntStream.range(0, 4).))
								  // .collect(Collectors.groupingBy(p -> p == IMPLIES || p == REV_IMPLIES))
								  // .forEach(p -> ))
		for (LogicalOperator o : LogicalOperator.values()) {
			for (LogicalOperator p : LogicalOperator.values()) {
				if (p == IMPLIES || p == REV_IMPLIES) {
					byte truthAAB = 0b0000;
					byte truthABA = 0b0000;
					byte truthABB = 0b0000;
					byte truthBAB = 0b0000;
					for (int n = 0; n <= 3; n++) {
						A.setValue((n & 2) == 2);
						B.setValue((n & 1) == 1);
						if (o.operate((n & 2) == 2, p.operate((n & 2) == 2, (n & 1) == 1)))
							truthAAB |= 1 << n;
						if (o.operate((n & 2) == 2, p.operate((n & 1) == 1, (n & 2) == 2)))
							truthABA |= 1 << n;
						if (p.operate(o.operate((n & 2) == 2, (n & 1) == 1), (n & 1) == 1))
							truthABB |= 1 << n;
						if (p.operate(o.operate((n & 1) == 1, (n & 2) == 2), (n & 1) == 1))
							truthBAB |= 1 << n;
					}
					absorptionAAB.put(o, p, ofAbsorbed(A, B, ops.get(truthAAB))); // constant
					absorptionABA.put(o, p, ofAbsorbed(A, B, ops.get(truthABA))); // constant
					absorptionABB.put(o, p, ofAbsorbed(A, B, ops.get(truthABB))); // constant
					absorptionBAB.put(o, p, ofAbsorbed(A, B, ops.get(truthBAB))); // constant
					// System.out.println(o + " " + p + ":" +
					// 				   absorptionAAB.get(o, p).absorb(A, B) + ";" +
					// 				   absorptionABA.get(o, p).absorb(A, B) + ";" +
					// 				   absorptionABB.get(o, p).absorb(A, B) + ";" +
					// 				   absorptionBAB.get(o, p).absorb(A, B));
				} else {
					// if (o == NAND)
					// 	System.out.println();
					byte truthLeft  = 0b0000;
					byte truthRight = 0b0000;
					for (int n = 0; n <= 3; n++) {
						A.setValue((n & 2) == 2);
						B.setValue((n & 1) == 1);
						// if (o.operate(p.operate((n & 2) == 2, (n & 1) == 1), (n & 1) == 1))
						// 	truthLeft |= 1 << n;
						if (o.operate((n & 2) == 2, p.operate((n & 2) == 2, (n & 1) == 1)))
							truthRight |= 1 << n;
						if (p.operate(o.operate((n & 2) == 2, (n & 1) == 1), (n & 1) == 1))
							truthLeft |= 1 << n;
						// if (p.operate((n & 2) == 2, o.operate((n & 2) == 2, (n & 1) == 1)))
						// 	truthRight |= 1 << n;
					}
					absorptionLeftOp.put(o, p, ofAbsorbed(A, B, ops.get(truthLeft)));
					absorptionRightOp.put(o, p, ofAbsorbed(A, B, ops.get(truthRight)));
					// if (absorptionLeftOp.get(o, p).absorb(A, B).equals(absorptionRightOp.get(o, p).absorb(A, B)))
					// System.out.println(o + " " + p + "-" +
					// 				   absorptionLeftOp.get(o, p).absorb(A, B) + " " +
					// 				   absorptionRightOp.get(o, p).absorb(A, B));

				}

				// System.out.println(s + "=" + x);
			}
		}
		// rightOperators.forEach((key, value) -> System.out.println(key + " = " + value.absorb(A, B)));
	}

}
