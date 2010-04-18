package ca.wlu.gisql.parser;

import java.util.List;
import java.util.Set;

import ca.wlu.gisql.ast.AstName;
import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.util.Precedence;
import ca.wlu.gisql.util.ShowablePrintWriter;

/** Matches a valid Java identifier. */
public class TokenName extends Token {

	public static final Token self = new TokenName();

	protected TokenName() {
	}

	@Override
	public void addReservedWords(Set<String> reservedwords) {
	}

	@Override
	boolean parse(Parser parser, Precedence level, List<AstNode> results) {
		StringBuilder sb = new StringBuilder();

		while (parser.hasMore()) {
			char codepoint = parser.peek();

			if (codepoint == '$') {
				break;
			} else if (sb.length() == 0 ? Character
					.isJavaIdentifierStart(codepoint) : Character
					.isJavaIdentifierPart(codepoint)) {
				parser.next();
				sb.append(codepoint);
			} else {
				break;
			}
		}

		if (sb.length() == 0) {
			parser.pushError("Expected name missing.");
			return false;
		}
		String name = sb.toString();
		if (parser.isReservedWord(name)) {
			return false;
		} else {
			results.add(new AstName(name));
			return true;
		}
	}

	@Override
	public void show(ShowablePrintWriter<Object> print) {
		print.print("<identifier>");
	}

}