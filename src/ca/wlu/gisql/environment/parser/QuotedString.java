package ca.wlu.gisql.environment.parser;

import java.util.List;

public class QuotedString extends NextTask {

	private final Parser parser;

	public QuotedString(Parser parser) {
		this.parser = parser;
	}

	boolean parse(int level, List<Object> results) {
		int oldposition = this.parser.position;
		parser.consumeWhitespace();
		StringBuilder sb = null;
		boolean success = false;

		while (parser.position < parser.input.length()) {
			char codepoint = parser.input.charAt(parser.position);

			if (codepoint == '"') {
				parser.position++;
				if (sb == null) {
					/* first quote. */
					sb = new StringBuilder();
				} else {
					/* found final quote. */
					success = (sb.length() != 0);
					break;
				}
			} else if (codepoint == '\\') {
				parser.position++;
				if (parser.position < parser.input.length()) {
					sb.append(parser.input.charAt(parser.position));
					parser.position++;
				} else {
					success = false;
					break;
				}
			} else {
				if (sb == null) {
					success = false;
					break;
				} else {
					parser.position++;

					sb.append(codepoint);
				}
			}
		}

		if (!success) {
			this.parser.error.push("Failed to parse quoted string. Position: "
					+ oldposition);
			return false;
		}
		results.add(sb.toString());
		return true;
	}

}