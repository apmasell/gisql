package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;

/**
 * Matches a quoted string. Escape sequences are permitted.
 */
public class TokenQuotedString extends Token {
	public static final TokenQuotedString self = new TokenQuotedString();

	private TokenQuotedString() {
		super();
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
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
					char c = parser.input.charAt(parser.position);
					parser.position++;

					switch (c) {
					case 'a':
						sb.append('\u0007');
						break;

					case 'b':
						sb.append('\b');
						break;

					case 'f':
						sb.append('\f');
						break;

					case 'n':
						sb.append('\n');
						break;

					case 'r':
						sb.append('\r');
						break;

					case 't':
						sb.append('\t');
						break;

					case 'u':
						Integer result = pullHex(parser, 4);
						if (result == null) {
							return false;
						}
						sb.appendCodePoint(result);
						break;

					case 'U':
						result = pullHex(parser, 8);
						if (result == null) {
							return false;
						}
						sb.appendCodePoint(result);
						break;

					case 'v':
						sb.append('\u000B');
						break;

					case 'x':
						result = pullHex(parser, 2);
						if (result == null) {
							return false;
						}
						sb.appendCodePoint(result);
						break;

					default:
						sb.append(c);
						break;
					}
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

	private Integer pullHex(Parser parser, int digits) {
		int result = 0;
		while (digits > 0 && parser.position < parser.input.length()) {
			int value = Character.digit(parser.input.charAt(parser.position),
					16);
			if (value == -1) {
				return null;
			}
			result = result * 0x10 + value;
			parser.position++;
			digits--;
		}
		if (digits == 0 && result != 0) {
			return result;
		} else {
			return null;
		}
	}
}