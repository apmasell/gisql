package ca.wlu.gisql.parser;

import java.util.List;

import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;

/** Matches a valid Java identifier. */
public class TokenName extends Token {

	public static final Token self = new TokenName();

	protected TokenName() {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		StringBuilder sb = new StringBuilder();

		while (parser.position < parser.input.length()) {
			char codepoint = parser.input.charAt(parser.position);

			if (sb.length() == 0 ? Character.isJavaIdentifierStart(codepoint)
					: Character.isJavaIdentifierPart(codepoint)) {
				parser.position++;
				sb.append(codepoint);
			} else {
				break;
			}
		}

		if (sb.length() == 0) {
			parser.pushError("Expected name missing.");
			return false;
		}
		results.add(new AstName(sb.toString()));
		return true;
	}

}