package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

public class TokenSequence extends Token {

	private final Token[] tokens;

	public TokenSequence(Token... tokens) {
		this.tokens = tokens;
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
		for (Token token : tokens) {
			token.addReservedWords(reservedwords);
		}
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {

		AstLiteralList subresults = new AstLiteralList();
		for (Token token : tokens) {
			if (!token.parse(parser, level, subresults)) {
				return false;
			}
		}
		results.addAll(subresults);
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Token token : tokens) {
			sb.append(token);
			sb.append(' ');
		}
		return sb.toString();
	}
}
