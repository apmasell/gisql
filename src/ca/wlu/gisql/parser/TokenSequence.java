package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

public class TokenSequence extends Token {

	private final Token[] tokens;

	public TokenSequence(Token... tokens) {
		this.tokens = tokens;
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {

		AstLiteralList subresults = new AstLiteralList();
		for (Token token : tokens) {
			if (!token.parse(parser, level, subresults)) {
				return false;
			}
		}
		results.add(subresults);
		return true;
	}
}
