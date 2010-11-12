package ca.wlu.gisql.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import name.masella.iterator.StringIterable;
import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.ast.AstApplication;
import ca.wlu.gisql.ast.AstFormatter;
import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Matches a quoted string. Escape sequences are permitted.
 */
public class TokenQuotedString extends Token<AstNode, Precedence> {
	private static abstract class FormatterBuilder {
		protected final FormatterBuilder preceeding;

		protected FormatterBuilder(FormatterBuilder preceeding) {
			super();
			this.preceeding = preceeding;
		}

		AstNode build() {
			Map<String, Integer> namedarguments = new HashMap<String, Integer>();
			prepare(namedarguments);

			StringBuilder sb = new StringBuilder();
			this.build(sb, namedarguments);
			AstNode result = new AstFormatter(sb.toString());
			if (namedarguments.size() > 0) {
				AstNode[] arguments = new AstNode[namedarguments.size() + 1];
				arguments[0] = result;
				for (Entry<String, Integer> entry : namedarguments.entrySet()) {
					arguments[entry.getValue()] = new AstName(entry.getKey());
				}
				result = new AstApplication(arguments);
			}
			return result;
		}

		protected abstract void build(StringBuilder sb,
				Map<String, Integer> namedarguments);

		protected void prepare(Map<String, Integer> namedarguments) {
			if (preceeding != null) {
				preceeding.prepare(namedarguments);
			}
		}

	}

	private static class IndexArgumentBuilder extends FormatterBuilder {

		private final int offset;

		IndexArgumentBuilder(FormatterBuilder preceeding, int offset) {
			super(preceeding);
			this.offset = offset;

		}

		@Override
		protected void build(StringBuilder sb,
				Map<String, Integer> namedarguments) {
			if (preceeding != null) {
				preceeding.build(sb, namedarguments);
			}
			sb.append('%').append(namedarguments.size() + offset).append("$s");
		}

	}

	private static class LiteralBuilder extends FormatterBuilder {
		private final StringIterable value;

		LiteralBuilder(FormatterBuilder preceeding, String value) {
			super(preceeding);
			this.value = new StringIterable(value);
		}

		@Override
		protected void build(StringBuilder sb,
				Map<String, Integer> namedarguments) {
			if (preceeding != null) {
				preceeding.build(sb, namedarguments);
			}

			for (char c : value) {
				if (c == '%') {
					sb.append("%%");
				} else {
					sb.append(c);
				}
			}
		}
	}

	private static class NamedVariableBuilder extends FormatterBuilder {

		private final String name;

		NamedVariableBuilder(FormatterBuilder preceeding, String name) {
			super(preceeding);
			this.name = name;
		}

		@Override
		protected void build(StringBuilder sb,
				Map<String, Integer> namedarguments) {
			if (preceeding != null) {
				preceeding.build(sb, namedarguments);
			}
			sb.append('%').append(namedarguments.get(name)).append("$s");
		}

		@Override
		protected void prepare(Map<String, Integer> namedarguments) {
			if (preceeding != null) {
				preceeding.prepare(namedarguments);
			}

			if (!namedarguments.containsKey(name)) {
				namedarguments.put(name, namedarguments.size() + 1);
			}
		}
	}

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

		if (!parser.hasMore() || parser.read() != '"') {
			return false;
		}

		StringBuilder sb = new StringBuilder();
		boolean success = false;
		FormatterBuilder formatter = null;

		while (parser.hasMore()) {
			char codepoint = parser.peek();

			if (codepoint == '"') {
				/* found final quote. */
				parser.next();
				success = true;
				break;
			} else if (codepoint == '\\') {
				parser.next();
				if (parser.hasMore()) {
					char c = parser.read();
					switch (c) {
					case '{':
						formatter = new LiteralBuilder(formatter, sb.toString());
						sb = new StringBuilder();

						while (true) {
							if (!parser.hasMore() || parser.peek() == '"') {
								parser
										.pushError("End of string without matching }.");
								return false;
							}
							if (parser.peek() == '}') {
								parser.next();
								break;
							}
							sb.append(parser.peek());
							parser.next();
						}

						String identifier = sb.toString();
						if (GisQL.isValidName(identifier)) {
							formatter = new NamedVariableBuilder(formatter,
									identifier);
						} else {
							try {
								int offset = Integer.parseInt(identifier);
								if (offset < 1) {
									parser.pushError("Invalid index " + offset
											+ " in place holder.");
									return false;
								}
								formatter = new IndexArgumentBuilder(formatter,
										offset);
							} catch (NumberFormatException e) {
								parser.pushError("Invalid identifier "
										+ identifier + " in place holder.");
								return false;
							}
						}

						sb = new StringBuilder();
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
				sb.append(codepoint);
				parser.next();
			}
		}

		if (!success) {
			parser.pushError("Failed to parse quoted string.");
			return false;
		}
		if (formatter == null) {
			results.add(new AstLiteral(Type.StringType, sb.toString()));
		} else {
			results.add(new LiteralBuilder(formatter, sb.toString()).build());
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
