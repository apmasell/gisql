package ca.wlu.gisql.interactome.output;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstLiteralReference;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class OutputDescriptor implements Parseable {
	private static final Logger log = Logger.getLogger(OutputDescriptor.class);

	private static final Token[] tokens = new Token[] {
			new TokenMaybe(TokenExpressionRight.self),
			TokenExpressionRight.self };

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

	public Precedence getPrecedence() {
		return Precedence.Channel;
	}

	public boolean isMatchingOperator(char c) {
		return c == '@';
	}

	public Boolean isPrefixed() {
		return false;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print
				.println("Write to file: A @ [{summary | interactome | genome | dot | gml | graphml | adjacency | laplace}] \"filename\"");
	}

	public Token[] tasks() {
		return tokens;
	}
}