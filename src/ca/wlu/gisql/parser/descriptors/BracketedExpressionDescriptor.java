package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

/** Parses a bracketed subexpression. */
public class BracketedExpressionDescriptor extends Parseable {
	public static final Parseable descriptor = new BracketedExpressionDescriptor();

	private static final Token[] tokens = new Token[] { new TokenExpressionFull(
			')') };

	private BracketedExpressionDescriptor() {
		super();
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	@Override
	protected String getInfo() {
		return "Control Order of Operations";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '(' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	@Override
	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
