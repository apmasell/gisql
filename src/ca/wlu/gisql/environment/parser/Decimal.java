package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstDouble;
import ca.wlu.gisql.environment.parser.ast.AstNode;

public class Decimal extends Token {

	private final Parser parser;

	public Decimal(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<AstNode> results) {
		int oldposition = this.parser.position;
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
			this.parser.error.push("Failed to parse double. Position: "
					+ oldposition);
			return false;
		}
	}

}