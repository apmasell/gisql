package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstLiteral;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.ast.type.Type;

public class TokenNumber extends Token {
	public static final TokenNumber self = new TokenNumber();

	private TokenNumber() {
		super();
	}

	@Override
	boolean parse(Parser parser, int level, List<AstNode> results) {
		int oldposition = parser.position;
		parser.consumeWhitespace();
		while (parser.position < parser.input.length()
				&& Character.isDigit(parser.input.charAt(parser.position))) {
			parser.position++;
		}

		try {
			results.add(new AstLiteral(Type.NumberType, Long
					.parseLong(parser.input.substring(oldposition,
							parser.position))));
			return true;
		} catch (NumberFormatException e) {
			parser.pushError("Failed to parse number.");
			return false;
		}
	}
}
