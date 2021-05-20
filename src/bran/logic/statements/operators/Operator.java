package bran.logic.statements.operators;

import bran.logic.statements.*;
import bran.tree.Associativity;
import bran.tree.ForkOperator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static bran.logic.statements.OperatorType.*;
import static bran.logic.statements.StatementDisplayStyle.statementStyle;
import static bran.logic.statements.VariableStatement.CONTRADICTION;
import static bran.logic.statements.VariableStatement.TAUTOLOGY;
import static bran.logic.statements.operators.Operator.AbsorbedOperationStatement.ofAbsorbed;
import static bran.sets.SetDisplayStyle.setStyle;
import static java.util.stream.Collectors.toMap;

public enum Operator implements ForkOperator {
	AND((l, r) -> l && r, 			ANDS, "\u22c0", "n", "&&", "&", "intersection", "\u222a", "and"),
	OR((l, r) -> l || r,			ORS, "\u22c1", "v", "||", "|", "union", "\u2229", "or"),
	NAND((l, r) -> !(l && r),		ANDS, "\u22bc", "nand", "nand", "nand"),
	NOR((l, r) -> !(l || r),		ORS, "\u22bd", "nor", "nor", "nor"),
	XOR((l, r) -> l ^ r,			XORS, "\u22bb", "xor", "^", "^", "symmetric difference", "!="),
	XNOR((l, r) -> l == r,			XORS, "\u2299", "xnor", "==", "=="),
	REV_IMPLIES((l, r) -> l || !r,	REVERSE, "\u21d0", "<-", "implied by", "implied by", "reverse implies"),
	IMPLIES((l, r) -> !l || r,		IMPLY, "\u21d2", "->", "implies", "implies");
	// NOT(-1, "\u00ac", "~", "!", "~", "complement", "\\", "not"),
	// EQUIVALENT(-1, "\u8801", "=", "equivalent to", "equivalent to", "equals"),

	/*
	LEFT((l, r) -> l,					2, ""),
	RIGHT((l, r) -> r,					2, ""),
	NOT_LEFT((l, r) -> !l,				2, ""),
	NOT_RIGHT((l, r) -> !r,				2, ""),
	TAUTOLOGY((l, r) -> true,				2, ""),
	CONTRADICTION((l, r) -> false,			2, ""),
	NOT_IMPLIES((l, r) -> l && !r,			2, ""),
	NOT_REVERSE_IMPLIES((l, r) -> !l && r,	2, ""),
	 */

	public static final int MIN_ORDER = 2;
	public static final int MAX_ORDER = 5;

	private final String[] symbols;
	private final OperatorType operatorType;
	private final Operable operable;

	// private static final AbsorptionOperationStatement[][] absorptionOperators =
	// 		new Operator[][] {
	// 	{ }
	// };

	Operator(Operable operable, OperatorType operatorType, String... symbols) {
		this.operable = operable;
		this.operatorType = operatorType;
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

	@FunctionalInterface
	interface Operable {
		boolean operate(boolean left, boolean right);
	}

	public boolean operate(boolean left, boolean right) {
		return operable.operate(left, right);
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
	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionAAB = new MultiEnumMap<>(Operator.class, Operator.values());
	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionABA = new MultiEnumMap<>(Operator.class, Operator.values());
	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionABB = new MultiEnumMap<>(Operator.class, Operator.values());
	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionBAB = new MultiEnumMap<>(Operator.class, Operator.values());

	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionLeftOp = new MultiEnumMap<>(Operator.class, Operator.values());
	public static final MultiEnumMap<Operator, AbsorbedOperationStatement> absorptionRightOp = new MultiEnumMap<>(Operator.class, Operator.values());

	static {
		VariableStatement A = new VariableStatement("a");
		VariableStatement B = new VariableStatement("b");

		Map<Byte, Statement> ops =
			Stream.concat(Arrays.stream(Operator.values()).map(o -> new OperationStatement(A, o, B)), // every possible logic outcome
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
		for (Operator o : Operator.values()) {
			for (Operator p : Operator.values()) {
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
						if (o.operate(p.operate((n & 2) == 2, (n & 1) == 1), (n & 1) == 1))
							truthABB |= 1 << n;
						if (o.operate(p.operate((n & 1) == 1, (n & 2) == 2), (n & 1) == 1))
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
					byte truthLeft = 0b0000;
					byte truthRight = 0b0000;
					for (int n = 0; n <= 3; n++) {
						A.setValue((n & 2) == 2);
						B.setValue((n & 1) == 1);
						if (o.operate((n & 2) == 2, p.operate((n & 2) == 2, (n & 1) == 1)))
							truthLeft |= 1 << n;
						if (o.operate(p.operate((n & 2) == 2, (n & 1) == 1), (n & 1) == 1))
							truthRight |= 1 << n;
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
