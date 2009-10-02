package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

public class TokenSubExpression extends Token {
	public static final TokenSubExpression self = new TokenSubExpression();

	private TokenSubExpression() {
		super();
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		AstNode result = parser.parseAutoExpression(level + 1);
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}
}