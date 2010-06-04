package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Parser an expression at the same level as the current precedence. This us
 * useful in right-hand productions of the form: E_n = E_{n+1} operator E_n
 */
public class TokenExpressionRight<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {

	@SuppressWarnings("unchecked")
	private static final TokenExpressionRight self = new TokenExpressionRight();

	@SuppressWarnings("unchecked")
	public static <R, P extends Enum<P> & Nextable<P>> TokenExpressionRight<R, P> get() {
		return self;
	}

	private TokenExpressionRight() {
		super();
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		R result = parser.parseAutoExpression(knowledgebase, level);
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<expₓ>");
	}
}