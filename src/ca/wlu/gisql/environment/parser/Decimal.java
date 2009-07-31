package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Decimal extends Token {
	public static final Decimal self = new Decimal();

	private Decimal() {
		super();
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		parser.consumeWhitespace();
		while (parser.position < parser.input.length()
				&& Character.isDigit(parser.input.charAt(parser.position))) {
			parser.position++;
		}
		if (parser.position < parser.input.length()
				&& parser.input.charAt(parser.position) == '.') {
			parser.position++;
			while (parser.position < parser.input.length()
					&& Character.isDigit(parser.input.charAt(parser.position))) {
				parser.position++;
			}
		}

		try {
			results.add(new AstDouble(Double.parseDouble(parser.input
					.substring(oldposition, parser.position))));
			return true;
		} catch (NumberFormatException e) {
			parser.error.push("Failed to parse double. Position: "
					+ oldposition);
			return false;
		}
	}

}