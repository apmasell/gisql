package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Parsers an expression at a precedence level greater than the current level. */
public class TokenExpressionChild extends Token {
	public static final TokenExpressionChild self = new TokenExpressionChild();

	private TokenExpressionChild() {
		super();
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		AstNode result = parser.parseAutoExpression(level.next());
		if (result == null) {
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