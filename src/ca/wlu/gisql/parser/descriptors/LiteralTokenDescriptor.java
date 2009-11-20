package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class LiteralTokenDescriptor implements Parseable {

	private final Token[] tokens;

	public LiteralTokenDescriptor(Token token) {
		super();
		tokens = new Token[] { token };
	}

	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	public boolean isMatchingOperator(char c) {
		return false;
	}

	public Boolean isPrefixed() {
		return null;
	}

	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
	}

	public Token[] tasks() {
		return tokens;
	}

}
