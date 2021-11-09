package bran.mathexprs;

import bran.run.DisplayStyle;

public enum ExpressionDisplayStyle implements DisplayStyle {

	NAME(-1), LOWERCASE_NAME(-1), SYMBOL(0), SPOKEN(1);

	public static volatile ExpressionDisplayStyle expressionStyle = ExpressionDisplayStyle.SYMBOL; // public default

	int index;

	ExpressionDisplayStyle(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

}
