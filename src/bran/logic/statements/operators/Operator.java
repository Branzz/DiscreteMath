package bran.logic.statements.operators;

import bran.logic.statements.OperatorType;
import bran.logic.tree.Associativity;
import bran.logic.tree.ForkOperator;

import static bran.logic.statements.OperatorType.*;
import static bran.logic.statements.operators.DisplayStyle.displayStyle;

public enum Operator implements ForkOperator {
	AND((l, r) -> l && r, 			ANDS, "\u22c0", "n", "&&", "&", "intersection", "\u222a", "and"),
	NAND((l, r) -> !(l && r),		ANDS, "\u22bc", "nand", "nand", "nand"), //
	OR((l, r) -> l || r,			ORS, "\u22c1", "v", "||", "|", "union", "\u2229", "or"),
	NOR((l, r) -> !(l || r),		ORS, "\u22bd", "nor", "nor", "nor"), //
	XOR((l, r) -> l ^ r,			XORS, "\u22bb", "xor", "^", "^", "symmetric difference", "!="),
	XNOR((l, r) -> l == r,			XORS, "\u2299", "xnor", "==", "=="), //
	IMPLIES((l, r) -> !l || r,		IMPLY, "\u21d2", "->", "implies", "implies"), //
	REV_IMPLIES((l, r) -> l || !r,	REVERSE, "\u21d0", "<-", "implied by", "implied by", "reverse implies");
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

	public String toString() {
		return switch (displayStyle) {
			case NAME -> name();
			case LOWERCASE_NAME -> name().toLowerCase();
			default -> symbols[displayStyle.index()];
		};
	}

	public boolean operate(boolean left, boolean right) {
		return operable.operate(left, right);
	}

	@FunctionalInterface
	interface Operable {
		boolean operate(boolean left, boolean right);
	}

}
