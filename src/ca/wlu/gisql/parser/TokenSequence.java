package ca.wlu.gisql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TokenSequence<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {

	private final Token<R, P>[] tokens;

	public TokenSequence(Token<R, P>... tokens) {
		this.tokens = tokens;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		for (Token<R, P> token : tokens) {
			token.addReservedWords(reservedwords);
		}
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {

		List<R> subresults = new ArrayList<R>();
		for (Token<R, P> token : tokens) {
			parser.consumeWhitespace();
			if (!token.parse(knowledgebase, parser, level, subresults)) {
				return false;
			}
		}
		results.addAll(subresults);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		boolean first = true;
		print.print('(');
		for (Token<R, P> token : tokens) {
			if (first) {
				first = false;
			} else {
				print.print(' ');
			}
			print.print(token);
		}
		print.print(')');
	}
}
