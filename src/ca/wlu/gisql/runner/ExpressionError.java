package ca.wlu.gisql.runner;

/** Represents an error in a specific context (a part of the expression). */
public class ExpressionError {
	private final ExpressionContext context;

	private final Exception exception;

	private final String message;

	public ExpressionError(ExpressionContext context, String message,
			Exception exception) {
		super();
		this.context = context;
		this.message = message;
		this.exception = exception;
	}

	public ExpressionContext getContext() {
		return context;
	}

	public Exception getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}
}
