package bran.parser.abst;

import bran.parser.matching.Token;

@Deprecated
public class TypelessStringPart extends StringPart {

	public TypelessStringPart(String string, int from, int to) {
		super(string, from, to);
	}

	public <T> TypedStringPart<T> casted(T actual, Token<T> tokenType) {
		return new TypedStringPart<T>(string, from, to, actual, tokenType);
	}

}
