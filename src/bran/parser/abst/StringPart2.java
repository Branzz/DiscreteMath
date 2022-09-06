package bran.parser.abst;

import bran.parser.matching.Token;

public class StringPart2<T> {

	private final String string;
	private final int from;
	private final int to;
	private final boolean typeless;
	private final Token<T> tokenType;
	private final T actual;

	public StringPart2(String string, int from, int to, T actual, Token<T> tokenType) {
		this.string = string;
		this.from = from;
		this.to = to;
		this.typeless = tokenType == null;
		this.actual = actual;
		this.tokenType = tokenType;
	}

	public StringPart2(String string, int from, int to) {
		this(string, from, to, null, null);
	}

	public String string() {
		return string;
	}

	public int from() {
		return from;
	}

	public int to() {
		return to;
	}

	public Token tokenType() {
		return tokenType;
	}

	@Override
	public String toString() {
		return string();
	}

	public T actual() {
		return actual;
	}

}
