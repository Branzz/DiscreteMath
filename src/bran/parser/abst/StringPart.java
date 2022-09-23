package bran.parser.abst;

import bran.parser.matching.Token;

public class StringPart<T> {

//	private final EnumerableRange<Character> string = new ArrayRange<>();
	private final String string; // TODO lazily create? (PatternBuilder greedily)
	private final int from;
	private final int to;
	private final boolean typeless;
	private final Token<T> tokenType;
	private final T actual;

	public StringPart(String string, int from, int to, T actual, Token<T> tokenType) {
		this.string = string;
		this.from = from;
		this.to = to;
		this.typeless = tokenType == null;
		this.actual = actual;
		this.tokenType = tokenType;
	}

	public StringPart(String string, int from, int to) {
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

	public StringPart<T> casted(T actual, Token<T> tokenType) {
		return new StringPart<>(string, from, to, actual, tokenType);
	}

}
