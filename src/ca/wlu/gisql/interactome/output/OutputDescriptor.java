package ca.wlu.gisql.interactome.output;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

final class OutputDescriptor extends Parseable<AstNode, Precedence> {
	private static final Logger log = Logger.getLogger(OutputDescriptor.class);

	public OutputDescriptor() {
		super(new TokenMaybe<AstNode, Precedence>(TokenExpressionRight
				.<AstNode, Precedence> get()), TokenExpressionRight
				.<AstNode, Precedence> get());
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		AstNode interactome = params.get(0);
		AstNode format = params.get(1);
		AstNode filename = params.get(2);
		if (format == null) {
			try {
				format = new AstLiteralReference(FileFormat.class
						.getField("interactome"), Type.FormatType);
			} catch (SecurityException e) {
				log.error("Failed to get file format.", e);
				return null;
			} catch (NoSuchFieldException e) {
				log.error("Failed to get file format.", e);
				return null;
			}
		}

		return new AstApplication(new OutputFunction(runner), interactome,
				format, filename);
	}

	@Override
	protected String getInfo() {
		return "Write to file";
	}

	@Override
	protected char[] getOperators() {
		return new char[] { '@' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.ExpressionCharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Channel;
	}
}