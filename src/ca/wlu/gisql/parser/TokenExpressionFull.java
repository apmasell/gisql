package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.util.Nextable;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parser an expression level starting at the root precedence level. */
public class TokenExpressionFull<R, P extends Enum<P> & Nextable<P>> extends
		Token<R, P> {
	private final Character end;
	private final P start;

	public TokenExpressionFull(P start, Character end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(ParserKnowledgebase<R, P> knowledgebase, Parser parser,
			P level, List<R> results) {
		R result = end == null ? parser.parseAutoExpression(knowledgebase,
				start) : parser.parseExpression(knowledgebase, end, start);
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<exp₀>");
		if (end != null) {
			print.print(" [");
			print.print(end);
			print.print("]");
		}
	}
}