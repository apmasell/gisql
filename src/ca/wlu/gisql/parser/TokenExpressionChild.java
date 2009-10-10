package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

public class TokenExpressionChild extends Token {
	public static final TokenExpressionChild self = new TokenExpressionChild();

	private TokenExpressionChild() {
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