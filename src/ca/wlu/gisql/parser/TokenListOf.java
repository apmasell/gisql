package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstList;
import ca.wlu.gisql.ast.AstNode;

/**
 * Finds a list of tokens delimited by a specific character. Whitespace may
 * appear around the delimiter. This must match at least on token. That is, it
 * behaves like the regular expression + operator.
 */
public class TokenListOf extends Token {
	private final Token child;

	private final char delimiter;

	public TokenListOf(Token child, char delimiter) {
		super();
		this.child = child;
		this.delimiter = delimiter;
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		AstList items = new AstList();

		if (!child.parse(parser, level, items)) {
			return false;
		}

		parser.consumeWhitespace();
		while (parser.position < parser.input.length()) {
			if (parser.input.charAt(parser.position) == delimiter) {
				parser.position++;
				parser.consumeWhitespace();
				if (!child.parse(parser, level, items)) {
					return false;
				}
			} else {
				results.add(items);
				return true;
			}
			parser.consumeWhitespace();
		}
		results.add(items);
		return true;
	}

}