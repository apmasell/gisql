package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.GisQL;
import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Matches a valid Java identifier. */
public class TokenName<R, P extends Enum<P> & Nextable<P>> extends Token<R, P> {

	@SuppressWarnings("unchecked")
	private static final Token self = new TokenName();

	@SuppressWarnings("unchecked")
	public static <R, P extends Enum<P> & Nextable<P>> Token<R, P> get() {
		return self;
	}

	protected TokenName() {
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		StringBuilder sb = new StringBuilder();

		while (parser.hasMore()) {
			char codepoint = parser.peek();

			if (sb.length() == 0 ? GisQL.isValidIdentifierStart(codepoint)
					: GisQL.isValidIdentifierPart(codepoint)) {
				parser.next();
				sb.append(codepoint);
			} else {
				break;
			}
		}

		if (sb.length() == 0) {
			if (parser.getCurrentTokens() > 0) {
				parser.pushError("Expected name missing.");
			}
			return false;
		}
		String name = sb.toString();
		if (knowledgebase.isReservedWord(name)) {
			return false;
		} else {
			results.add(knowledgebase.makeName(parser, name));
			return true;
		}
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<identifier>");
	}

}