package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstList;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class ListOf extends Token {
	private final Token child;

	private final char delimiter;

	private final Parser parser;

	public ListOf(Parser parser, Token child, char delimiter) {
		super();
		this.parser = parser;
		this.child = child;
		this.delimiter = delimiter;
	}

	boolean parse(int level, List<AstNode> results) {
		AstList items = new AstList();

		if (!child.parse(level, items)) {
			return false;
		}

		this.parser.consumeWhitespace();
		while (this.parser.position < this.parser.input.length()) {
			if (this.parser.input.charAt(this.parser.position) == delimiter) {
				this.parser.position++;
				if (!child.parse(level, items)) {
					return false;
				}
			} else {
				results.add(items);
				return true;
			}
			this.parser.consumeWhitespace();
		}
		results.add(items);
		return true;
	}

}