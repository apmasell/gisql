package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.ParserKnowledgebase;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parses a bracketed subexpression. */
public class BracketedExpressionDescriptor implements Parseable {
	public static final Parseable descriptor = new BracketedExpressionDescriptor();

	private static final Token[] tokens = new Token[] { new TokenExpressionFull(
			')') };

	private BracketedExpressionDescriptor() {
		super();
	}

	@Override
	public AstNode construct(UserEnvironment environment, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public boolean isMatchingOperator(char c) {
		return c == '(';
	}

	@Override
	public Boolean isPrefixed() {
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<ParserKnowledgebase> print) {
		print.println("Control Order of Operations: (expression)");
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
