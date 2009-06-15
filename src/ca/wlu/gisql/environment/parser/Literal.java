package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Literal extends Token {
	private final char c;

	private final Parser parser;

	public Literal(Parser parser, char c) {
		super();
		this.parser = parser;
		this.c = c;
	}

	boolean parse(int level, List<AstNode> results) {
		this.parser.consumeWhitespace();
		if (this.parser.position < this.parser.input.length()
				&& c == this.parser.input.charAt(this.parser.position)) {
			this.parser.position++;
			return true;
		}
		this.parser.error.push("Expected '" + c + "' missing. Position: "
				+ this.parser.position);
		return false;
	}

}