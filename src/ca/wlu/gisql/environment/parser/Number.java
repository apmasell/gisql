package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstInteger;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Number extends Token {
	public static final Number self = new Number();

	private Number() {
		super();
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		parser.consumeWhitespace();
		while (parser.position < parser.input.length()
				&& Character.isDigit(parser.input.charAt(parser.position))) {
			parser.position++;
		}

		try {
			results.add(new AstInteger(Long.parseLong(parser.input.substring(
					oldposition, parser.position))));
			return true;
		} catch (NumberFormatException e) {
			parser.error.push("Failed to parse number. Position: "
					+ oldposition);
			return false;
		}
	}
}
