package ca.wlu.gisql.interactome.output;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenMaybe;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

final class OutputDescriptor implements Parseable {
	private static final Token[] tokens = new Token[] {
			new TokenMaybe(TokenExpressionRight.self),
			TokenExpressionRight.self };

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {

		AstNode interactome = params.get(0);
		AstNode format = params.get(1);
		AstNode filename = params.get(2);
		if (format == null) {
			format = new AstLiteral(Type.FormatType, FileFormat.interactome);
		}

		return new AstApplication(AbstractOutput.function, interactome, format,
				filename);
	}

	public Precedence getPrecedence() {
		return Precedence.Assignment;
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