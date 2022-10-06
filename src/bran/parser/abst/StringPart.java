package bran.parser.abst;

import bran.parser.matching.Token;

public abstract class StringPart {

	//	private final EnumerableRange<Character> string = new ArrayRange<>();
	protected final String string; // TODO lazily create? (PatternBuilder greedily)
	protected final int from;
	protected final int to;

	public StringPart(String string, int from, int to) {
		this.string = string;
		this.from = from;
		this.to = to;
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

	@Override
	public String toString() {
		return string();
	}

}
