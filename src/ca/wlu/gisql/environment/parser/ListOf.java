package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class ListOf extends Token {
	private final Token child;

	private final char delimiter;

	public ListOf(Token child, char delimiter) {
		super();
		this.child = child;
		this.delimiter = delimiter;
	}

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