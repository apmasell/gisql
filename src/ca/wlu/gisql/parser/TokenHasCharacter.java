package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

public class TokenHasCharacter<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {
	private final char[] delimiters;

	public TokenHasCharacter(char... delimiters) {
		super();
		this.delimiters = delimiters;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		char codepoint = parser.peek();
		for (char delimiter : delimiters) {
			if (codepoint == delimiter) {
				parser.next();
				parser.consumeWhitespace();
				results.add(knowledgebase.makeBoolean(true));
				return true;
			}
		}
		results.add(knowledgebase.makeBoolean(false));
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print('[');
		for (char delimiter : delimiters) {
			print.print(delimiter);
		}
		print.print("]?");
	}

}
