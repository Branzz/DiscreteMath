package bran.parser.abst;

import bran.parser.matching.Token;

public class TypedStringPart<T> {

	StringPart<T> stringPart;
	private final Token<T> tokenType;
	private final T actual;

	public TypedStringPart(StringPart<T> stringPart, Token<T> tokenType, T actual) {
		this.stringPart = stringPart;
		this.tokenType = tokenType;
		this.actual = actual;
	}

}
