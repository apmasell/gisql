package ca.wlu.gisql.environment.parser;

import java.util.List;

public class Number extends NextTask {

	private final Parser parser;

	public Number(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<Object> results) {
		int oldposition = this.parser.position;
		parser.consumeWhitespace();
		while (parser.position < parser.input.length()
				&& Character.isDigit(parser.input.charAt(parser.position))) {
			parser.position++;
		}

		try {
			results.add(new Integer(parser.input.substring(oldposition,
					parser.position)));
			return true;
		} catch (NumberFormatException e) {
			this.parser.error.push("Failed to parse number. Position: "
					+ oldposition);
			return false;
		}
	}
}
