package bran.sets.numbers.godel;

public enum GodelNumberSymbols implements GodelNumber {
	LOGICAL_NOT("¬"), LOGICAL_OR("∨"), IF_THEN("⊃"), EACH("∃"), EQUALS("="), ZERO("0"), SUCCESSOR("s"),
	LEFT("("), RIGHT(")"), PUNCTUATION("'"), PLUS("+"), TIMES("*"), SYNTAX_ERROR("?");

	private final String string;
	private final int number;

	GodelNumberSymbols(final String string) {
		this.string = string;
		this.number = ordinal() + 1;
	}

	@Override
	public int number() {
		return number;
	}

	@Override
	public String toString() {
		return string;
	}

}
