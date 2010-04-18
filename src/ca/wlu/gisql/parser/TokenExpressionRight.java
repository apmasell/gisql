package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/**
 * Parser an expression at the same level as the current precedence. This us
 * useful in right-hand productions of the form: E_n = E_{n+1} operator E_n
 */
public class TokenExpressionRight extends Token {

	public static final TokenExpressionRight self = new TokenExpressionRight();

	private TokenExpressionRight() {
		super();
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		AstNode result = parser.parseAutoExpression(level);
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