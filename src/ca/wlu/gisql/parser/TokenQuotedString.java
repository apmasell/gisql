package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

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
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		parser.consumeWhitespace();
		StringBuilder sb = new StringBuilder();
		boolean success = false;

		if (parser.read() != '"') {
			return false;
		}

		while (parser.hasMore()) {
			char codepoint = parser.peek();

			if (codepoint == '"') {
				/* found final quote. */
				parser.next();
				success = sb.length() != 0;
				break;
			} else if (codepoint == '\\') {
				parser.next();
				if (parser.hasMore()) {
					char c = parser.read();

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
					parser.next();

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
		while (digits > 0 && parser.hasMore()) {
			int value = Character.digit(parser.peek(), 16);
			if (value == -1) {
				return null;
			}
			result = result * 0x10 + value;
			parser.next();
			digits--;
		}
		if (digits == 0 && result != 0) {
			return result;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return "<string>";
	}
}