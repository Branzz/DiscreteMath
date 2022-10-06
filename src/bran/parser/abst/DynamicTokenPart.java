package bran.parser.abst;

import bran.parser.matching.Token;

import java.util.HashMap;
import java.util.Map;

public class DynamicTokenPart {

	private final int startIndex;
	private final Map<Integer, Map<Token, TypelessStringPart>> found;

	public DynamicTokenPart(int startIndex) {
		this.startIndex = startIndex;
		this.found = new HashMap<>();
	}

	public void found(TypelessStringPart stringPart) {
		found.computeIfAbsent(stringPart.to(), k -> new HashMap<>())
			 // .put(stringPart.token(), stringPart)
		;
	}

	public void foundToken(int to, Token[] tokens) {
		int j = startIndex;
		for (int i = 0; i < tokens.length; i++) {
			// for (; j < )
		}
		TypelessStringPart stringPart = found.get(to).get(tokens);
	}

	public int getStartIndex() {
		return startIndex;
	}

}
