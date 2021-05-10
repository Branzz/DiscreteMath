package bran.exceptions;

public class ParseException extends RuntimeException {

	public ParseException(String message) {
		super(message);
	}

	public ParseException(String message, Object... arguments) {
		super(String.format(message, arguments));
	}

}
