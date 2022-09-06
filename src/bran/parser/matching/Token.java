package bran.parser.matching;

public interface Token<T> {

	// long typeId(); // same for each Token of a given type????
	boolean matches(String token);
	// Function<String, Boolean> matcher;

	// Set<Token> children;

	default boolean equalsToken(Token other) {
		return this.equals(other) || equalSubToken(other);
	}

	/**
	 * @return if child is a subtype of this Token or the same type
	 */
	boolean equalSubToken(Token child);

}
