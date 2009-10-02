package ca.wlu.gisql.parser.util;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.runner.ExpressionRunListener;

public abstract class GenericParseListener implements ExpressionRunListener {
	private final Type expected;

	public GenericParseListener(Type expected) {
		super();
		this.expected = expected;
	}

	abstract protected void assertWarning(Parser parser, String message);

	public void parseFailed(Parser parser) {
		assertWarning(parser, "unable to parse line");
	}

	public void renderFailure(Parser parser, String message) {
		assertWarning(parser, "rendering failed");
	}

	public void resolutionFailed(Parser parser) {
		assertWarning(parser, "name resolution failed");
	}

	public void typeCheckFailed(Parser parser) {
		assertWarning(parser, "type checking failed");
	}

	public void typeVerifyFailed(Parser parser) {
		assertWarning(parser, "expected type " + expected.toString());
	}

}
