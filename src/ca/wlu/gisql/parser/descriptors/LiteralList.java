package ca.wlu.gisql.parser.descriptors;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.Token;
import ca.wlu.gisql.parser.TokenExpressionRight;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class LiteralList extends Parseable {

	public static final Parseable descriptor = new LiteralList();
	private static final Token[] tokens = new Token[] {
			new TokenListOf(TokenExpressionRight.self, ','),
			TokenMatchCharacter.get(']') };

	private LiteralList() {
		super();
	}

	@Override
	public AstNode construct(ExpressionRunner runner, List<AstNode> params,
			Stack<ExpressionError> error, ExpressionContext context) {
		return params.get(0);
	}

	@Override
	protected String getInfo() {
		return "List";
	}

	@Override
	public char[] getOperators() {
		return new char[] { '[' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}

	@Override
	public Token[] tasks() {
		return tokens;
	}

}
