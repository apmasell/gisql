package ca.wlu.gisql.parser.util;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Parser;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpression;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class LiteralList implements Parseable {

	public static final Parseable descriptor = new LiteralList();
	private static final Token[] tokens = new Token[] {
			new TokenListOf(TokenExpression.self, ','),
			TokenMatchCharacter.get('}') };

	private LiteralList() {
		super();
	}

	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	public int getPrecedence() {
		return Parser.PREC_LIST;
	}

	public boolean isMatchingOperator(char c) {
		return c == '{';
	}

	public boolean isPrefixed() {
		return true;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.print("List: {A, B, C, ...}");
	}

	public Token[] tasks() {
		return tokens;
	}

}
