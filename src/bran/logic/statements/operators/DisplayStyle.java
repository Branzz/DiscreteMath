package bran.logic.statements.operators;

public enum DisplayStyle {

	NAME(-1), LOWERCASE_NAME(-1), MATH(0), CONVENTIONAL(1), JAVA_LOGICAL(2), JAVA_BITWISE(3), SET(4), SET_SYMBOL(5);

	public static DisplayStyle displayStyle = DisplayStyle.MATH;

	int index;

	DisplayStyle(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

	public static String spaceBufferForNot() {
		return displayStyle == DisplayStyle.NAME || displayStyle == DisplayStyle.LOWERCASE_NAME ? " " : "";
	}

}