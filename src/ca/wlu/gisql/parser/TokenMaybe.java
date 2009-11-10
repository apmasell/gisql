package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

/**
 * Optionally match a token. If the token is not matched, the result will be
 * null. This is equivalent to the ? operator in regular expressions.
 */
public class TokenMaybe extends Token {
	private final Token child;

	public TokenMaybe(Token child) {
		super();
		this.child = child;
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		int oldposition = parser.position;
		int errorposition = parser.error.size();
		if (child.parse(parser, level, results)) {
			return true;
		}
		results.add(null);
		parser.position = oldposition;
		parser.error.setSize(errorposition);
		return true;
	}
}