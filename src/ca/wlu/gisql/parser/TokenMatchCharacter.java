package ca.wlu.gisql.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Find exactly some character. The character is not included in the results. */
public class TokenMatchCharacter<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {
	@SuppressWarnings("unchecked")
	private static final Map<Character, TokenMatchCharacter> literals = new HashMap<Character, TokenMatchCharacter>();

	@SuppressWarnings("unchecked")
	public static <R, P extends Enum<P> & Nextable<P>> TokenMatchCharacter<R, P> get(
			char c) {
		TokenMatchCharacter literal = literals.get(c);
		if (literal == null) {
			literal = new TokenMatchCharacter(c);
			literals.put(c, literal);
		}
		return literal;
	}

	private final char c;

	private TokenMatchCharacter(char c) {
		super();
		this.c = c;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		parser.consumeWhitespace();
		if (parser.hasMore() && c == parser.peek()) {
			parser.next();
			return true;
		}
		parser.pushError("Expected '" + c + "' missing.");
		return false;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print('[');
		print.print(c);
		print.print(']');
	}
}