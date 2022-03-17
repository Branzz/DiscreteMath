package bran.application.draw.exprs;

public class ExpressionInputException extends Exception {

	ExpressionInputExceptionType type;

	public ExpressionInputException(String message, ExpressionInputExceptionType type) {
		super(message);
		this.type = type;
	}

}
