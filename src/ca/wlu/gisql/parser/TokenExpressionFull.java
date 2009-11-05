package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;

/** Parser an expression level starting at the root precedence level. */
public class TokenExpressionFull extends Token {
	private final char end;

	public TokenExpressionFull(char end) {
		super();
		this.end = end;
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		AstNode result = parser.parseExpression(end);
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}
}