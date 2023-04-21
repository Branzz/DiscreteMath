package bran.parser.abst;

import bran.parser.matching.Token;

@Deprecated
public class TypedStringPart<T> extends StringPart {

	private final T actual;
	private final Token<T> tokenType;

	public TypedStringPart(String string, int from, int to, T actual, Token<T> tokenType) {
		super(string, from, to);
		this.actual = actual;
		this.tokenType = tokenType;
	}

	public T actual() {
		return actual;
	}

	public Token token() {
		return tokenType;
	}

}
