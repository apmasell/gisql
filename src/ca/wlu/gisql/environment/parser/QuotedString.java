package ca.wlu.gisql.environment.parser;

import java.util.List;

import ca.wlu.gisql.environment.parser.ast.AstNode;
import ca.wlu.gisql.environment.parser.ast.AstString;

public class QuotedString extends Token {
	public static final QuotedString self = new QuotedString();

	private QuotedString() {
		super();
	}

	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
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
			parser.error.push("Failed to parse quoted string. Position: "
					+ oldposition);
			return false;
		}
		results.add(new AstString(sb.toString()));
		return true;
	}

}