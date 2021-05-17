package bran.logic.statements;

public enum StatementDisplayStyle {

	NAME(-1), LOWERCASE_NAME(-1), MATH(0), CONVENTIONAL(1), JAVA_LOGICAL(2), JAVA_BITWISE(3);

	public static StatementDisplayStyle statementStyle = StatementDisplayStyle.MATH;

	int index;

	StatementDisplayStyle(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	public static String spaceBufferForNot() {
		return statementStyle == StatementDisplayStyle.NAME || statementStyle == StatementDisplayStyle.LOWERCASE_NAME ? " " : "";
	}

}