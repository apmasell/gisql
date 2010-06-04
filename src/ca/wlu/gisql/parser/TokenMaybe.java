package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Optionally match a token. If the token is not matched, the result will be
 * null. This is equivalent to the ? operator in regular expressions.
 */
public class TokenMaybe<R, P extends Enum<P> & Nextable<P>> extends Token<R, P> {
	private final Token<R, P> child;

	public TokenMaybe(Token<R, P> child) {
		super();
		this.child = child;
	}

	public TokenMaybe(Token<R, P>... children) {
		this(new TokenSequence<R, P>(children));
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		child.addReservedWords(reservedwords);
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		parser.mark();
		int errorposition = parser.error.size();
		if (child.parse(knowledgebase, parser, level, results)) {
			parser.clearMark();
			return true;
		}
		results.add(null);
		parser.rewindToMark();
		parser.error.setSize(errorposition);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print(child);
		print.print('?');
	}
}