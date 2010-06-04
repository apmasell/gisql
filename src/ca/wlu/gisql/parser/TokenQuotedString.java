package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstFormatter;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Matches a quoted string. Escape sequences are permitted.
 */
public class TokenQuotedString extends Token<AstNode, Precedence> {
	public static final TokenQuotedString self = new TokenQuotedString();

	private TokenQuotedString() {
		super();
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<AstNode, Precedence> knowledgebase,
			Parser parser, Precedence level, List<AstNode> results) {
		parser.consumeWhitespace();
		StringBuilder sb = new StringBuilder();
		boolean success = false;
		boolean formatter = false;

		if (!parser.hasMore() || parser.read() != '"') {
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
					case '{':
						if (formatter == false) {
							int offset = 0;
							while (offset < sb.length()
									&& (offset = sb.indexOf("%", offset)) > 0) {
								sb.insert(offset + 1, '%');
								offset += 2;
							}
						}
						formatter = true;

						int value = 0;
						while (Character.isDigit(parser.peek())) {
							value = value * 10 + parser.peek() - '0';
							if (parser.hasMore()) {
								parser.next();
							} else {
								parser
										.pushError("End of string without matching }.");
								return false;
							}
						}
						if (parser.peek() != '}') {
							parser.pushError("Invalid character "
									+ parser.peek() + " in place holder.");
							return false;
						}
						parser.next();
						if (value == 0) {
							parser.pushError("\\{0} not allowed in strings.");
							return false;
						}

						sb.append('%').append(value).append("$s");
						break;
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
		if (formatter) {
			results.add(new AstFormatter(sb.toString()));
		} else {
			results.add(new AstLiteral(Type.StringType, sb.toString()));
		}
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
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<string>");
	}
}
