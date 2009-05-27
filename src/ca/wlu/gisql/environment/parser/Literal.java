package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Literal extends NextTask {
	private final Parser parser;

	private final char c;

	public Literal(Parser parser, char c) {
		super();
		this.parser = parser;
		this.c = c;
	}

	boolean parse(int level, List<Object> results) {
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