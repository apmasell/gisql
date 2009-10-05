package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;

public class TokenQuotedString extends Token {
	public static final TokenQuotedString self = new TokenQuotedString();

	private TokenQuotedString() {
		super();
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
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
					success = sb.length() != 0;
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
			parser.pushError("Failed to parse quoted string.");
			return false;
		}
		results.add(new AstLiteral(Type.StringType, sb.toString()));
		return true;
	}

}