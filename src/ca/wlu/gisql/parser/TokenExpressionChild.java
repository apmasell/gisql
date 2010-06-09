package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parsers an expression at a precedence level greater than the current level. */
public class TokenExpressionChild<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {
	@SuppressWarnings("unchecked")
	private static final TokenExpressionChild self = new TokenExpressionChild();

	@SuppressWarnings("unchecked")
	public static <R, P extends Enum<P> & Nextable<P>> TokenExpressionChild<R, P> get() {
		return self;
	}

	private TokenExpressionChild() {
		super();
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		int olderror = parser.error.size();
		R result = parser.parseAutoExpression(knowledgebase, level.next());
		if (result == null) {
			if (olderror == parser.error.size()) {
				parser.pushError("Failed to parse subexpression.");
			}
			return false;
		}
		results.add(result);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<expₓ₊₁>");
	}

}