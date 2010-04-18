package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.util.Precedence;

/** Matches a whole number. */
public class TokenNumber extends Token {
	public static final TokenNumber self = new TokenNumber();

	private TokenNumber() {
		super();
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		parser.mark();
		parser.consumeWhitespace();
		while (parser.hasMore() && Character.isDigit(parser.peek())) {
			parser.next();
		}
		String string = parser.stringFromMark();
		try {
			if (string.length() == 0) {
				return false;
			}
			results
					.add(new AstLiteral(Type.NumberType, Long.parseLong(string)));
			return true;
		} catch (NumberFormatException e) {
			parser.pushError("Failed to parse number.");
			return false;
		}
	}

	@Override
	public String toString() {
		return "<number>";
	}
}
