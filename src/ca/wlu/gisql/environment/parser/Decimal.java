package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Decimal extends NextTask {

	private final Parser parser;

	public Decimal(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<Object> results) {
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
			results.add(new Double(parser.input.substring(oldposition,
					parser.position)));
			return true;
		} catch (NumberFormatException e) {
			this.parser.error.push("Failed to parse double. Position: "
					+ oldposition);
			return false;
		}
	}

}