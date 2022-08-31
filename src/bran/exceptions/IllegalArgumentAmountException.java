package bran.exceptions;

public class IllegalArgumentAmountException extends RuntimeException { // TODO centralize message

	public IllegalArgumentAmountException(String message) {
		super(message);
	}

	public IllegalArgumentAmountException(int input, int actual) {
		super(String.format("wrong number of arguments. Expected %d but got %d", actual, input));
	}

}
