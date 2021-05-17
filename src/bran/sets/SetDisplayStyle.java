package bran.sets;

import bran.run.DisplayStyle;

public enum SetDisplayStyle implements DisplayStyle {

	NAME(-1), LOWERCASE_NAME(-1), SET(4), SET_SYMBOL(5);

	public static SetDisplayStyle setStyle = SetDisplayStyle.SET;

	int index;

	SetDisplayStyle(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}

}
