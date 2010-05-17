package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Optionally match a token. If the token is not matched, the result will be
 * null. This is equivalent to the ? operator in regular expressions.
 */
public class TokenMaybe extends Token {
	private final Token child;

	public TokenMaybe(Token child) {
		super();
		this.child = child;
	}

	public TokenMaybe(Token... children) {
		this(new TokenSequence(children));
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		child.addReservedWords(reservedwords);
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		parser.mark();
		int errorposition = parser.error.size();
		if (child.parse(parser, level, results)) {
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