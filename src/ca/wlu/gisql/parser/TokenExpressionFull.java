package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parser an expression level starting at the root precedence level. */
public class TokenExpressionFull extends Token {
	private final Character end;

	public TokenExpressionFull(Character end) {
		super();
		this.end = end;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		AstNode result = end == null ? parser.parseAutoExpression(Precedence
				.expression()) : parser.parseExpression(end, Precedence
				.expression());
		if (result == null) {
			return false;
		}
		results.add(result);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<exp₀>");
	}
}