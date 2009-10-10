package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

public class TokenExpressionRight extends Token {

	public static final TokenExpressionRight self = new TokenExpressionRight();

	private TokenExpressionRight() {
		super();
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		AstNode result = parser.parseAutoExpression(level);
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}
}