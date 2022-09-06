package bran.parser.composition;

public final class StringPart {

	private final String string;
	private final int from;
	private final int to;
	private final TokenType tokenType;
	private final String visualString;

	public StringPart(String string, int from, int to, TokenType tokenType) {
		this.string = string;
		this.from = from;
		this.to = to;
		this.tokenType = tokenType;
		visualString = string;
	}

	public StringPart(String string, int from, int to, TokenType tokenType, String visualString) {
		this.string = string;
		this.from = from;
		this.to = to;
		this.tokenType = tokenType;
		this.visualString = visualString;
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

	public TokenType tokenType() {
		return tokenType;
	}

	@Override
	public String toString() {
		return visualString;
	}

}
