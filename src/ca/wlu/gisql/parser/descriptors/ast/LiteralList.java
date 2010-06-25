package ca.wlu.gisql.parser.descriptors.ast;

import java.util.List;
import java.util.Stack;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.parser.Parseable;
import ca.wlu.gisql.parser.TokenExpressionFull;
import ca.wlu.gisql.parser.TokenListOf;
import ca.wlu.gisql.parser.TokenMatchCharacter;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunner;
import ca.wlu.gisql.util.Precedence;

public class LiteralList extends Parseable<AstNode, Precedence> {

	public static final Parseable<AstNode, Precedence> descriptor = new LiteralList();

	private LiteralList() {
		super(new TokenListOf(new TokenExpressionFull<AstNode, Precedence>(
				Precedence.Closure, null), ','), TokenMatchCharacter
				.<AstNode, Precedence> get(']'));
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
	protected char[] getOperators() {
		return new char[] { '[' };
	}

	@Override
	public Order getParsingOrder() {
		return Order.CharacterTokens;
	}

	public Precedence getPrecedence() {
		return Precedence.Value;
	}
}
