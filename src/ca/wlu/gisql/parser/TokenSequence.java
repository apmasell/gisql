package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstLiteralList;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

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
			parser.consumeWhitespace();
			if (!token.parse(parser, level, subresults)) {
				return false;
			}
		}
		results.addAll(subresults);
		return true;
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		boolean first = true;
		print.print('(');
		for (Token token : tokens) {
			if (first) {
				first = false;
			} else {
				print.print(' ');
			}
			print.print(token);
		}
		print.print(')');
	}
}
