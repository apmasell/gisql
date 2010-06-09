package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Find exactly some word in the input. The word is not included in the results.
 */
public class TokenReservedWord<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {
	private final String word;

	public TokenReservedWord(String word) {
		super();
		this.word = word;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		reservedwords.add(word);
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		for (int index = 0; index < word.length(); index++) {
			if (!parser.hasMore() || word.charAt(index) != parser.peek()) {
				if (parser.getCurrentTokens() > 0) {
					parser.pushError("Expected " + word + " missing.");
				}
				return false;
			}
			parser.next();
		}
		if (parser.hasMore() && Character.isJavaIdentifierPart(parser.peek())) {
			parser.pushError("Expected " + word
					+ " found with trailing garbage.");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print('⟦');
		print.print(word);
		print.print('⟧');
	}

}